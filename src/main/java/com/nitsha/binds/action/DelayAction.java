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
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;*/
//?}

import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

public class DelayAction extends ActionType {

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
    public void buildTasks(Map<String, Object> data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        Object value = data.get("value");
        try {
            int ms;
            if (value instanceof Number) {
                ms = ((Number) value).intValue();
            } else {
                String msText = String.valueOf(value);
                if (msText.isEmpty()) msText = "0";
                ms = Integer.parseInt(msText);
            }
            final int finalMs = ms;
            actions.add(() -> setWaitUntil.accept(Util.getMillis() + finalMs));
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public void init(int x, int y, int width, Object value) {
        this.x = x;
        this.y = y;
        this.field = new TextField(
                Minecraft.getInstance().font,
                x + 90, y + 3, width - 116, 19,
                6,
                String.valueOf(value),
                TextUtils.translatable("nitsha.binds.advances.actions.delayLine").getString(),
                true
        );
    }

    @Override
    public void render(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        GuiGraphics c = (GuiGraphics) ctx;
        //?} else {
        /*PoseStack c = (PoseStack) ctx;*/
        //?}
        field.render(c, mouseX, mouseY, delta);
        GUIUtils.addText(c, TextUtils.translatable("nitsha.binds.advances.actions.delayDesс"), 0,
                x + 2, y + 8, "top", "left", 0xFF212121, false);
    }

    @Override
    public Map<String, Object> getValue() {
        int ms;
        try {
            ms = Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            ms = 100;
        }
        return Map.of("type", "delay", "value", ms);
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