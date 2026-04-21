package com.nitsha.binds.action;

import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.TextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

import com.nitsha.binds.configs.dto.actions.AllActionsData.LoopActionData;
import com.nitsha.binds.configs.dto.actions.AllActionsData.LoopInnerData;

public class LoopAction extends ActionType<LoopActionData> {

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
    public LoopActionData createDefaultData() { return new LoopActionData(); }

    @Override
    public void buildTasks(LoopActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        if (data.value == null) return;
        int aIdx = data.value.actions;
        int tIdx = data.value.count;
        if (aIdx == 0 || tIdx == 0) return;
    }

    @Override
    public void init(int x, int y, int width, LoopActionData value) {
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

        if (value != null) {
            actionsField.setText(String.valueOf(value.value.actions));
            countField.setText(String.valueOf(value.value.count));
        }
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (actionsField != null) actionsField.setY(y + 24);
        if (countField != null) countField.setY(y + 24);
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.loop"), 0,
                x + 2, y + 8, "top", "left", 0xFF212121, false);
        actionsField.renderWidget(ctx, mouseX, mouseY, delta);
        countField.renderWidget(ctx, mouseX, mouseY, delta);
    }

    @Override
    public LoopActionData getValue() {
        LoopActionData result = new LoopActionData();
        try { result.value.actions = Integer.parseInt(actionsField.getText()); } catch (NumberFormatException ignored) {}
        try { result.value.count = Integer.parseInt(countField.getText()); } catch (NumberFormatException ignored) {}
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