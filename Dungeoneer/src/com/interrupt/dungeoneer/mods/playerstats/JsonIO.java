package com.interrupt.dungeoneer.mods.playerstats;

import com.badlogic.gdx.files.FileHandle;
import com.interrupt.utils.JsonUtil;

/** Minimal wrapper around the engine JsonUtil to keep mod code tidy. */
public final class JsonIO {
    private JsonIO() {}

    public static void write(Object obj, FileHandle file) {
        JsonUtil.toJson(obj, file);
    }

    public static <T> T read(Class<T> type, FileHandle file) {
        return JsonUtil.fromJson(type, file);
    }
}

