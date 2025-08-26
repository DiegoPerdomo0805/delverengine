import com.interrupt.dungeoneer.stats.PlayerStats;
import com.interrupt.dungeoneer.entities.Player;

/**
 * Example mod demonstrating how to access realtime player statistics.
 * This file can be compiled alongside {@code delver_jar_decompiled.java}
 * to build a playable mod.
 */
public class StatsMod {
    public void onSomeEvent(Player player) {
        // Example usage: print current stats to console.
        System.out.println(PlayerStats.instance.toString());
    }
}

