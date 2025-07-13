package com.nitsha.binds.gui.extend;

import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

public class PositionedDrawElement {
    public int x;
    public int y;
    Consumer<DrawContext> draw;

    public PositionedDrawElement(int x, int y, Consumer<DrawContext> draw) {
        this.x = x;
        this.y = y;
        this.draw = draw;
    }

    public void render(DrawContext ctx) {
        draw.accept(ctx);
    }
}