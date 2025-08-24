package com.nitsha.binds.configs;

import com.nitsha.binds.utils.BindExecutor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class BindHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Set<BindEntry> activeBinds = new HashSet<>();

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null) return;

            for (BindEntry bind : getAllKeyBind()) {
                int key = bind.keyCode;
                if (key == 0) continue;

                if (isKeyPressed(key)) {
                    if (!activeBinds.contains(bind)) {
                        activeBinds.add(bind);
                        BindExecutor.startBind(bind);
                    }
                } else {
                    activeBinds.remove(bind);
                }
            }
        });
    }

    public static List<BindEntry> getAllKeyBind() {
        List<BindEntry> result = new ArrayList<>();
        for (List<BindEntry> preset : BindsConfig.presets) {
            for (BindEntry bind : preset) {
                if (bind != null && bind.keyCode != 0) {
                    result.add(bind);
                }
            }
        }
        return result;
    }

    private static boolean isKeyPressed(int keyCode) {
        long handle = client.getWindow().getHandle();
        return GLFW.glfwGetKey(handle, keyCode) == GLFW.GLFW_PRESS;
    }
}
