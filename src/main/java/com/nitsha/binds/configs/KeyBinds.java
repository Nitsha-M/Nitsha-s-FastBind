package com.nitsha.binds.configs;

import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.screen.BindsGUI;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static KeyBinding BINDS;
    public static KeyBinding PREV_PAGE;
    public static KeyBinding NEXT_PAGE;
    public static KeyBinding EDITOR;

    public static void register() {
        BINDS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nitsha.binds",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "category.nitsha.binds"
        ));

        NEXT_PAGE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nitsha.next_page",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.nitsha.binds"
        ));

        PREV_PAGE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nitsha.prev_page",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.nitsha.binds"
        ));

        EDITOR = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nitsha.editor",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.nitsha.binds"
        ));
    }

    public static void tick(MinecraftClient client) {
        if (BINDS.wasPressed()) {
            if (BindsGUI.ignoreHoldToOpenOnce) return;
            if (!(client.currentScreen instanceof BindsGUI)) {
                BindsGUI.ignoreHoldToOpenOnce = true;
                //? if >=1.17.1 {
                client.setScreen(new BindsGUI());
                //? } else {
                /*client.openScreen(new BindsGUI());*/
                //? }
            }
        }
        if (!InputUtil.isKeyPressed(client.getWindow().getHandle(), BINDS.getDefaultKey().getCode())) {
            BindsGUI.ignoreHoldToOpenOnce = false;
        }
        if (EDITOR.wasPressed()) {
            if (!(client.currentScreen instanceof BindsGUI)) {
                //? if >=1.17.1 {
                client.setScreen(new BindsEditor(client.currentScreen));
                //? } else {
                /*client.openScreen(new BindsEditor(client.currentScreen));*/
                //? }
            }
        }
    }
}
