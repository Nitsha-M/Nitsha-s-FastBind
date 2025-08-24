package com.nitsha.binds.gui.widget;

import com.google.common.collect.Lists;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.DrawElement;
import net.minecraft.client.gui.*;
//? if >=1.17 {
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
//? }
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class AnimatedWindow extends AbstractParentElement implements Drawable, Element /*? if >=1.17 {*/ , Selectable /*? }*/ {
    private Identifier T_1, T_2;

    private final List<Element> children = Lists.<Element>newArrayList();
    private final List<Drawable> drawables = Lists.<Drawable>newArrayList();
    private final List<DrawElement> drawElementsTop = Lists.<DrawElement>newArrayList();
    private final List<DrawElement> drawElementsBottom = Lists.<DrawElement>newArrayList();

    private long delayMs;
    private float x, y, width, height;
    private float targetX, targetY, targetWidth, targetHeight;

    private boolean visible = false;
    private int globalColor = 0xFFFFFFFF;

    private enum AnimationState {
        DROPPING_ALL,
        LIFTING_TOP,
        FINISHED,
        HIDING_TOP,
        LIFTING_ALL,
        HIDDEN
    }

    private AnimationState animState;
    private final float speed = MainClass.GLOBAL_ANIMATION_SPEED - 0.1f;
    private float baseYOffset, topYOffset, alpha;
    private Runnable onFinish = null;

    private long lastUpdateTime;

    public AnimatedWindow(float x, float y, float width, float height, Identifier t1, Identifier t2, int delayMs) {
        children.clear();
        drawElementsBottom.clear();
        this.x = this.targetX = x;
        this.y = this.targetY = y;
        this.width = this.targetWidth = width;
        this.height = this.targetHeight = height;
        this.T_1 = t1;
        this.T_2 = t2;
        this.delayMs = delayMs;

        this.animState = AnimationState.HIDDEN;
        this.baseYOffset = y - 80;
        this.topYOffset = 0;
        this.alpha = 0.0f;
        this.lastUpdateTime = 0;
    }

    // Add new children
    public void addDrawElement(DrawElement.Drawer drawer, int position) {
        if(position == 0) drawElementsBottom.add(new DrawElement(drawer));
        if(position == 1) drawElementsTop.add(new DrawElement(drawer));
    }

    public void addDrawElement(DrawElement.Drawer drawer) {
        this.addDrawElement(drawer, 0);
    }

    public <T extends Element & Drawable> void addElement(T drawableElement) {
        this.children.add(drawableElement);
        this.drawables.add(drawableElement);
    }

    // Basic parameters: get/set coords or size
    public int getX() { return Math.round(this.x); }
    public int getY() { return Math.round(this.y); }

    public int getWidth() { return Math.round(this.width); }
    public int getHeight() { return Math.round(this.height); }

    public void updateWidth(float delta) { this.targetWidth = this.width + delta; }
    public void updateHeight(float delta) { this.targetHeight = this.height + delta; }

    public void updateX(float delta) { this.targetX = this.x - delta; }
    public void updateY(float delta) { this.targetY = this.y - delta; }

    public void setWidth(float newWidth) { this.targetWidth = newWidth; }
    public void setHeight(float newHeight) { this.targetHeight = newHeight; }

    public void setX(float newX) { this.targetX = newX; }
    public void setY(float newY) { this.targetY = newY; }

    public void open(Runnable onFinish) {
        if (visible) return;
        this.visible = true;
        this.onFinish = onFinish;
        this.animState = AnimationState.DROPPING_ALL;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void close(Runnable onFinish) {
        if (!visible) return;
        this.animState = AnimationState.HIDING_TOP;
        this.onFinish = onFinish;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    private void updateAnimationState(float lerpFactor) {
        switch (animState) {
            case DROPPING_ALL -> {
                this.alpha = MathHelper.lerp(lerpFactor, this.alpha, 1.0f);
                baseYOffset = MathHelper.lerp(lerpFactor, baseYOffset, this.y + 2);
                if (Math.abs(baseYOffset - (this.y + 2)) < 0.001f) {
                    baseYOffset = this.y + 2;
                    alpha = 1.0f;
                    animState = AnimationState.LIFTING_TOP;
                    if (onFinish != null) {
                        onFinish.run();
                        onFinish = null;
                    }
                }
            }
            case LIFTING_TOP -> {
                topYOffset = MathHelper.lerp(lerpFactor, topYOffset, 2);
                if (Math.abs(topYOffset - 2) < 0.001f) {
                    topYOffset = 2;
                    animState = AnimationState.FINISHED;
                }
            }
            case HIDING_TOP -> {
                alpha = MathHelper.lerp(lerpFactor, alpha, 0.0f);
                baseYOffset = MathHelper.lerp(lerpFactor, baseYOffset, y - 100);

                if (onFinish != null && baseYOffset < y - 49) {
                    this.visible = false;
                    onFinish.run();
                    onFinish = null;
                }
                if (Math.abs(baseYOffset - (y - 100)) < 0.001f) {
                    baseYOffset = y - 100;
                    alpha = 0.0f;
                    animState = AnimationState.LIFTING_ALL;
                }
            }
            case LIFTING_ALL -> {
                topYOffset = MathHelper.lerp(lerpFactor, topYOffset, 0);
                if (Math.abs(topYOffset) < 0.001f) {
                    topYOffset = 0;
                    animState = AnimationState.HIDDEN;
                }
            }
            case FINISHED, HIDDEN -> {}
        }
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

    public boolean isVisible() {
        return visible;
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
            delayMs -= deltaTime;
            if (delayMs < 0) delayMs = 0;
            return;
        }

        float lerpFactor = 1.0f - (float) Math.pow(1.0f - speed, deltaTime / 16.666f);

        animateValues(lerpFactor);
        updateAnimationState(lerpFactor);
    }

    private void animateValues(float lerpFactor) {
        this.width = MathHelper.lerp(lerpFactor, this.width, targetWidth);
        this.height = MathHelper.lerp(lerpFactor, this.height, targetHeight);
        this.x = MathHelper.lerp(lerpFactor, this.x, targetX);
        this.y = MathHelper.lerp(lerpFactor, this.y, targetY);

        if (Math.abs(this.width - targetWidth) < 0.1f) this.width = targetWidth;
        if (Math.abs(this.height - targetHeight) < 0.1f) this.height = targetHeight;
        if (Math.abs(this.x - targetX) < 0.1f) this.x = targetX;
        if (Math.abs(this.y - targetY) < 0.1f) this.y = targetY;
    }

    @Override
    public void render(
            //? if >=1.20 {
            DrawContext ctx
            //? } else {
            /*MatrixStack ctx
             *///? }
            , int mouseX, int mouseY, float delta) {
        int alphaByte = (int)(alpha * 255.0f) & 0xFF;
        globalColor = (alphaByte << 24) | 0xFFFFFFFF;

        int xO = getX();
        int yO = getYOffset();

        if (delayMs <= 0 && visible) {
            GUIUtils.matricesUtil(ctx, xO, Math.round(baseYOffset), 1, () -> {
                GUIUtils.drawResizableBox(ctx, this.T_1, 0, 0, getWidth(), getHeight() - 2, 7, 15, globalColor);
            });

            GUIUtils.matricesUtil(ctx, xO, yO, 2, () -> {
                GUIUtils.drawResizableBox(ctx, this.T_2, 0, 0, getWidth(), getHeight() - 2, 7, 15, globalColor);
                drawElementsBottom.forEach(element -> element.render(ctx, mouseX - xO, mouseY - yO));
            });

            drawables.forEach(element -> {
                Runnable render = () -> {
                    GUIUtils.matricesUtil(ctx, xO, yO, 2, () -> {
                        element.render(ctx, mouseX - xO, mouseY - yO, delta);
                    });
                };
                if (element instanceof ScrollableWindow sw) {
                    GUIUtils.customScissor(ctx,xO + sw.getX(), yO + sw.getY(), sw.getWidth(), sw.getHeight(), render);
                } else {
                    render.run();
                }
            });

            GUIUtils.matricesUtil(ctx, xO, yO, 2, () -> {
                drawElementsTop.forEach(element -> element.render(ctx, mouseX - xO, mouseY - yO));
            });
        }
        tick();
    }

    //? if >=1.17 {
    @Override
    public void appendNarrations(NarrationMessageBuilder builder) { }
    //? }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    public void clearChildren() {
        children.clear();
        drawables.clear();
        drawElementsTop.clear();
        drawElementsBottom.clear();
    }

    public void removeElementsOfType(Class<?> type) {
        children.removeIf(type::isInstance);
        drawables.removeIf(type::isInstance);
    }

    //? if >=1.17 {
    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
    //? }

    public boolean isMouseInside(double mouseX, double mouseY) {
        if (!visible) return false;
        float windowX = this.x;
        float windowY = getYOffset();
        float windowWidth = this.width;
        float windowHeight = this.height;

        return mouseX >= windowX &&
                mouseX <= windowX + windowWidth &&
                mouseY >= windowY &&
                mouseY <= windowY + windowHeight;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getYOffset();

        for (Element child : children) {
            if (child.mouseClicked(adjustedX, adjustedY, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        boolean released = false;
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getYOffset();

        for (Element child : children) {
            if (child.mouseReleased(adjustedX, adjustedY, button)) {
                released = true;
            }
        }

        return released;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!visible) return false;
        boolean dragged = false;
        double adjustedX = mouseX - getX();
        double adjustedY = mouseY - getYOffset();

        for (Element child : children) {
            if (child.mouseDragged(adjustedX, adjustedY, button, deltaX, deltaY)) {
                dragged = true;
            }
        }

        return dragged;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element child : children()) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Element child : children()) {
            if (child.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isVisible() || !isMouseInside(mouseX, mouseY)) return false;

        double adjX = mouseX - this.getX();
        double adjY = mouseY - this.getYOffset();

        for (Element child : children()) {
            if (child.mouseScrolled(adjX, adjY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    //? } else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isVisible() || !isMouseInside(mouseX, mouseY)) return false;

        double adjX = mouseX - this.getX();
        double adjY = mouseY - this.getYOffset();

        for (Element child : children()) {
            if (child.mouseScrolled(adjX, adjY, amount)) {
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    *///? }
}
