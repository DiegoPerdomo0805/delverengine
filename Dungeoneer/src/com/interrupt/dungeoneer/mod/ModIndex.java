package com.interrupt.dungeoneer.mod;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.interrupt.dungeoneer.game.Game;

import java.util.Optional;

/**
 * Lightweight mod index and helper utilities for generator discovery and data lookups.
 *
 * This scans the active mod roots (via Game.modManager) to find generator definitions
 * and locate optional spawn override files in a mod's data folder.
 */
public final class ModIndex {
    private final Array<GeneratorDescriptor> generators = new Array<>();

    private ModIndex() {}

    /** Scan all active mods for generator definitions under generator/*/info.dat and section.dat. */
    public static ModIndex scan(FileHandle modsRoot) {
        ModIndex idx = new ModIndex();

        // Walk each active mod and look for generator folders
        Array<String> mods = Game.modManager.getAllMods();
        for (String modPath : mods) {
            FileHandle genRoot = Game.getInternal(modPath + "/generator");
            if (!genRoot.exists() || !genRoot.isDirectory()) continue;

            for (FileHandle child : Game.listDirectory(genRoot)) {
                if (!child.isDirectory()) continue;
                FileHandle info = child.child("info.dat");
                FileHandle section = child.child("section.dat");
                if (info.exists()) {
                    String folderName = child.name();
                    String modId = new FileHandle(modPath).name();
                    String generatorId = folderName.toUpperCase();
                    String name = folderName;
                    idx.generators.add(new GeneratorDescriptor(modId, generatorId, name, info, section.exists() ? section : null));
                }
            }
        }
        return idx;
    }

    /** Return discovered generators. */
    public Array<GeneratorDescriptor> getGenerators() { return generators; }

    /**
     * Try to locate a spawn overrides data file for the given mod id.
     * Accepts either JSON (spawn_overrides.json) or DAT (spawn_overrides.dat) formats.
     */
    public Optional<FileHandle> findSpawnOverrides(String modId) {
        ArrayMap<String, String> candidates = new ArrayMap<>();
        candidates.put("json", "data/spawn_overrides.json");
        candidates.put("dat", "data/spawn_overrides.dat");

        for (int i = 0; i < candidates.size; i++) {
            String rel = candidates.getValueAt(i);
            FileHandle f = Game.getInternal("mods/" + modId + "/" + rel);
            if (f.exists()) return Optional.of(f);
        }

        // Also allow overrides bundled in other active mods (last one wins)
        for (String modPath : Game.modManager.getAllMods()) {
            for (int i = 0; i < candidates.size; i++) {
                FileHandle f = Game.getInternal(modPath + "/" + candidates.getValueAt(i));
                if (f.exists()) return Optional.of(f);
            }
        }

        return Optional.empty();
    }

    /** Descriptor for a discovered generator. */
    public static final class GeneratorDescriptor {
        public final String modId;
        public final String generatorId;
        public final String name;
        public final FileHandle infoDat;
        public final FileHandle sectionDat; // may be null

        public GeneratorDescriptor(String modId, String generatorId, String name, FileHandle infoDat, FileHandle sectionDat) {
            this.modId = modId;
            this.generatorId = generatorId;
            this.name = name;
            this.infoDat = infoDat;
            this.sectionDat = sectionDat;
        }
    }
}

