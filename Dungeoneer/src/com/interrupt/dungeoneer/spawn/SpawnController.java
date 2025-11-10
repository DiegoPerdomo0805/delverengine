package com.interrupt.dungeoneer.spawn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.interrupt.dungeoneer.entities.Entity;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.entities.Monster;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.mod.ModIndex;
import com.interrupt.managers.ItemManager;
import com.interrupt.managers.MonsterManager;
import com.interrupt.utils.JsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Live-tunable spawn controller that merges base monster/item tables with runtime overrides.
 *
 * Supports global scales and simple per-biome / per-roomTag overrides. The controller exposes
 * setters that the main controller can tweak at runtime; subsequent spawn calls will reflect
 * the updated values immediately.
 */
public final class SpawnController {
    private final MonsterManager monsters;
    private final ItemManager items;
    private final Random rng;

    private volatile float enemyDensityScale = 1.0f;
    private volatile float itemDensityScale = 1.0f;
    private volatile float spawnBudgetFactor = 1.0f;

    private final Map<String, BiomeOverrides> biome = new HashMap<>();
    private final Map<String, TagOverrides> roomTags = new HashMap<>();

    /** Minimal context for spawn decisions. */
    public static final class RoomCtx {
        public final String biome;       // e.g., "TECH"
        public final String tag;         // e.g., "treasure", may be null
        public final int difficultyTier; // 1+ roughly dungeon depth

        public RoomCtx(String biome, String tag, int difficultyTier) {
            this.biome = biome;
            this.tag = tag;
            this.difficultyTier = Math.max(1, difficultyTier);
        }
    }

    /** A picked enemy that can be applied to a level in a room. */
    public static final class EnemyPick {
        public final Array<Monster> toSpawn;
        public EnemyPick(Array<Monster> toSpawn) { this.toSpawn = toSpawn; }
        public void applyTo(Level level, int centerX, int centerY) {
            if (toSpawn == null) return;
            for (Monster m : toSpawn) {
                // Try to place near the room center
                m.x = centerX + Game.rand.nextFloat() * 2f - 1f;
                m.y = centerY + Game.rand.nextFloat() * 2f - 1f;
                m.z = level.getTile((int)m.x, (int)m.y).getFloorHeight(m.x, m.y) + 0.5f;
                m.Init(level, Game.instance != null && Game.instance.player != null ? Game.instance.player.level : 1);
                level.SpawnEntity(m);
            }
        }
    }

    /** Picked items for a room. */
    public static final class ItemPick {
        public final Array<Item> toSpawn;
        public ItemPick(Array<Item> toSpawn) { this.toSpawn = toSpawn; }
        public void applyTo(Level level, int centerX, int centerY) {
            if (toSpawn == null) return;
            for (Item it : toSpawn) {
                it.x = centerX + Game.rand.nextFloat() * 2f - 1f;
                it.y = centerY + Game.rand.nextFloat() * 2f - 1f;
                it.z = level.getTile((int)it.x, (int)it.y).getFloorHeight(it.x, it.y) + 0.5f;
                level.SpawnEntity(it);
            }
        }
    }

    /** JSON data container for overrides. */
    private static final class OverridesFile {
        public Globals globals;
        public ObjectMap<String, BiomeOverrides> biomes;
        public ObjectMap<String, TagOverrides> room_tags;
    }
    private static final class Globals { public Float enemy_density_scale, item_density_scale, spawn_budget_factor; }
    private static final class BiomeOverrides {
        public ObjectMap<String, Float> enemy_weights;
        public ObjectMap<String, Float> item_weights;
        public Caps caps;
    }
    private static final class Caps { public Integer max_enemies_per_room, max_items_per_room; }
    private static final class TagOverrides {
        public Float item_density_scale, enemy_density_scale;
    }

    public SpawnController(MonsterManager monsters, ItemManager items, Random rng) {
        this.monsters = monsters;
        this.items = items;
        this.rng = rng;
    }

    public void setEnemyDensityScale(float v) { enemyDensityScale = Math.max(0f, v); }
    public void setItemDensityScale(float v) { itemDensityScale = Math.max(0f, v); }
    public void setSpawnBudgetFactor(float v) { spawnBudgetFactor = Math.max(0f, v); }

    /** Load overrides from a file. Supports JSON (recommended). */
    public void reloadOverrides(FileHandle f) {
        if (f == null || !f.exists()) return;
        try {
            // JSON structure
            OverridesFile of = JsonUtil.fromJson(OverridesFile.class, f);
            if (of != null) applyOverrides(of);
        }
        catch (Exception ex) {
            Gdx.app.error("SpawnController", "Failed parsing overrides " + f.path() + ": " + ex.getMessage());
        }
    }

    private void applyOverrides(OverridesFile of) {
        if (of.globals != null) {
            if (of.globals.enemy_density_scale != null) enemyDensityScale = of.globals.enemy_density_scale;
            if (of.globals.item_density_scale != null) itemDensityScale = of.globals.item_density_scale;
            if (of.globals.spawn_budget_factor != null) spawnBudgetFactor = of.globals.spawn_budget_factor;
        }
        biome.clear();
        roomTags.clear();
        if (of.biomes != null) {
            for (ObjectMap.Entry<String,BiomeOverrides> e : of.biomes.entries()) {
                biome.put(e.key.toUpperCase(), e.value);
            }
        }
        if (of.room_tags != null) {
            for (ObjectMap.Entry<String,TagOverrides> e : of.room_tags.entries()) {
                roomTags.put(e.key.toLowerCase(), e.value);
            }
        }
    }

    /** Pick 0..N monsters for a room. */
    public EnemyPick pickEnemy(RoomCtx room, Random random) {
        String biomeKey = safeBiome(room.biome);
        float tagEnemyScale = tagScale(room.tag, true);
        int cap = capEnemies(biomeKey, 2); // default 2

        int count = Math.max(0, Math.round(enemyDensityScale * tagEnemyScale * spawnBudgetFactor));
        count = Math.min(count, cap);

        Array<Monster> picks = new Array<>();
        for (int i = 0; i < count; i++) {
            Monster m = MonsterManager.instance != null ? MonsterManager.instance.GetRandomMonster(biomeKey) : monsters.GetRandomMonster(biomeKey);
            if (m != null) picks.add(m);
        }
        return new EnemyPick(picks);
    }

    /** Pick 0..N items for a room. */
    public ItemPick pickItems(RoomCtx room, Random random) {
        String biomeKey = safeBiome(room.biome);
        float tagItemScale = tagScale(room.tag, false);
        int cap = capItems(biomeKey, 1); // default 1

        int count = Math.max(0, Math.round(itemDensityScale * tagItemScale * spawnBudgetFactor));
        count = Math.min(count, cap);

        Array<Item> picks = new Array<>();
        for (int i = 0; i < count; i++) {
            Item it = Game.GetItemManager().GetLevelLoot(Math.max(1, room.difficultyTier));
            if (it != null) picks.add(it);
        }
        return new ItemPick(picks);
    }

    private String safeBiome(String b) { return b == null ? "DUNGEON" : b.toUpperCase(); }
    private float tagScale(String tag, boolean enemy) {
        if (tag == null) return 1f;
        TagOverrides t = roomTags.get(tag.toLowerCase());
        if (t == null) return 1f;
        Float v = enemy ? t.enemy_density_scale : t.item_density_scale;
        return v == null ? 1f : Math.max(0f, v);
    }
    private int capEnemies(String biomeKey, int def) {
        BiomeOverrides b = biome.get(biomeKey);
        return b != null && b.caps != null && b.caps.max_enemies_per_room != null ? b.caps.max_enemies_per_room : def;
    }
    private int capItems(String biomeKey, int def) {
        BiomeOverrides b = biome.get(biomeKey);
        return b != null && b.caps != null && b.caps.max_items_per_room != null ? b.caps.max_items_per_room : def;
    }

    /** Make a default controller, loading overrides from a specific mod if available. */
    public static SpawnController makeForActiveMods() {
        SpawnController sc = new SpawnController(Game.GetMonsterManager(), Game.GetItemManager(), Game.rand);
        try {
            // Prefer AIAdaptiveDelver overrides if present
            ModIndex idx = ModIndex.scan(Gdx.files.local("mods"));
            idx.findSpawnOverrides("AIAdaptiveDelver").ifPresent(sc::reloadOverrides);
        } catch (Exception ignored) { }
        return sc;
    }
}

