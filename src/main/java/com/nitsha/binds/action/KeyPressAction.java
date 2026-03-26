package com.nitsha.binds.action;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.KeybindSelector;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

public class KeyPressAction extends ActionType {

    private KeybindSelector keybind;
    private int x, y;

    @Override public String getId() { return "keybind"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.pressKey").getString();
    }

    @Override public String getDefaultValue() { return "0"; }
    @Override public int getLineColor() { return 0xFF790e06; }
    @Override public int getHeight() { return 26; }

    @Override
    public void buildTasks(Map<String, Object> data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        Object value = data.get("value");
        try {
            int keyCode;
            if (value instanceof Number) {
                keyCode = ((Number) value).intValue();
            } else {
                keyCode = Integer.parseInt(String.valueOf(value));
            }
            if (keyCode == 0) return;

            //? if >=1.21.9 {
            /*final int finalKeyCode = keyCode;
            actions.add(() -> {
                InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(finalKeyCode);
                KeyMapping.set(key, true);
                KeyMapping.click(key);
            });
            actions.add(() -> setWaitUntil.accept(Util.getMillis() + 100));
            actions.add(() -> {
                InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(finalKeyCode);
                KeyMapping.set(key, false);
            });*/
            //? } else {
            int scancode = GLFW.glfwGetKeyScancode(keyCode);
            actions.add(() -> {
                InputConstants.Key key = InputConstants.getKey(keyCode, scancode);
                KeyMapping.set(key, true);
                KeyMapping.click(key);
            });
            actions.add(() -> setWaitUntil.accept(Util.getMillis() + 100));
            actions.add(() -> {
                InputConstants.Key key = InputConstants.getKey(keyCode, scancode);
                KeyMapping.set(key, false);
            });
            //? }
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public void init(int x, int y, int width, Object value) {
        this.x = x;
        this.y = y;
        this.keybind = new KeybindSelector(x + width - 26 - 68, y + 3, 68, 19);

        int keyCode = 0;
        if (value instanceof Number) {
            keyCode = ((Number) value).intValue();
        } else {
            try {
                keyCode = Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException ignored) {}
        }
        this.keybind.setKeyCode(keyCode);
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        keybind.renderWidget(ctx, mouseX, mouseY, delta);

        String userLanguage = GUIUtils.getUL();
        if (userLanguage.equals("uk_ua") || userLanguage.equals("ru_ru")) {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.pressKey_1"), 0,
                    x + 2, y + 4, "top", "left", 0xFF212121, false);
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.key"), 0,
                    x + 2, y + 12, "top", "left", 0xFF212121, false);
        } else {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.pressKey"), 0,
                    x + 2, y + 8, "top", "left", 0xFF212121, false);
        }
    }

    @Override
    public Map<String, Object> getValue() {
        return Map.of("type", "keybind", "value", keybind.getKeyCode());
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