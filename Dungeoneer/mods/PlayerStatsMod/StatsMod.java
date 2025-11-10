import com.interrupt.dungeoneer.stats.PlayerStats;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.mods.playerstats.PlayerStatsStore;

/**
 * Example mod demonstrating how to access realtime player statistics.
 * This file can be compiled alongside {@code delver_jar_decompiled.java}
 * to build a playable mod.
 */
public class StatsMod {
    /** Quick helper to print current stats and the JSON path. */
    public static void printStats() {
        System.out.println("[StatsMod] " + PlayerStats.instance.toString());
        java.nio.file.Path p = PlayerStatsStore.getFile();
        if (p != null) System.out.println("[StatsMod] File: " + p.toString());
    }

    public void onSomeEvent(Player player) {
        // Example usage: print current stats to console.
        printStats();
    }
}

