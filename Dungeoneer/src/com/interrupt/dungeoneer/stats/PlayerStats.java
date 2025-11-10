package com.interrupt.dungeoneer.stats;

import com.interrupt.dungeoneer.entities.items.Weapon.DamageType;

/**
 * Tracks realtime player combat statistics.
 */
public class PlayerStats {
    private int meleeKills = 0;
    private int magicKills = 0;
    private int meleeHits = 0;
    private int magicHits = 0;
    private int damageInflicted = 0;
    private int damageReceived = 0;
    private int deaths = 0;

    public interface PlayerStatsListener {
        void onStatsChanged(PlayerStats stats);
    }

    private transient volatile PlayerStatsListener listener;

    public static final PlayerStats instance = new PlayerStats();

    private PlayerStats() {}

    /** Records a hit dealt by the player. */
    public void recordHit(DamageType damageType, int damage, boolean killed) {
        if (damageType == DamageType.PHYSICAL) {
            meleeHits++;
            if (killed) meleeKills++;
        }
        else {
            magicHits++;
            if (killed) magicKills++;
        }
        if (damage > 0) damageInflicted += damage;
        notifyChanged();
    }

    /** Records damage received by the player. */
    public void recordDamageReceived(int damage) {
        if (damage > 0) damageReceived += damage;
        notifyChanged();
    }

    /** Records a player death. */
    public void recordDeath() {
        deaths++;
        notifyChanged();
    }

    public int getMeleeKills() { return meleeKills; }
    public int getMagicKills() { return magicKills; }
    public float getMeleeHitsPerKill() { return meleeKills == 0 ? 0f : (float) meleeHits / meleeKills; }
    public float getMagicHitsPerKill() { return magicKills == 0 ? 0f : (float) magicHits / magicKills; }
    public float getDamageRatio() { return damageInflicted == 0 ? 0f : (float) damageReceived / damageInflicted; }
    public int getDeaths() { return deaths; }

    // Additional getters for serialization
    public int getMeleeHits() { return meleeHits; }
    public int getMagicHits() { return magicHits; }
    public int getDamageInflicted() { return damageInflicted; }
    public int getDamageReceived() { return damageReceived; }

    /** Install a listener notified when stats mutate. */
    public void setListener(PlayerStatsListener l) { this.listener = l; }

    private void notifyChanged() {
        PlayerStatsListener l = this.listener;
        if (l != null) {
            try {
                l.onStatsChanged(this);
            } catch (Throwable ignored) { }
        }
    }

    /** Seed counters from a persisted snapshot. */
    public void seed(int meleeKills, int magicKills, int meleeHits, int magicHits, int damageInflicted, int damageReceived, int deaths) {
        this.meleeKills = Math.max(0, meleeKills);
        this.magicKills = Math.max(0, magicKills);
        this.meleeHits = Math.max(0, meleeHits);
        this.magicHits = Math.max(0, magicHits);
        this.damageInflicted = Math.max(0, damageInflicted);
        this.damageReceived = Math.max(0, damageReceived);
        this.deaths = Math.max(0, deaths);
        notifyChanged();
    }

    @Override
    public String toString() {
        return String.format(
                "Melee kills: %d, Magic kills: %d, Melee hits/kill: %.2f, Magic hits/kill: %.2f, Damage ratio: %.2f, Deaths: %d",
                meleeKills, magicKills, getMeleeHitsPerKill(), getMagicHitsPerKill(), getDamageRatio(), deaths);
    }
}

