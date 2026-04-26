package com.nitsha.binds.gui.widget;

import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.button.BedrockButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

//? if >=1.21.9 {
/*import net.minecraft.client.input.KeyEvent;*/
//? }

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;*/
//? }

public class MainKeybindSelector extends BedrockButton {
    private static MainKeybindSelector focusedKeybind = null;

    private String baseName = TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString();
    private int keyCode;

    public MainKeybindSelector(int x, int y, int width, int height) {
        super(TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString(),
                x, y, width, height, true, () -> {}, 0xFFFFFFFF, 0xFF07938d, 0xFF212121, 0xFFFFFFFF);
        this.setButtonDirection("_left");
    }

    public void setKeyCode(int kC) {
        this.keyCode = kC;
        this.baseName = (kC == 0)
                ? TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString()
                : InputConstants.Type.KEYSYM.getOrCreate(kC).getDisplayName().getString();
    }

    public int getKeyCode() { return this.keyCode; }

    public static MainKeybindSelector getFocusedField() { return focusedKeybind; }

    public static void setFocusedField(MainKeybindSelector fK) {
        if (focusedKeybind != null) focusedKeybind.setPressed(false);
        focusedKeybind = fK;
    }

    @Override
    public void onPress() {
        setLastClickedWidget(this);
        setFocusedField(this);
        this.setPressed(true); 
        this.setFocused(true);
    }

    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int maxSymbols = (this.getWidth() / 7) - (isPressed() ? 4 : 0);
        String displayName = (isPressed()) ? "> " + GUIUtils.truncateString(baseName, maxSymbols) + " <" : GUIUtils.truncateString(baseName, maxSymbols);

        int btnColor = isPressed() ? 0xFF07938d : 0xFFFFFFFF;
        int textColor = isPressed() ? 0xFFFFFFFF : 0xFF212121;

        this.setName(displayName);
        this.setColors(btnColor, 0xFF07938d, textColor, 0xFFFFFFFF);

        super.renderWidget(ctx, mouseX, mouseY, delta);
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
    //? if >=1.21.9 {
    /*public boolean keyPressed(KeyEvent event) {
        if (focusedKeybind != null) {
            int keyCode = event.key();
            int newKey = (keyCode == GLFW.GLFW_KEY_ESCAPE) ? 0 : keyCode;
            focusedKeybind.setPressed(false);
            focusedKeybind.setKeyCode(newKey);
            MainKeybindSelector.setFocusedField(null);
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) return true;
        }
        return super.keyPressed(event);
    }*/
    //? } else {
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedKeybind != null) {
            int newKey = (keyCode == GLFW.GLFW_KEY_ESCAPE) ? 0 : keyCode;
            focusedKeybind.setPressed(false);
            focusedKeybind.setKeyCode(newKey);
            MainKeybindSelector.setFocusedField(null);
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    //? }
}