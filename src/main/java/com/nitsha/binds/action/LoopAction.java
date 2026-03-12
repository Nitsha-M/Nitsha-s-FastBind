package com.nitsha.binds.action;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.KeybindSelector;
import com.nitsha.binds.gui.widget.TextField;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
 *///?}
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

public class LoopAction extends ActionType {

    private int x, y, width;
    private TextField actionsField;
    private TextField countField;

    @Override public String getId() { return "loop"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.loop").getString();
    }

    @Override public String getDefaultValue() { return ""; }
    @Override public int getLineColor() { return 0xFF0e5279; }
    @Override public int getHeight() { return 45; }

    @Override
    public void buildTasks(Map<String, Object> data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {}

    @Override
    public void init(int x, int y, int width, Object value) {
        this.x = x;
        this.y = y;
        this.width = width;

        this.actionsField = new TextField(
                Minecraft.getInstance().font,
                x, y + 24, 80, 19,
                2, "1",
                TextUtils.translatable("nitsha.binds.advances.actions.loop.actions").getString(), true
        );

        this.countField = new TextField(
                Minecraft.getInstance().font,
                x + 82, y + 24, 80, 19,
                2, "1",
                TextUtils.translatable("nitsha.binds.advances.actions.loop.count").getString(), true
        );

        if (value instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) value;
            if (data.containsKey("actions")) {
                actionsField.setText(String.valueOf(data.get("actions")));
            }
            if (data.containsKey("count")) {
                countField.setText(String.valueOf(data.get("count")));
            }
        }
    }

    @Override
    public void render(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        GuiGraphics c = (GuiGraphics) ctx;
        //?} else {
        /*PoseStack c = (PoseStack) ctx;*/
        //?}
        GUIUtils.addText(c, TextUtils.translatable("nitsha.binds.advances.actions.loop"), 0,
                x + 2, y + 8, "top", "left", 0xFF212121, false);
        actionsField.render(c, mouseX, mouseY, delta);
        countField.render(c, mouseX, mouseY, delta);
    }

    @Override
    public Map<String, Object> getValue() {
        int actions = 1, count = 1;
        try { actions = Integer.parseInt(actionsField.getText()); } catch (NumberFormatException ignored) {}
        try { count = Integer.parseInt(countField.getText()); } catch (NumberFormatException ignored) {}

        Map<String, Object> value = new HashMap<>();
        value.put("actions", actions);
        value.put("count", count);

        Map<String, Object> result = new HashMap<>();
        result.put("type", "loop");
        result.put("value", value);
        return result;
    }

    @Override
    public void reset() {
        actionsField.setText("1");
        countField.setText("1");
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        return countField.mouseClicked(event, bl) || actionsField.mouseClicked(event, bl);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return countField.keyPressed(event) || actionsField.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        return countField.charTyped(event) || actionsField.charTyped(event);
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        return countField.mouseClicked(mx, my, btn) || actionsField.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        return countField.keyPressed(key, scan, mods) || actionsField.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return countField.charTyped(c, mods) || actionsField.charTyped(c, mods);
    }
    //? }
}