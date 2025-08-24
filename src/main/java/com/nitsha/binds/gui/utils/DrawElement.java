package com.nitsha.binds.gui.utils;

//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
import net.minecraft.client.util.math.MatrixStack;
//? }

public class DrawElement {

    @FunctionalInterface
    public interface Drawer {
        void draw(Object ctx, float mouseX, float mouseY);
    }

    private final Drawer drawer;

    public DrawElement(Drawer drawer) {
        this.drawer = drawer;
    }

    //? if >=1.20 {
    public void render(DrawContext ctx, float mouseX, float mouseY) {
        drawer.draw(ctx, mouseX, mouseY);
    }
    //? } else {
    /*public void render(MatrixStack ctx, float mouseX, float mouseY) {
        drawer.draw(ctx, mouseX, mouseY);
    }
    *///? }
}
