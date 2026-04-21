package com.nitsha.binds.action;

import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.TextField;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }
import net.minecraft.client.gui.GuiGraphics;
import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

import com.nitsha.binds.configs.dto.actions.AllActionsData.DelayActionData;

public class DelayAction extends ActionType<DelayActionData> {

    private TextField field;
    private int x, y;

    @Override public String getId() { return "delay"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.delay").getString();
    }

    @Override public String getDefaultValue() { return "100"; }
    @Override public int getLineColor() { return 0xFFc99212; }
    @Override public int getHeight() { return 26; }

    @Override
    public DelayActionData createDefaultData() {
        DelayActionData delay = new DelayActionData();
        delay.value = 100;
        return delay;
    }

    @Override
    public void buildTasks(DelayActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        final int finalMs = data.value;
        actions.add(() -> setWaitUntil.accept(Util.getMillis() + finalMs));
    }

    @Override
    public void init(int x, int y, int width, DelayActionData data) {
        this.x = x;
        this.y = y;
        this.field = new TextField(
                Minecraft.getInstance().font,
                x + 90, y + 3, width - 116, 19,
                6,
                String.valueOf(data.value),
                TextUtils.translatable("nitsha.binds.advances.actions.delayLine").getString(),
                true
        );
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (field != null) field.setY(y + 3);
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        field.renderWidget(ctx, mouseX, mouseY, delta);
        GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.delayDesс"), 0,
                x + 2, y + 8, "top", "left", 0xFF212121, false);
    }

    @Override
    public DelayActionData getValue() {
        DelayActionData result = new DelayActionData();
        try {
            result.value = Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            result.value = 100;
        }
        return result;
    }

    @Override public void reset() { field.setText("100"); }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        return field.mouseClicked(event, bl);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return field.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        return field.charTyped(event);
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        return field.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        return field.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return field.charTyped(c, mods);
    }
    //? }
}