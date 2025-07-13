package com.nitsha.binds.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.gui.GUIUtils;
import com.nitsha.binds.gui.extend.PositionedDrawElement;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AnimatedWindow extends AbstractParentElement implements Drawable, Element, Selectable {
    private Identifier T_1, T_2;

    private final List<Element> children = new ArrayList<>();
    private final List<PositionedDrawElement> drawElements = new ArrayList<>();

    private int height, y;
    private int delay = 0;

    private float x, width;
    private float targetX = 0, targetWidth = 0;

    private boolean visible = false;

    private enum AnimationState {
        DROPPING_ALL,
        LIFTING_TOP,
        FINISHED,
        HIDING_TOP,
        LIFTING_ALL,
        HIDDEN
    }

    private AnimationState animState;
    private final float speed = 0.1f;
    private float baseYOffset, topYOffset, alpha = 0.0f;
    private Runnable onFinish = null;

    public AnimatedWindow(float width, int height, float x, int y, Identifier t1, Identifier t2, int delay) {
        this.x = this.targetX = x;
        this.width = this.targetWidth = width;
        this.y = y;
        this.height = height;
        this.T_1 = t1;
        this.T_2 = t2;
        this.delay = delay;

        this.animState = AnimationState.HIDDEN;
        this.baseYOffset = y - 100;
        this.topYOffset = 0;
        this.alpha = 0.0f;
    }

    public void addElement(Element element) {
        children.add(element);
    }

    public void addDrawElement(Consumer<DrawContext> drawElement) {
        drawElements.add(new PositionedDrawElement(0, 0, ctx -> drawElement.accept(ctx)));
    }

    public int getX() {
        return Math.round(x);
    }

    public int getY() {
        return y;
    }

    public void updateWidth(float delta) {
        this.targetWidth = this.width + delta;
        updateX(delta / 2);
    }

    public void updateX(float delta) {
        this.targetX = this.x - delta;
    }

    public void open(Runnable onFinish) {
        if (visible) return;
        this.visible = true;
        this.onFinish = onFinish;
        this.animState = AnimationState.DROPPING_ALL;
    }

    public void close(Runnable onFinish) {
        if (!visible) return;
        this.animState = AnimationState.HIDING_TOP;
        this.onFinish = onFinish;
    }

    private void drawAnimatedBox(DrawContext ctx) {
        if (delay > 0) {
            delay--;
            return;
        }

        switch (animState) {
            case DROPPING_ALL -> {
                this.alpha = MathHelper.lerp(this.speed, this.alpha, 1.0f);
                if (Math.abs(this.alpha - 1.0f) < 0.001f) this.alpha = 1.0f;
                baseYOffset = MathHelper.lerp(speed, baseYOffset, this.y + 2);
                if (Math.abs(baseYOffset - (this.y + 2)) < 0.001f) {
                    baseYOffset = this.y + 2;
                    animState = AnimationState.LIFTING_TOP;
                    if (onFinish != null) {
                        onFinish.run();
                        onFinish = null;
                    }
                }
            }
            case LIFTING_TOP -> {
                topYOffset = MathHelper.lerp(speed, topYOffset, 2);
                if (Math.abs(topYOffset - 2) < 0.001f) {
                    topYOffset = 2;
                    animState = AnimationState.FINISHED;
                }
            }
            case HIDING_TOP -> {
                alpha = MathHelper.lerp(speed, alpha, 0.0f);
                baseYOffset = MathHelper.lerp(speed, baseYOffset, y - 100);

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
                topYOffset = MathHelper.lerp(speed, topYOffset, 0);
                if (Math.abs(topYOffset) < 0.001f) {
                    topYOffset = 0;
                    animState = AnimationState.HIDDEN;
                }
            }
            case FINISHED, HIDDEN -> {}
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int alphaByte = (int)(alpha * 255.0f) & 0xFF;
        int color = (alphaByte << 24) | 0xFFFFFF;

        int boxY = Math.round(baseYOffset);
        int topY = boxY - Math.round(topYOffset);
        GUIUtils.drawResizableBox(ctx, this.T_1, Math.round(this.x), boxY, Math.round(this.width), this.height - 2, 7, 15, color);
        GUIUtils.drawResizableBox(ctx, this.T_2, Math.round(this.x), topY, Math.round(this.width), this.height - 2, 7, 15, color);

        int yO = Math.round(baseYOffset) - Math.round(topYOffset);

        //? if >=1.21.6 {
        /*Matrix3x2fStack matrices = ctx.getMatrices();
        Matrix3x2f saved = new Matrix3x2f(matrices);
        matrices.translate(this.x, yO);
        *///? } else {
        MatrixStack matrices = ctx.getMatrices();
        matrices.push();
        matrices.translate(this.x, yO, 20);
        //? }




        if (delay == 0 && visible) {

            for (PositionedDrawElement element : drawElements) {
                element.render(ctx);
            }
            for (Element child : children) {
                if (child instanceof Drawable drawable) {
                    drawable.render(ctx, mouseX - Math.round(this.x), mouseY - yO, delta);
                }
            }
        }

        //? if >=1.21.6 {
        /*matrices.set(saved);
        *///? } else {
        matrices.pop();
        //? }

        drawAnimatedBox(ctx);

        this.width = MathHelper.lerp(speed, this.width, targetWidth);
        if (Math.abs(this.width - targetWidth) < 0.1f) this.width = targetWidth;
        this.x = MathHelper.lerp(speed, this.x, targetX);
        if (Math.abs(this.x - targetX) < 0.1f) this.x = targetX;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) { }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    public void removeElementsOfType(Class<?> type) {
        children.removeIf(type::isInstance);
    }

    public List<PositionedDrawElement> drawChildren() {
        return drawElements;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        float offsetX = this.x;
        float offsetY = Math.round(baseYOffset) - Math.round(topYOffset);

        double adjustedX = mouseX - offsetX;
        double adjustedY = mouseY - offsetY;

        boolean clickedAny = false;
        for (Element child : children()) {
            if (child.mouseClicked(adjustedX, adjustedY, button)) {
                clickedAny = true;
                if (child instanceof TextFieldWidget field) {
                    setFocused(field);
                } else {
                    setFocused(null);
                }
                return true;
            }
        }
        if (!clickedAny) {
            setFocused(null);
        }
        return super.mouseClicked(adjustedX, adjustedY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        double adjustedX = mouseX - this.x;
        double adjustedY = mouseY - (Math.round(baseYOffset) - Math.round(topYOffset));

        boolean released = false;
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
        double adjustedX = mouseX - this.x;
        double adjustedY = mouseY - (Math.round(baseYOffset) - Math.round(topYOffset));

        for (Element child : children) {
            if (child.mouseDragged(adjustedX, adjustedY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Element child : this.children) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Element child : children) {
            if (child.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
    }
}
