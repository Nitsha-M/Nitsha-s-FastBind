package com.nitsha.binds.gui.utils;

//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AnimatedSprite {
    protected int x, y, width, height;
    private int textureU;
    private final int minU;
    private final int maxU;
    private final int step;
    private final int frameDelayMs;
    private int color = 0xFFFFFFFF;
    private final int texV;
    private final Identifier texture;
    private boolean animating = false;
    private boolean isOpen;
    private final int textureW;
    private final int textureH;
    private boolean loop = false;

    private long lastUpdateTime;
    private float timeAccumulator;

    public AnimatedSprite(int width, int height, Identifier texture, int texV, boolean isOpen, int textureU, int minU, int maxU, int step, int frameDelayMs, int textureW, int textureH) {
        this.width = width;
        this.height = height;
        this.textureU = textureU;
        this.minU = minU;
        this.maxU = maxU;
        this.step = step;
        this.frameDelayMs = frameDelayMs;
        this.texture = texture;
        this.texV = texV;
        this.isOpen = isOpen;
        this.textureW = textureW;
        this.textureH = textureH;

        this.lastUpdateTime = 0;
        this.timeAccumulator = 0;
    }

    public void render(
            //? if >=1.20 {
            DrawContext ctx
            //? } else {
            /*MatrixStack ctx
             *///? }
            ) {
        GUIUtils.adaptiveDrawTexture(ctx, texture, this.x, y, textureU, texV, width, height, textureW, textureH, color);
        tick();
    }

    public void startAnimation(boolean open) {
        this.isOpen = open;
        this.animating = true;
        this.lastUpdateTime = System.currentTimeMillis();
        this.timeAccumulator = 0;
    }

    public void stopAnimation() {
        this.isOpen = false;
        this.animating = false;
        this.lastUpdateTime = 0;
        this.timeAccumulator = 0;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLooping() {
        return loop;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void tick() {
        if (!animating) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            return;
        }

        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        timeAccumulator += deltaTime;

        while (timeAccumulator >= frameDelayMs) {
            timeAccumulator -= frameDelayMs;

            if (isOpen) {
                if (textureU < maxU) {
                    textureU += step;
                } else {
                    if (loop) {
                        textureU = minU;
                    } else {
                        textureU = maxU;
                        animating = false;
                        break;
                    }
                }
            } else {
                if (textureU > minU) {
                    textureU -= step;
                } else {
                    if (loop) {
                        textureU = maxU;
                    } else {
                        textureU = minU;
                        animating = false;
                        break;
                    }
                }
            }
        }
    }
}