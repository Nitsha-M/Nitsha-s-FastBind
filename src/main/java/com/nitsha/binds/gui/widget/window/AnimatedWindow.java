package com.nitsha.binds.gui.widget.window;

import com.nitsha.binds.Main;
import com.nitsha.binds.multiversions.NitshaContainer;
import com.nitsha.binds.multiversions.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class AnimatedWindow extends NitshaContainer {

    private final Identifier bottomTexture;
    private final Identifier topTexture;

    private float targetX;
    private float targetY;
    private float targetWidth;
    private float targetHeight;

    private long delayMs;
    private long lastUpdateTime;

    private float baseYOffset;
    private float topYOffset;
    private float alpha;

    private int globalColor = 0xFFFFFFFF;

    private final float speed = Main.GLOBAL_ANIMATION_SPEED - 0.1F;

    private Runnable onFinish;

    private enum AnimationState {
        DROPPING_ALL,
        LIFTING_TOP,
        FINISHED,
        HIDING_TOP,
        LIFTING_ALL,
        HIDDEN
    }

    private AnimationState animState = AnimationState.HIDDEN;

    public AnimatedWindow(float x, float y, float width, float height, Identifier bottomTexture, Identifier topTexture, int delayMs) {
        super(x, y, width, height);

        this.targetX = x;
        this.targetY = y;
        this.targetWidth = width;
        this.targetHeight = height;

        this.bottomTexture = bottomTexture;
        this.topTexture = topTexture;
        this.delayMs = delayMs;

        this.baseYOffset = y - 80;
    }

    @Override
    public void setX(float x) {
        targetX = x;
    }

    @Override
    public void setY(float y) {
        targetY = y;
    }

    @Override
    public void setWidth(float width) {
        targetWidth = width;
    }

    @Override
    public void setHeight(float height) {
        targetHeight = height;
    }

    public void updateX(float delta) {
        targetX = x - delta;
    }

    public void updateY(float delta) {
        targetY = y - delta;
    }

    public void updateWidth(float delta) {
        targetWidth = width + delta;
    }

    public void updateHeight(float delta) {
        targetHeight = height + delta;
    }

    public int getYOffset() {
        return Math.round(baseYOffset - topYOffset);
    }

    public int getColor() {
        return globalColor;
    }

    public long getDelay() {
        return delayMs;
    }

    public boolean isAnimationFinished() {
        return animState == AnimationState.FINISHED;
    }

    @Override
    protected int getContentY() {
        return getYOffset();
    }

    @Override
    protected boolean canClick() {
        return isVisible() && isAnimationFinished();
    }

    public void open(Runnable onFinish) {
        if (isVisible()) return;

        setVisible(true);

        this.onFinish = onFinish;
        animState = AnimationState.DROPPING_ALL;
        alpha = 0.0F;
        baseYOffset = y - 80;
        topYOffset = 0.0F;
        lastUpdateTime = System.currentTimeMillis();
    }

    public void close(Runnable onFinish) {
        if (!isVisible()) return;

        this.onFinish = onFinish;
        animState = AnimationState.HIDING_TOP;
        lastUpdateTime = System.currentTimeMillis();
    }

    private void runOnFinish() {
        Runnable callback = onFinish;
        onFinish = null;

        if (callback != null) {
            callback.run();
        }
    }

    public void tick() {
        long currentTime = System.currentTimeMillis();

        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            return;
        }

        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        if (delayMs > 0) {
            delayMs = Math.max(0L, delayMs - deltaTime);
            return;
        }

        float lerpFactor = 1.0F - (float) Math.pow(1.0F - speed, deltaTime / 16.666F);

        animateValues(lerpFactor);
        updateAnimationState(lerpFactor);
    }

    private void animateValues(float lerpFactor) {
        float previousY = y;
        width = Mth.lerp(lerpFactor, width, targetWidth);
        height = Mth.lerp(lerpFactor, height, targetHeight);
        x = Mth.lerp(lerpFactor, x, targetX);
        y = Mth.lerp(lerpFactor, y, targetY);

        if (Math.abs(width - targetWidth) < 0.1F) {
            width = targetWidth;
        }

        if (Math.abs(height - targetHeight) < 0.1F) {
            height = targetHeight;
        }

        if (Math.abs(x - targetX) < 0.1F) {
            x = targetX;
        }

        if (Math.abs(y - targetY) < 0.1F) {
            y = targetY;
        }

        baseYOffset += y - previousY;
    }

    private void updateAnimationState(float lerpFactor) {
        switch (animState) {
            case DROPPING_ALL:
                alpha = Mth.lerp(lerpFactor, alpha, 1.0F);
                baseYOffset = Mth.lerp(lerpFactor, baseYOffset, y + 2);

                if (Math.abs(baseYOffset - (y + 2)) < 0.001F) {
                    baseYOffset = y + 2;
                    alpha = 1.0F;
                    animState = AnimationState.LIFTING_TOP;
                    runOnFinish();
                }

                break;

            case LIFTING_TOP:
                topYOffset = Mth.lerp(lerpFactor, topYOffset, 2);

                if (Math.abs(topYOffset - 2) < 0.001F) {
                    topYOffset = 2;
                    animState = AnimationState.FINISHED;
                }

                break;

            case HIDING_TOP:
                alpha = Mth.lerp(lerpFactor, alpha, 0.0F);
                topYOffset = Mth.lerp(lerpFactor, topYOffset, 0);

                if (Math.abs(topYOffset) < 0.001F) {
                    topYOffset = 0;
                    animState = AnimationState.LIFTING_ALL;
                }

                break;

            case LIFTING_ALL:
                alpha = Mth.lerp(lerpFactor, alpha, 0.0F);
                baseYOffset = Mth.lerp(lerpFactor, baseYOffset, y - 80);

                if (onFinish != null && baseYOffset < y - 49) {
                    setVisible(false);
                    runOnFinish();
                }

                if (animState == AnimationState.LIFTING_ALL && Math.abs(baseYOffset - (y - 80)) < 0.001F) {
                    baseYOffset = y - 80;
                    alpha = 0.0F;
                    animState = AnimationState.HIDDEN;
                    setVisible(false);
                }

                break;

            case FINISHED:
                baseYOffset = y + 2;
                break;

            case HIDDEN:
                break;
        }
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        tick();
        updateLayout();

        int alphaByte = Mth.clamp(Math.round(alpha * 255.0F), 0, 255);
        globalColor = 0xFFFFFFFF;

        if (delayMs <= 0 && isVisible()) {
            RenderUtils.matricesUtil(ctx, getX(), Math.round(baseYOffset), 1, () -> {
                RenderUtils.drawResizableBox(ctx, bottomTexture, 0, 0, getWidth(), getHeight() - 2, 7, 15, globalColor);
            });

            super.onRender(ctx, mouseX, mouseY, delta);
        }
    }

    protected void updateLayout() {
    }

    @Override
    protected void renderBackground(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.drawResizableBox(ctx, topTexture, 0, 0, getWidth(), getHeight() - 2, 7, 15, globalColor);
    }

    @Override
    protected void renderChild(GuiGraphicsExtractor ctx, GuiEventListener child, int contentX, int contentY, int mouseX, int mouseY, float delta) {
        if (child instanceof ScrollableWindow window) {
            RenderUtils.customScissor(ctx, contentX + window.getX(), contentY + window.getY(), window.getWidth(), window.getHeight(), () -> {
                super.renderChild(ctx, child, contentX, contentY, mouseX, mouseY, delta);
            });

            return;
        }

        super.renderChild(ctx, child, contentX, contentY, mouseX, mouseY, delta);
    }
}
