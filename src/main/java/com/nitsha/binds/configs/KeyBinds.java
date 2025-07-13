package com.nitsha.binds.configs;

import com.nitsha.binds.gui.BindsGUI;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static KeyBinding BINDS;
    public static KeyBinding PREV_PAGE;
    public static KeyBinding NEXT_PAGE;

    public static void register() {
        BINDS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nitsha.binds",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "category.nitsha.binds"
        ));

        PREV_PAGE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nitsha.prev_page",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.nitsha.binds"
        ));

        NEXT_PAGE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nitsha.next_page",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.nitsha.binds"
        ));
    }

    public static void tick(MinecraftClient client) {
            if (BINDS.wasPressed()) {
                if (!(client.currentScreen instanceof BindsGUI)) {
                    client.setScreen(new BindsGUI());
                }
            }
    }
}
