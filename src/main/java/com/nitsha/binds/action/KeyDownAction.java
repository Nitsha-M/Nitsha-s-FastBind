package com.nitsha.binds.action;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.KeybindSelector;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

import com.nitsha.binds.configs.dto.actions.AllActionsData.KeybindActionData;

public class KeyDownAction extends ActionType<KeybindActionData> {

    private KeybindSelector keybind;
    private int x, y;

    @Override public String getId() { return "keyDown"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.keyDown").getString();
    }

    @Override public String getDefaultValue() { return "0"; }
    @Override public int getLineColor() { return 0xFF972C24; }
    @Override public int getHeight() { return 26; }

    @Override
    public KeybindActionData createDefaultData() { return new KeybindActionData("keyDown"); }

    @Override
    public void buildTasks(KeybindActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        int keyCode = data.value;
        if (keyCode == 0) return;

            //? if >=1.21.9 {
            /*final int finalKeyCode = keyCode;
            actions.add(() -> {
                InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(finalKeyCode);
                KeyMapping.set(key, true);
            });*/
            //? } else {
            int scancode = GLFW.glfwGetKeyScancode(keyCode);
            actions.add(() -> {
                InputConstants.Key key = InputConstants.getKey(keyCode, scancode);
                KeyMapping.set(key, true);
            });
            //? }
    }

    @Override
    public void init(int x, int y, int width, KeybindActionData data) {
        this.x = x;
        this.y = y;
        this.keybind = new KeybindSelector(x + width - 26 - 68, y + 3, 68, 19);
        this.keybind.setKeyCode(data.value);
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (keybind != null) keybind.setY(y + 3);
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        keybind.renderWidget(ctx, mouseX, mouseY, delta);

        String userLanguage = GUIUtils.getUL();
        if (userLanguage.equals("uk_ua") || userLanguage.equals("ru_ru")) {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.keyDown_1"), 0,
                    x + 2, y + 4, "top", "left", 0xFF212121, false);
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.key"), 0,
                    x + 2, y + 12, "top", "left", 0xFF212121, false);
        } else {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.keyDown"), 0,
                    x + 2, y + 8, "top", "left", 0xFF212121, false);
        }
    }

    @Override
    public KeybindActionData getValue() {
        KeybindActionData result = new KeybindActionData("keyDown");
        result.value = keybind.getKeyCode();
        return result;
    }

    @Override
    public void reset() {
        keybind.setKeyCode(0);
        keybind.setPressed(false);
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        return keybind.mouseClicked(event, bl);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return keybind.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        return keybind.charTyped(event);
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        return keybind.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        return keybind.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return keybind.charTyped(c, mods);
    }
    //? }
}