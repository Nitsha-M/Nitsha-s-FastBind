package com.nitsha.binds.gui.utils;

import net.minecraft.client.gui.GuiGraphics;

public class DrawElement {
    @FunctionalInterface
    public interface Drawer {
        void draw(GuiGraphics ctx, float mouseX, float mouseY);
    }

    private final Drawer drawer;

    public DrawElement(Drawer drawer) {
        this.drawer = drawer;
    }

    public void render(GuiGraphics ctx, float mouseX, float mouseY) {
        drawer.draw(ctx, mouseX, mouseY);
    }
}