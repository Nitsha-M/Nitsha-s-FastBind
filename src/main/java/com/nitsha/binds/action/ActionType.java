package com.nitsha.binds.action;

import com.nitsha.binds.configs.dto.preset.ActionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

public abstract class ActionType<T extends ActionData> {

    protected Runnable heightChangeListener;

    public void setHeightChangeListener(Runnable listener) {
        this.heightChangeListener = listener;
    }

    public void setPosition(int x, int y) {}

    public abstract String getId();
    public abstract String getDisplayName();
    public abstract String getDefaultValue();
    public abstract int getLineColor();
    public abstract int getHeight();

    public abstract T createDefaultData();

    public abstract void buildTasks(T data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil);

    public abstract void init(int x, int y, int width, T value);

    public abstract void render(GuiGraphics ctx, int mouseX, int mouseY, float delta);

    public abstract T getValue();

    public abstract void reset();

    //? if >=1.21.9 {
    /*public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) { return false; }
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) { return false; }
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double deltaX, double deltaY) { return false; }
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) { return false; }
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) { return false; }*/
    //? } else {
    public boolean mouseClicked(double mx, double my, int btn) { return false; }
    public boolean mouseReleased(double mx, double my, int btn) { return false; }
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) { return false; }
    public boolean keyPressed(int key, int scan, int mods) { return false; }
    public boolean charTyped(char c, int mods) { return false; }
    //? }
    public boolean mouseScrolled(double mx, double my, double amount) { return false; }

    public boolean isMouseOverColorButtons(double mouseX, double mouseY) { return false; }
}