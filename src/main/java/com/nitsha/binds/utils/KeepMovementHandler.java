package com.nitsha.binds.utils;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.gui.screen.BindsGUI;
import com.nitsha.binds.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
//? if >=1.21.9 {
/*import com.mojang.blaze3d.platform.Window;*/
//? }

public class KeepMovementHandler {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void tick() {
        if (mc.player == null || mc.screen == null) return;

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
        return (mc.screen instanceof BindsGUI) && BindsStorage.getBooleanConfig("keepMovement", false);
    }

    private static void updateKey(KeyMapping keyMapping) {
        //? if >=1.21.9 {
        /*Window window = mc.getWindow();*/
        //? } else {
        long window = mc.getWindow().getWindow();
        //? }
        InputConstants.Key key = ((KeyMappingAccessor) keyMapping).nitsha$getKey();

        if (key.getType() != InputConstants.Type.KEYSYM)
            return;

        int keyCode = key.getValue();
        if (keyCode == InputConstants.UNKNOWN.getValue())
            return;

        boolean isPressed = InputConstants.isKeyDown(window, keyCode);

        keyMapping.setDown(isPressed);
        KeyMapping.set(key, isPressed);
    }
}