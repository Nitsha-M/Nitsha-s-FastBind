package com.nitsha.binds.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.gui.widget.button.BedrockButton;
import com.nitsha.binds.multiversions.NitshaInput;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.MutableComponent;

public class KeybindSelector extends BedrockButton {
    private static KeybindSelector focusedKeybind;
    private static GuiEventListener lastClickedWidget;

    private int keyCode;
    private String baseName = TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString();

    public KeybindSelector(int x, int y, int width, int height) {
        super(TextUtils.empty(), x, y, width, height, true, () -> {}, 0xFFFFFFFF, 0xFF07938D, 0xFF212121, 0xFFFFFFFF);
        updateName();
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
        baseName = keyCode == 0 ? TextUtils.translatable("nitsha.binds.advances.noKeyBind").getString() : NitshaInput.getKeyName(keyCode);
        updateName();
    }

    public int getKeyCode() {
        return keyCode;
    }

    private void updateName() {
        Font font = Minecraft.getInstance().font;
        String prefix = isPressed() ? "> " : "";
        String suffix = isPressed() ? " <" : "";
        int availableWidth = Math.max(0, getWidth() - 8 - font.width(TextUtils.small(TextUtils.literal(prefix + suffix))));
        MutableComponent name = TextUtils.small(TextUtils.literal(baseName));
        String displayName = baseName;

        if (font.width(name) > availableWidth) {
            int ellipsisWidth = font.width(TextUtils.small(TextUtils.literal("...")));

            if (ellipsisWidth <= availableWidth) {
                displayName = font.substrByWidth(name, availableWidth - ellipsisWidth).getString() + "...";
            } else {
                displayName = font.substrByWidth(name, availableWidth).getString();
            }
        }

        setText(TextUtils.small(TextUtils.literal(prefix + displayName + suffix)));
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        updateName();
    }

    public static KeybindSelector getFocusedField() {
        return focusedKeybind;
    }

    public static void setFocusedField(KeybindSelector selector) {
        if (focusedKeybind != null && focusedKeybind != selector) {
            focusedKeybind.setPressed(false);
            focusedKeybind.setFocused(false);
        }

        focusedKeybind = selector;
    }

    public static void setLastClickedWidget(GuiEventListener widget) {
        lastClickedWidget = widget;
    }

    public static void controlFocus() {
        if (focusedKeybind != null && lastClickedWidget != focusedKeybind) {
            setFocusedField(null);
        }
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (captureMouseButton(button)) {
            return true;
        }

        if (!active || !super.onMouseClicked(mouseX, mouseY, button)) {
            return false;
        }

        setLastClickedWidget(this);
        setFocusedField(this);
        setPressed(true);
        setFocused(true);
        return true;
    }

    public boolean captureMouseButton(int button) {
        if (focusedKeybind != this || !active || !visible || !isEnabled()) {
            return false;
        }

        setKeyCode(NitshaInput.encodeMouseButton(button));
        setFocusedField(null);
        return true;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int nativeCode, int modifiers) {
        if (focusedKeybind != this || !active || !visible || !isEnabled()) {
            return super.onKeyPressed(keyCode, nativeCode, modifiers);
        }

        setKeyCode(keyCode == InputConstants.KEY_ESCAPE ? 0 : keyCode);
        setFocusedField(null);
        return true;
    }
}
