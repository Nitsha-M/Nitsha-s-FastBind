package com.nitsha.binds.gui.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class AnimatedSprite {
    protected int x, y, width, height;
    private int textureU;
    private final int minU;
    private final int maxU;
    private final int step;
    private final int frameDelayMs;
    private int color = 0xFFFFFFFF;
    private final int texV;
    private final ResourceLocation texture;
    private boolean animating = false;
    private boolean isOpen;
    private final int textureW;
    private final int textureH;
    private boolean loop = false;

    private long lastUpdateTime;
    private float timeAccumulator;
    
    private int loopPauseMs = 0;
    private boolean isPaused = false;
    private long pauseStartTime = 0;

    public AnimatedSprite(int width, int height, ResourceLocation texture, int texV, boolean isOpen, int textureU, int minU, int maxU, int step, int frameDelayMs, int textureW, int textureH) {
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

    public void render(GuiGraphics ctx) {
        GUIUtils.adaptiveDrawTexture(ctx, texture, this.x, y, textureU, texV, width, height, textureW, textureH, color);
        tick();
    }

    public void startAnimation(boolean open) {
        this.isOpen = open;
        this.animating = true;
        this.lastUpdateTime = System.currentTimeMillis();
        this.timeAccumulator = 0;
        this.textureU = 0;
        this.isPaused = false;
    }

    public void stopAnimation() {
        this.isOpen = false;
        this.animating = false;
        this.lastUpdateTime = 0;
        this.timeAccumulator = 0;
        this.isPaused = false;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLooping() {
        return loop;
    }
    
    public void setLoopPause(int pauseMs) {
        this.loopPauseMs = pauseMs;
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

        if (isPaused) {
            if (currentTime - pauseStartTime >= loopPauseMs) {
                isPaused = false;
                lastUpdateTime = currentTime;
                timeAccumulator = 0;
            } else {
                return;
            }
        }

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
                        if (loopPauseMs > 0) {
                            isPaused = true;
                            pauseStartTime = currentTime;
                            timeAccumulator = 0;
                            break;
                        }
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
                        if (loopPauseMs > 0) {
                            isPaused = true;
                            pauseStartTime = currentTime;
                            timeAccumulator = 0;
                            break;
                        }
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