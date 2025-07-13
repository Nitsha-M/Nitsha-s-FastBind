package com.nitsha.binds.gui.extend;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.GUIUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AnimatedSprite {
    protected int x, y, width, height;
    private int textureU;
    private int minU;
    private int maxU;
    private int step;
    private int elapsedTicks = 0;
    private int frameDelay;
    private int color = 0xFFFFFFFF;
    private int texV;
    private Identifier texture;
    private boolean animating = false;
    private boolean isOpen;
    private int textureW, textureH;

    public AnimatedSprite(int width, int height, Identifier texture, int texV, boolean isOpen, int textureU, int minU, int maxU, int step, int frameDelay, int textureW, int textureH) {
        this.width = width;
        this.height = height;
        this.textureU = textureU;
        this.minU = minU;
        this.maxU = maxU;
        this.step = step;
        this.frameDelay = frameDelay;
        this.texture = texture;
        this.texV = texV;
        this.isOpen = isOpen;
        this.textureW = textureW;
        this.textureH = textureH;
    }

    public void render(DrawContext ctx) {
        GUIUtils.adaptiveDrawTexture(ctx, texture, this.x, y, textureU, texV, width, height, textureW, textureH, color);
    }

    public void startAnimation(boolean open) {
        this.isOpen = open;
        this.animating = true;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void tick() {
        if (animating) {
            elapsedTicks++;
            if (elapsedTicks >= frameDelay) {
                elapsedTicks = 0;
                if (isOpen) {
                    if (textureU < maxU) {
                        textureU += step;
                    } else {
                        textureU = maxU;
                        animating = false;
                    }
                } else {
                    if (textureU > minU) {
                        textureU -= step;
                    } else {
                        textureU = minU;
                        animating = false;
                    }
                }
            }
        }
    }
}
