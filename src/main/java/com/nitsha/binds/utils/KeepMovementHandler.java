package com.nitsha.binds.utils;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.gui.screen.BindsGUI;
import com.nitsha.binds.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
//? if >=1.21.9 {
import com.mojang.blaze3d.platform.Window;
//? }

public class KeepMovementHandler {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void tick() {
        //? >=26.3 {
         if (mc.player == null || mc.gui.screen() == null) return;
        //? } else {
        /*if (mc.player == null || mc.screen == null) return;
        *///? }

        if (!shouldKeepMovement()) return;

        updateKey(mc.options.keyUp);
        updateKey(mc.options.keyDown);
        updateKey(mc.options.keyLeft);
        updateKey(mc.options.keyRight);
        updateKey(mc.options.keyJump);
        updateKey(mc.options.keySprint);

        updateKey(mc.options.keyShift);
    }

    public static boolean shouldKeepMovement() {
        //? >=26.3 {
         return (mc.gui.screen() instanceof BindsGUI) && Storage.options.keepMovement;
        //? } else {
        /*return (mc.screen instanceof BindsGUI) && Storage.options.keepMovement;
        *///? }
    }

    private static void updateKey(KeyMapping keyMapping) {
        //? if >=1.21.9 {
        Window window = mc.getWindow();
        //? } else {
        /*long window = mc.getWindow().getWindow();
        *///? }
        InputConstants.Key key = ((KeyMappingAccessor) keyMapping).nitsha$getKey();

        //$ if >=26.3 'if (key.getType() != InputConstants.Type.KEYBOARD) return;' else 'if (key.getType() != InputConstants.Type.KEYSYM) return;'
        if (key.getType() != InputConstants.Type.KEYBOARD) return;

        int keyCode = key.getValue();
        if (keyCode == InputConstants.UNKNOWN.getValue())
            return;

        //$ if >=26.3 'boolean isPressed = InputConstants.isKeyDown(keyCode);' else 'boolean isPressed = InputConstants.isKeyDown(window, keyCode);'
        boolean isPressed = InputConstants.isKeyDown(keyCode);

        keyMapping.setDown(isPressed);
        KeyMapping.set(key, isPressed);
    }
}