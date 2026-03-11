package com.nitsha.binds.bind;

import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.utils.BindExecutor;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.*;

//? if fabric {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//?} elif neoforge {
/*import net.neoforged.neoforge.common.NeoForge;
//? if >1.20.4 {
import net.neoforged.neoforge.client.event.ClientTickEvent;
//? } else {
import net.neoforged.neoforge.event.TickEvent;
//? }*/
//?} elif forge {
/*import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
*///?}

public class BindHandler {
    private static Minecraft client;
    private static final Set<Bind> activeBinds = new HashSet<>();
    private static final Map<Integer, Long> keyPressStartTime = new HashMap<>();
    private static final Set<Integer> activeKeys = new HashSet<>();

    public static void register() {
        //? if fabric {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null)
                return;
            tick();
        });
        //? } elif neoforge {
        // NeoForge.EVENT_BUS.addListener(BindHandler::onClientTick);
        //? } elif forge {
        // MinecraftForge.EVENT_BUS.addListener(BindHandler::onClientTick);
        //? }
    }

    //? if neoforge {
    //? if >1.20.4 {
    /*private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        tick();
    }*/
    //? } else {
    /*private static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        tick();
    }*/
    //? }
    //? }

    //? if forge {
    /*@SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        tick();
    }*/
    //? }

    private static void tick() {
        Map<Integer, List<Bind>> bindsByKey = new HashMap<>();
        for (Bind bind : getAllKeyBind()) {
            if (bind.keyCode == 0) continue;
            bindsByKey.computeIfAbsent(bind.keyCode, k -> new ArrayList<>()).add(bind);
        }

        for (Map.Entry<Integer, List<Bind>> entry : bindsByKey.entrySet()) {
            int key = entry.getKey();
            List<Bind> binds = entry.getValue();

            if (isKeyPressed(key)) {
                if (!activeKeys.contains(key)) {
                    activeKeys.add(key);
                    keyPressStartTime.put(key, System.currentTimeMillis());
                }
            } else {
                if (activeKeys.contains(key)) {
                    long held = System.currentTimeMillis() - keyPressStartTime.getOrDefault(key, 0L);
                    activeKeys.remove(key);
                    keyPressStartTime.remove(key);

                    Bind best = null;
                    for (Bind bind : binds) {
                        int required = "hold".equals(bind.keyMode) ? bind.holdMs : 0;
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

                    if (best != null) {
                        BindExecutor.startBind(best);
                    }
                }
            }
        }
    }

    public static List<Bind> getAllKeyBind() {
        List<Bind> result = new ArrayList<>();
        for (Preset preset : BindsStorage.presets) {
            for (Page page : preset.pages) {
                for (Bind bind : page.binds) {
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