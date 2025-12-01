package com.nitsha.binds.utils;

import com.mojang.blaze3d.vertex.PoseStack;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.gui.components.events.GuiEventListener;

public class RenderUtils {
    public static Renderable wrapRenderable(GuiEventListener element) {
        if (element instanceof Renderable) {
            return (Renderable) element;
        }

        //? if >=1.19.1 {
        if (element instanceof net.minecraft.client.gui.components.Renderable vanilla) {
            return new Renderable() {
                @Override
                public void render(
                        //? if >=1.20 {
                        GuiGraphics ctx
                        //? } else {
                        // PoseStack ctx
                        //? }
                        , int mouseX, int mouseY, float delta) {
                    vanilla.render(ctx, mouseX, mouseY, delta);
                }
            };
        }
        //?} else {
        /*if (element instanceof net.minecraft.client.gui.components.Widget) {
            net.minecraft.client.gui.components.Widget widget = (net.minecraft.client.gui.components.Widget) element;
            return new Renderable() {
                @Override
                public void render(PoseStack ctx, int mouseX, int mouseY, float delta) {
                    widget.render(ctx, mouseX, mouseY, delta);
                }
            };
        }
        */
        //?}

        return null;
    }
}
