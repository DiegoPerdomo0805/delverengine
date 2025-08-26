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
    }

    /** Records damage received by the player. */
    public void recordDamageReceived(int damage) {
        if (damage > 0) damageReceived += damage;
    }

    /** Records a player death. */
    public void recordDeath() {
        deaths++;
    }

    public int getMeleeKills() { return meleeKills; }
    public int getMagicKills() { return magicKills; }
    public float getMeleeHitsPerKill() { return meleeKills == 0 ? 0f : (float) meleeHits / meleeKills; }
    public float getMagicHitsPerKill() { return magicKills == 0 ? 0f : (float) magicHits / magicKills; }
    public float getDamageRatio() { return damageInflicted == 0 ? 0f : (float) damageReceived / damageInflicted; }
    public int getDeaths() { return deaths; }

    @Override
    public String toString() {
        return String.format(
                "Melee kills: %d, Magic kills: %d, Melee hits/kill: %.2f, Magic hits/kill: %.2f, Damage ratio: %.2f, Deaths: %d",
                meleeKills, magicKills, getMeleeHitsPerKill(), getMagicHitsPerKill(), getDamageRatio(), deaths);
    }
}

