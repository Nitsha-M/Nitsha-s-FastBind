package com.nitsha.binds.configs;

import com.nitsha.binds.utils.BindExecutor;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.*;

//? if fabric {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//?} elif neoforge {
/*import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
*///?} elif forge {
/*import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
*///?}

public class BindHandler {
    private static final Minecraft client = Minecraft.getInstance();
    private static final Set<Bind> activeBinds = new HashSet<>();

    public static void register() {
        //? if fabric {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null) return;
            tick();
        });
        //?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(BindHandler::onClientTick);
         *///?} elif forge {
        /*MinecraftForge.EVENT_BUS.register(new BindHandler());
         *///?}
    }

    //? if neoforge {
    /*private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        tick();
    }
    *///?}

    //? if forge {
    /*@SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        tick();
    }
    *///?}

    private static void tick() {
        for (Bind bind : getAllKeyBind()) {
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
        long handle = client.getWindow().getWindow();
        return GLFW.glfwGetKey(handle, keyCode) == GLFW.GLFW_PRESS;
    }
}