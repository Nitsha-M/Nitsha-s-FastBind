package com.nitsha.binds.bind;

import com.nitsha.binds.FBLogger;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.preset.BindData;
import com.nitsha.binds.configs.dto.preset.PageData;
import com.nitsha.binds.configs.dto.preset.PresetData;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import java.util.*;

public class BindHandler {
    private static Minecraft client;

    private static final Map<Integer, Long> keyPressStartTime = new HashMap<>();
    private static final Set<Integer> activeKeys = new HashSet<>();

    private static Map<Integer, List<BindData>> bindsByKeyCache = null;

    private static Map<Integer, List<BindData>> getBindsByKey() {
        if (bindsByKeyCache != null) return bindsByKeyCache;

        FBLogger.info("Loading all binds with key shortcut...");

        bindsByKeyCache = new HashMap<>();
        for (BindData bind : getAllKeyBind()) {
            if (bind.keyCode == 0) continue;
            bindsByKeyCache.computeIfAbsent(bind.keyCode, k -> new ArrayList<>()).add(bind);
        }
        FBLogger.info("Successfully loaded " + bindsByKeyCache.size() + " key binds.");
        return bindsByKeyCache;
    }

    public static void invalidateCache() {
        bindsByKeyCache = null;
    }

    public static void tick() {
        Map<Integer, List<BindData>> bindsByKey = getBindsByKey();

        for (Map.Entry<Integer, List<BindData>> entry : bindsByKey.entrySet()) {
            int key = entry.getKey();
            List<BindData> binds = entry.getValue();

            if (isKeyPressed(key)) {
                if (!activeKeys.contains(key)) {
                    activeKeys.add(key);
                    keyPressStartTime.put(key, System.currentTimeMillis());
                }
            } else {
                if (activeKeys.contains(key)) {
                    long held = System.currentTimeMillis() - keyPressStartTime.getOrDefault(key, 0L);
                    FBLogger.info("Key released: " + key + ", held: " + held + "ms");
                    activeKeys.remove(key);
                    keyPressStartTime.remove(key);

                    BindData best = null;
                    for (BindData bind : binds) {
                        int required = "hold".equals(bind.keyMode) ? bind.holdMs : 0;
                        FBLogger.info("Checking bind: " + bind.name + ", keyMode=" + bind.keyMode + ", required=" + required + ", held=" + held);
                        if (held >= required) {
                            if (best == null) {
                                best = bind;
                            } else {
                                int bestRequired = "hold".equals(best.keyMode) ? best.holdMs : 0;
                                if (required > bestRequired) {
                                    best = bind;
                                }
                            }
                        }
                    }

                    FBLogger.info("Best bind selected: " + (best != null ? best.name : "null"));

                    if (best != null) {
                        BindExecutor.startBind(best);
                    }
                }
            }
        }
    }

    public static List<BindData> getAllKeyBind() {
        List<BindData> result = new ArrayList<>();
        for (PresetData preset : Storage.PRESET_REGISTRY.values()) {
            for (PageData page : preset.pages) {
                for (BindData bind : page.binds) {
                    if (bind != null && bind.keyCode != 0) {
                        result.add(bind);
                    }
                }
            }
        }
        return result;
    }

    private static boolean isKeyPressed(int keyCode) {
        if (client == null) client = Minecraft.getInstance();
        //? if <1.21.9 {
        long handle = client.getWindow().getWindow();
        //? } else {
        //long handle = client.getWindow().handle();
        //? }
        return GLFW.glfwGetKey(handle, keyCode) == GLFW.GLFW_PRESS;
    }
}