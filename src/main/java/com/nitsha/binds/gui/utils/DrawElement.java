package com.nitsha.binds.gui.utils;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class DrawElement {
    @FunctionalInterface
    public interface Drawer {
        void draw(GuiGraphicsExtractor ctx, float mouseX, float mouseY);
    }

    private final Drawer drawer;

    public DrawElement(Drawer drawer) {
        this.drawer = drawer;
    }

    public void render(GuiGraphicsExtractor ctx, float mouseX, float mouseY) {
        drawer.draw(ctx, mouseX, mouseY);
    }
}