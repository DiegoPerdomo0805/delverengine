package com.interrupt.dungeoneer.mods.playerstats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Options;
import com.interrupt.dungeoneer.stats.PlayerStats;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Debounced, thread-safe persistence for PlayerStats.
 */
public final class PlayerStatsStore implements PlayerStats.PlayerStatsListener {
    private static final long DEBOUNCE_MS = 700L;

    private static final PlayerStatsStore INSTANCE = new PlayerStatsStore();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "player-stats-store");
        t.setDaemon(true);
        return t;
    });

    private volatile ScheduledFuture<?> pending;
    private volatile FileHandle targetFile;

    private PlayerStatsStore() {}

    public static void init(Path root) {
        INSTANCE.initInternal(root);
    }

    public static void onStatsChanged(PlayerStats stats) {
        INSTANCE.onStatsChangedInternal(stats);
    }

    public static void flush() {
        INSTANCE.flushInternal();
    }

    public static Path getFile() {
        return INSTANCE.targetFile != null ? Paths.get(INSTANCE.targetFile.file().getPath()) : null;
    }

    private synchronized void initInternal(Path root) {
        // Resolve directory under save path unless an explicit root is provided
        String baseDir;
        if (root != null) {
            baseDir = root.toString();
        } else {
            baseDir = Options.getOptionsDir() + "mods/stats";
        }
        System.out.println(baseDir);

        FileHandle dir = Game.getFile(baseDir + "/");
        if (!dir.exists()) dir.mkdirs();

        targetFile = Game.getFile(baseDir + "/player_reference.json");

        // Try to load existing snapshot and seed the singleton
        try {
            if (targetFile.exists()) {
                Snapshot snap = JsonIO.read(Snapshot.class, targetFile);
                if (snap != null) {
                    PlayerStats.instance.seed(
                            safeInt(snap.melee_kills),
                            safeInt(snap.magic_kills),
                            safeInt(snap._melee_hits),
                            safeInt(snap._magic_hits),
                            safeInt(snap._damage_inflicted),
                            safeInt(snap._damage_taken),
                            safeInt(snap.deaths)
                    );
                }
            }
        } catch (Exception ignored) { }

        // Register as listener to get change events
        PlayerStats.instance.setListener(this);
    }

    private void onStatsChangedInternal(PlayerStats stats) {
        // Debounce writes, coalesce rapid updates
        if (targetFile == null) return;

        ScheduledFuture<?> toCancel = pending;
        if (toCancel != null && !toCancel.isDone()) {
            toCancel.cancel(false);
        }
        pending = scheduler.schedule(this::writeSnapshotSafe, DEBOUNCE_MS, TimeUnit.MILLISECONDS);
    }

    private void writeSnapshotSafe() {
        try {
            Snapshot snap = Snapshot.from(PlayerStats.instance);
            JsonIO.write(snap, targetFile);
        } catch (Exception e) {
            if (Gdx.app != null) Gdx.app.log("PlayerStatsStore", "Failed to write stats: " + e.getMessage());
        }
    }

    private int safeInt(Integer v) { return v == null ? 0 : Math.max(0, v); }

    private void flushInternal() {
        if (targetFile == null) return;
        ScheduledFuture<?> p = pending;
        if (p != null && !p.isDone()) {
            // Cancel and write immediately to ensure flush
            p.cancel(false);
        }
        writeSnapshotSafe();
    }

    /** Persisted schema. Extra underscored fields keep raw counters for accuracy. */
    public static class Snapshot {
        public Integer melee_kills;
        public Integer magic_kills;
        public Float hits_per_kill_melee;
        public Float hits_per_kill_magic;
        public Float damage_taken;
        public Float damage_dealt;
        public Float damage_ratio_taken_to_dealt;
        public Integer deaths;
        public Long updated_at_unix_ms;

        // Hidden/raw counters for precise resume
        public Integer _melee_hits;
        public Integer _magic_hits;
        public Integer _damage_inflicted;
        public Integer _damage_taken;

        public static Snapshot from(PlayerStats s) {
            Snapshot snap = new Snapshot();
            snap.melee_kills = s.getMeleeKills();
            snap.magic_kills = s.getMagicKills();
            snap.hits_per_kill_melee = s.getMeleeHitsPerKill();
            snap.hits_per_kill_magic = s.getMagicHitsPerKill();
            snap.damage_taken = (float) s.getDamageReceived();
            snap.damage_dealt = (float) s.getDamageInflicted();
            snap.damage_ratio_taken_to_dealt = s.getDamageRatio();
            snap.deaths = s.getDeaths();
            snap.updated_at_unix_ms = System.currentTimeMillis();

            snap._melee_hits = s.getMeleeHits();
            snap._magic_hits = s.getMagicHits();
            snap._damage_inflicted = s.getDamageInflicted();
            snap._damage_taken = s.getDamageReceived();
            return snap;
        }
    }
}

