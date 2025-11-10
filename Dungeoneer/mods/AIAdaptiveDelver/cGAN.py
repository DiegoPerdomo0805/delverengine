# ===========
# 1) Imports
# ===========
import math
import json
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader, TensorDataset, random_split
import numpy as np
torch.manual_seed(42)

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
# print(device)

# =========================================================
# 2) Data schema & normalization helpers (plug real data!)
# =========================================================
# Feature order (6D):
# 0 melee_kill_ratio ∈ [0,1]
# 1 magic_kill_ratio ∈ [0,1]   (usually melee + magic ≈ 1, but not enforced)
# 2 hits_per_kill_melee ∈ [0.5, 6] (example plausible range)
# 3 hits_per_kill_magic ∈ [0.3, 6]
# 4 dmg_taken_over_dealt ∈ [0, 2]  ( <1 means player deals more than takes )
# 5 death_rate ∈ [0, 1]            (deaths per encounter or normalized)

FEATURE_MINS = torch.tensor([0.0, 0.0, 0.5, 0.3, 0.0, 0.0], dtype=torch.float32, device=device)
FEATURE_MAXS = torch.tensor([1.0, 1.0, 6.0, 6.0, 2.0, 1.0], dtype=torch.float32, device=device)

def normalize(x):
    # x: (...,6) raw features
    return (x - FEATURE_MINS) / (FEATURE_MAXS - FEATURE_MINS + 1e-8)

def denormalize(x):
    return x*(FEATURE_MAXS - FEATURE_MINS) + FEATURE_MINS

# ---------------------------------------------------------
# Synthetic training set (REPLACE with your logged metrics)
# ---------------------------------------------------------
def sample_player_metrics(n=3000):
    # Make a broad synthetic distribution that covers plausible ranges.
    melee_ratio = torch.clamp(torch.rand(n, device=device) *1.0, 0, 1)
    magic_ratio = torch.clamp(1.0 - melee_ratio + 0.1*torch.randn(n, device=device) , 0, 1)
    # Not strictly summing to 1; that’s okay for generality.
    hpk_melee = torch.clamp(torch.randn(n, device=device) *0.8 + 2.2, 0.5, 6.0)
    hpk_magic = torch.clamp(torch.randn(n, device=device) *0.7 + 1.6, 0.3, 6.0)
    dmg_ratio = torch.clamp(torch.randn(n, device=device) *0.35 + 0.8, 0.0, 2.0)
    death_rate = torch.clamp(torch.randn(n, device=device) *0.15 + 0.25, 0.0, 1.0)
    X = torch.stack([melee_ratio, magic_ratio, hpk_melee, hpk_magic, dmg_ratio, death_rate], dim=1)
    return X

raw_X = sample_player_metrics(4000)
X = normalize(raw_X)
X = X.to(device)

# ============================================
# 3) Simple torch KMeans to discover archetypes
# ============================================
def kmeans_torch(data, k=3, iters=50):
    # data: (N, D)
    N, D = data.shape
    # init centers by random picks
    idx = torch.randperm(N)[:k]
    centers = data[idx].clone()

    for _ in range(iters):
        # assign
        dists = torch.cdist(data, centers)  # (N,k)
        labels = torch.argmin(dists, dim=1) # (N,)
        # update
        new_centers = torch.stack([data[labels==i].mean(0) if (labels==i).any() else centers[i]
                                   for i in range(k)], dim=0)
        if torch.allclose(new_centers, centers, atol=1e-4):
            break
        centers = new_centers
    return centers, labels

K = 3  # choose your initial number of archetypes (you can tune this later)
centers, cluster_labels = kmeans_torch(X, k=K, iters=60)
y = cluster_labels  # discovered archetype ids (0..K-1)

# One-hot for conditions
def one_hot(y, num_classes):
    out = torch.zeros(y.size(0), num_classes, device=y.device)
    out[torch.arange(y.size(0), device=y.device), y] = 1.0
    return out

Y_cond = one_hot(y, K)

# =====================================
# 4) Archetype classifier (metrics -> y)
# =====================================
class ArchetypeClassifier(nn.Module):
    def __init__(self, in_dim=6, hidden=64, num_classes=3):
        super().__init__()
        self.net = nn.Sequential(
            nn.Linear(in_dim, hidden),
            nn.ReLU(),
            nn.Linear(hidden, hidden//2),
            nn.ReLU(),
            nn.Linear(hidden//2, num_classes)
        )

    def forward(self, x):
        return self.net(x)  # logits

clf = ArchetypeClassifier(num_classes=K).to(device)
crit_clf = nn.CrossEntropyLoss()
opt_clf = optim.Adam(clf.parameters(), lr=1e-3)

dataset = TensorDataset(X, y)
train_sz = int(0.8*len(dataset))
train_set, val_set = random_split(dataset, [train_sz, len(dataset)-train_sz],
                                  generator=torch.Generator().manual_seed(42))
train_loader = DataLoader(train_set, batch_size=128, shuffle=True)
val_loader   = DataLoader(val_set, batch_size=256, shuffle=False)

for epoch in range(30):
    clf.train()
    total = 0.0
    for xb, yb in train_loader:
        xb, yb = xb.to(device), yb.to(device)
        logits = clf(xb)
        loss = crit_clf(logits, yb)
        opt_clf.zero_grad(); loss.backward(); opt_clf.step()
        total += loss.item()*xb.size(0)
    if epoch % 5 == 0 or epoch == 29:
        clf.eval()
        with torch.no_grad():
            correct, count = 0, 0
            for xb, yb in val_loader:
                xb, yb = xb.to(device), yb.to(device)
                pred = clf(xb).argmax(1)
                correct += (pred==yb).sum().item()
                count += yb.numel()
        print(f"[CLF] Epoch {epoch:02d} | loss {total/train_sz:.4f} | acc {100*correct/count:.1f}%")

# ======================================================
# 5) cGAN: Generator & Discriminator (conditioned on y)
# ======================================================
# We’ll generate two groups of outputs:
#   Monster stats: HP, damage, attack_speed, aggro_radius
#   Item stats: rarity, power, type_id (discrete, but we’ll predict a continuous and round later)
#
# We bound each with Sigmoid and post-scale to practical ranges.

MONSTER_DIM = 4
ITEM_DIM    = 3
OUT_DIM     = MONSTER_DIM + ITEM_DIM

def scale_monster_item(gen_out):
    """
    gen_out ∈ [0,1]^(OUT_DIM)
    Returns dict with physically-bounded stats.
    """
    # Split
    m = gen_out[..., :MONSTER_DIM]
    it = gen_out[..., MONSTER_DIM:]

    # Monster bounds (tune to your game balance)
    # HP: [40, 500], damage: [2, 40], attack_speed: [0.4, 2.5], aggro_radius: [1.5, 12]
    HP_min, HP_max = 40.0, 500.0
    DMG_min, DMG_max = 2.0, 40.0
    AS_min, AS_max = 0.4, 2.5
    AR_min, AR_max = 1.5, 12.0

    HP   = (HP_min + (HP_max-HP_min) * m[...,0]).round()
    DMG  = (DMG_min + (DMG_max-DMG_min) * m[...,1])
    ASpd = (AS_min + (AS_max-AS_min) * m[...,2])
    AR   = (AR_min + (AR_max-AR_min) * m[...,3])

    # Item bounds
    # rarity: [0,1], power: [5,100], type_id: 0..9
    rarity = it[...,0]                          # 0..1
    power  = 5.0 + 95.0 * it[...,1]             # 5..100
    type_id = torch.clamp((it[...,2]*10).round(), 0, 9).to(torch.int64)

    return {
        "monster": {
            "hp": HP,
            "damage": DMG,
            "attack_speed": ASpd,
            "aggro_radius": AR
        },
        "item": {
            "rarity": rarity,
            "power": power,
            "type_id": type_id
        }
    }

class Generator(nn.Module):
    def __init__(self, noise_dim=16, cond_dim=3, out_dim=OUT_DIM, hidden=128):
        super().__init__()
        self.net = nn.Sequential(
            nn.Linear(noise_dim + cond_dim, hidden),
            nn.ReLU(),
            nn.Linear(hidden, hidden),
            nn.ReLU(),
            nn.Linear(hidden, out_dim),
            nn.Sigmoid()   # bound to [0,1]
        )
    def forward(self, z, y_onehot):
        zc = torch.cat([z, y_onehot], dim=1)
        return self.net(zc)

class Discriminator(nn.Module):
    def __init__(self, in_dim=OUT_DIM, cond_dim=3, hidden=128):
        super().__init__()
        self.net = nn.Sequential(
            nn.Linear(in_dim + cond_dim, hidden),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Linear(hidden, hidden//2),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Linear(hidden//2, 1),
            nn.Sigmoid()
        )
    def forward(self, x, y_onehot):
        xc = torch.cat([x, y_onehot], dim=1)
        return self.net(xc)

G = Generator(cond_dim=K).to(device)
D = Discriminator(cond_dim=K).to(device)

# ---------------------------------------------------------
# 6) Build a paired (cond, target) dataset for the cGAN
# ---------------------------------------------------------
# Since we don’t have “real” monster/item pairs yet, we’ll synthesize a
# "teacher" target via simple, archetype-biased heuristics to bootstrap training.
# Replace TARGET_SAMPLER with your logged content distribution when available.

def TARGET_SAMPLER(archetype_ids):
    """
    Quick bootstrap heuristic:
    - Archetype 0 (e.g., 'Knight'): favor higher HP enemies, moderate damage, lower attack speed; items: defense/power
    - Archetype 1 (e.g., 'Berserker'): favor higher damage & attack speed; items: high power
    - Archetype 2 (e.g., 'Spell Sniper'): lower HP enemies but bigger aggro radius; items: higher rarity
    Output is in [0,1]^OUT_DIM (because the D expects same scale as G output).
    """
    n = archetype_ids.size(0)
    base = torch.rand(n, OUT_DIM, device=archetype_ids.device)*0.2 + 0.4  # smear near middle

    for i in range(n):
        a = archetype_ids[i].item()
        if a == 0:
            # Knight-leaning enemies
            base[i,0] = 0.8  # HP high
            base[i,1] = 0.55 # damage mid
            base[i,2] = 0.35 # attack speed lower
            base[i,3] = 0.45 # aggro mid
            # Items
            base[i,4] = 0.45 # rarity mid
            base[i,5] = 0.6  # power good
            base[i,6] = 0.3  # type bucket around 3
        elif a == 1:
            # Berserker
            base[i,0] = 0.55 # HP mid
            base[i,1] = 0.85 # damage high
            base[i,2] = 0.8  # attack speed high
            base[i,3] = 0.55 # aggro mid+
            base[i,4] = 0.35 # rarity lower
            base[i,5] = 0.85 # power high
            base[i,6] = 0.6  # type bucket around 6
        else:
            # Spell Sniper
            base[i,0] = 0.35 # HP lower
            base[i,1] = 0.55 # damage mid
            base[i,2] = 0.55 # attack speed mid
            base[i,3] = 0.85 # aggro larger
            base[i,4] = 0.85 # rarity higher
            base[i,5] = 0.55 # power mid
            base[i,6] = 0.2  # type bucket around 2
    # add small noise for diversity
    base += 0.05*torch.randn_like(base)
    return torch.clamp(base, 0.0, 1.0)

# Build cond set from discovered archetypes
cond_oh = one_hot(y, K)
targets = TARGET_SAMPLER(y)

cgan_ds = TensorDataset(cond_oh, targets)
cgan_loader = DataLoader(cgan_ds, batch_size=256, shuffle=True)

# ======================================
# 7) Train cGAN (non-saturating GAN loss)
# ======================================
bce = nn.BCELoss()
opt_G = optim.Adam(G.parameters(), lr=2e-4, betas=(0.5, 0.999))
opt_D = optim.Adam(D.parameters(), lr=2e-4, betas=(0.5, 0.999))

def train_cgan(epochs=100, noise_dim=16):
    for ep in range(epochs):
        G.train(); D.train()
        for cond_batch, real_batch in cgan_loader:
            cond_batch = cond_batch.to(device)
            real_batch = real_batch.to(device)

            bs = cond_batch.size(0)
            real_lbl = torch.ones(bs, 1, device=device)
            fake_lbl = torch.zeros(bs, 1, device=device)

            # --- Train D ---
            z = torch.randn(bs, noise_dim, device=device)
            fake = G(z, cond_batch).detach()
            D_real = D(real_batch, cond_batch)
            D_fake = D(fake, cond_batch)
            loss_D = bce(D_real, real_lbl) + bce(D_fake, fake_lbl)

            opt_D.zero_grad(); loss_D.backward(); opt_D.step()

            # --- Train G ---
            z = torch.randn(bs, noise_dim, device=device)
            gen = G(z, cond_batch)
            D_gen = D(gen, cond_batch)
            # Non-saturating: maximize log(D(G(z)))  => minimize BCE with real labels
            loss_G = bce(D_gen, real_lbl)

            opt_G.zero_grad(); loss_G.backward(); opt_G.step()

        if ep % 10 == 0 or ep == epochs-1:
            print(f"[cGAN] Epoch {ep:03d} | D {loss_D.item():.4f} | G {loss_G.item():.4f}")

train_cgan(epochs=80)

# ============================================================
# 8) End-to-end: classify player -> generate content (monster,
#    item) aligned to that archetype
# ============================================================
@torch.no_grad()
def classify_archetype(raw_metrics_batch):
    """
    raw_metrics_batch: tensor of shape (B,6) with unnormalized real stats
    returns: archetype ids, one-hot vectors
    """
    x = normalize(raw_metrics_batch.to(device))
    logits = clf(x)
    yhat = logits.argmax(1)
    return yhat, one_hot(yhat, K)

@torch.no_grad()
def generate_content_from_metrics(raw_metrics_batch, n_samples_per_player=1, noise_dim=16):
    """
    For each player metrics row, classify archetype and generate n samples.
    Returns: list of dicts with monster/item stats
    """
    yhat, yhat_oh = classify_archetype(raw_metrics_batch)
    results = []
    for i in range(raw_metrics_batch.size(0)):
        entries = []
        for _ in range(n_samples_per_player):
            z = torch.randn(1, noise_dim, device=device)
            out01 = G(z, yhat_oh[i:i+1])
            scaled = scale_monster_item(out01.squeeze(0).cpu())
            # Cast to plain Python types for JSONability
            entry = {
                "archetype_id": int(yhat[i].item()),
                "monster": {
                    k: (float(v.item()) if torch.is_tensor(v) else float(v)) for k, v in scaled["monster"].items()
                },
                "item": {
                    "rarity": float(scaled["item"]["rarity"].item()),
                    "power": float(scaled["item"]["power"].item()),
                    "type_id": int(scaled["item"]["type_id"].item())
                }
            }
            entries.append(entry)
        results.append(entries)
    return results

# ------------------
# Quick demonstration
# ------------------
demo_metrics = torch.tensor([
    # melee, magic, hpk_melee, hpk_magic, dmg_taken/dealt, death_rate
    [0.85, 0.10, 1.8, 0.7, 0.6, 0.15],  # likely "Knight-like"
    [0.90, 0.05, 2.6, 0.5, 1.1, 0.35],  # likely "Berserker-like"
    [0.15, 0.80, 0.8, 1.7, 0.7, 0.12],  # likely "Spell Sniper-like"
], dtype=torch.float32, device=device)

samples = generate_content_from_metrics(demo_metrics, n_samples_per_player=2)
print(json.dumps(samples, indent=2))