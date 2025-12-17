package com.nitsha.binds.gui.utils;

//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
 *///?}

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
    public void render(GuiGraphics ctx, float mouseX, float mouseY) {
        drawer.draw(ctx, mouseX, mouseY);
    }
    //?} else {
    /*public void render(PoseStack ctx, float mouseX, float mouseY) {
        drawer.draw(ctx, mouseX, mouseY);
    }
    *///?}
}