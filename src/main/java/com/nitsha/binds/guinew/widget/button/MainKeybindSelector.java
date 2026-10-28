package com.nitsha.binds.guinew.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.multiversions.NitshaInput;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;

public class MainKeybindSelector extends BedrockButton {
    private static MainKeybindSelector focusedKeybind = null;

    private String baseName = TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString();
    private int keyCode;

    public MainKeybindSelector(int x, int y, int width, int height) {
        super(TextUtils.small(TextUtils.translatable("nitsha.binds.advances.noKeyBind")),
                x, y, width, height, true, () -> {}, 0xFFFFFFFF, 0xFF07938d, 0xFF212121, 0xFFFFFFFF);
        this.setButtonDirection(1);
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
        this.baseName = keyCode == 0 ? TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString() : NitshaInput.getKeyName(keyCode);
    }

    public int getKeyCode() { return this.keyCode; }

    public static MainKeybindSelector getFocusedField() { return focusedKeybind; }

    public static void setFocusedField(MainKeybindSelector fK) {
        if (focusedKeybind != null) focusedKeybind.setPressed(false);
        focusedKeybind = fK;
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        int maxSymbols = (this.getWidth() / 7) - (isPressed() ? 4 : 0);
        String displayName = (isPressed()) ? "> " + GUIUtils.truncateString(baseName, maxSymbols) + " <" : GUIUtils.truncateString(baseName, maxSymbols);

        int btnColor = isPressed() ? 0xFF07938d : 0xFFFFFFFF;
        int textColor = isPressed() ? 0xFFFFFFFF : 0xFF212121;

        this.setText(TextUtils.small(TextUtils.literal(displayName)));
        this.setColors(btnColor, 0xFF07938d, textColor, 0xFFFFFFFF);

        super.onRender(ctx, mouseX, mouseY, delta);
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
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!super.onMouseClicked(mouseX, mouseY, button)) {
            return false;
        }

        setLastClickedWidget(this);
        setFocusedField(this);
        setPressed(true);
        setFocused(true);

        return true;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int nativeCode, int modifiers) {
        if (focusedKeybind != null) {
            int newKey = keyCode == InputConstants.KEY_ESCAPE ? 0 : keyCode;

            focusedKeybind.setPressed(false);
            focusedKeybind.setKeyCode(newKey);
            MainKeybindSelector.setFocusedField(null);

            return true;
        }

        return super.onKeyPressed(keyCode, nativeCode, modifiers);
    }
}