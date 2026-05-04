package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.AbstractButton;
import com.nitsha.binds.gui.widget.button.BedrockButton;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;*/
//? }

public class KeybindSelector extends BedrockButton {
    private static KeybindSelector focusedKeybind = null;
    private int keyCode;
    private String baseName = TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString();

    public KeybindSelector(int x, int y, int width, int height) {
        super("", x, y, width, height, true, () -> {}, 0xFFFFFFFF, 0xFF07938d, 0xFF212121, 0xFFFFFFFF);
        updateName();
    }

    public void setKeyCode(int kC) {
        this.keyCode = kC;
        this.baseName = (kC == 0)
                ? TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString()
                : InputConstants.Type.KEYSYM.getOrCreate(kC).getDisplayName().getString();
        updateName();
    }

    private void updateName() {
        int maxSymbols = (this.getWidth() / 7) - (isPressed() ? 4 : 0);
        String cName = calculateName(this.baseName, (isPressed()) ? "> " + this.baseName + " <" : this.baseName, maxSymbols);
        String fName = (isPressed()) ? "> " + cName + " <" : cName;
        this.setName(fName);
    }

    private String calculateName(String insideName, String fullName, int max) {
        int textCut = Math.min(fullName.length(), max);
        return GUIUtils.truncateString(insideName, textCut);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        updateName();
    }

    public int getKeyCode() { return this.keyCode; }

    public static KeybindSelector getFocusedField() { return focusedKeybind; }

    public static void setFocusedField(KeybindSelector fK) {
        if (focusedKeybind != null) focusedKeybind.setPressed(false);
        focusedKeybind = fK;
    }

    private static GuiEventListener lastClickedWidget = null;

    public static void setLastClickedWidget(GuiEventListener widget) {
        lastClickedWidget = widget;
    }

    public static void controlFocus() {
        if (focusedKeybind == null) return;
        if (lastClickedWidget == focusedKeybind) return;
        focusedKeybind.setFocused(false);
        focusedKeybind.setPressed(false);
        focusedKeybind = null;
    }

    @Override
    public void onPress() {
        setLastClickedWidget(this);
        setFocusedField(this);
        this.setPressed(true);
        this.setFocused(true);
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        if (focusedKeybind != null) {
            int keyCode = event.key();
            int newKey = (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) ? 0 : keyCode;
            focusedKeybind.setPressed(false);
            focusedKeybind.setKeyCode(newKey);
            KeybindSelector.setFocusedField(null);
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) return true;
        }
        return super.keyPressed(event);
    }*/
    //? } else {
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedKeybind != null) {
            int newKey = (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) ? 0 : keyCode;
            focusedKeybind.setPressed(false);
            focusedKeybind.setKeyCode(newKey);
            KeybindSelector.setFocusedField(null);
            if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    //? }
}