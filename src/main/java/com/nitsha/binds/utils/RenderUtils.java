package com.nitsha.binds.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class RenderUtils {
    public static Renderable wrapRenderable(GuiEventListener element) {
        if (element instanceof Renderable) {
            return (Renderable) element;
        }

        //? if >=26.1 {
        /*if (element instanceof net.minecraft.client.gui.components.Renderable vanilla) {
            return new Renderable() {
                @Override
                public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
                    vanilla.extractRenderState(ctx, mouseX, mouseY, delta);
                }
            };
        }*/
        //? } else >=1.19.3 {
        if (element instanceof net.minecraft.client.gui.components.Renderable vanilla) {
            return new Renderable() {
                @Override
                public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
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
