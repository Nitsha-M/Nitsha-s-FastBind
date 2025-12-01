package com.nitsha.binds.gui.widget;

import com.google.common.collect.Lists;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.DrawElement;
import com.nitsha.binds.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import com.nitsha.binds.utils.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScrollableWindow extends AbstractContainerEventHandler
        implements Renderable, GuiEventListener /* ? if >=1.17 { */ , NarratableEntry /* ?} */ {
    private final List<GuiEventListener> children = Lists.<GuiEventListener>newArrayList();
    private final List<Renderable> renderables = Lists.<Renderable>newArrayList();
    private final List<DrawElement> drawElements = Lists.<DrawElement>newArrayList();

    private int scrollableArea, scrollOffset;
    private boolean horizontal = false;

    private int maxScroll = 0;
    private int scrollBarOffset = 0;
    private int barSize = 20;

    private boolean isDraggingScrollbar = false;
    private int dragStartY = 0;
    private int dragStartScrollOffset = 0;

    private int x, y, realX, realY, width, height;

    public ScrollableWindow(int x, int y, int realX, int realY, int width, int height, boolean horizontal) {
        children.clear();
        drawElements.clear();
        this.horizontal = horizontal;
        this.scrollBarOffset = 0;
        this.scrollableArea = 0;
        this.scrollOffset = 0;
        this.x = x;
        this.y = y;
        this.realX = realX;
        this.realY = realY;
        this.width = width;
        this.height = height;
        updateScrollLogic();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRealX() {
        return realX;
    }

    public int getRealY() {
        return realY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setRealY(int rY) {
        this.realY = rY;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    // Add new children
    public void addDrawElement(DrawElement.Drawer drawer) {
        drawElements.add(new DrawElement(drawer));
    }

    public void addElement(GuiEventListener element) {
        this.children.add(element);
        Renderable dr = RenderUtils.wrapRenderable(element);
        if (dr != null) {
            this.renderables.add(dr);
        }
    }

    public <T extends GuiEventListener & Renderable> void addElementAfter(T drawableElement, int index) {
        int insertPosChildren = Math.min(this.children.size(), index);
        int insertPosRenderables = Math.min(this.renderables.size(), index);

        this.children.add(insertPosChildren, drawableElement);
        this.renderables.add(insertPosRenderables, drawableElement);
    }

    public void clearChildren() {
        children.clear();
        renderables.clear();
    }

    public void setScrollableArea(int scrollableArea) {
        this.scrollableArea = scrollableArea;
        updateScrollLogic();
    }

    public void addScrollableArea(int delta) {
        this.scrollableArea += delta;
        updateScrollLogic();
    }

    public void resetScroll() {
        this.scrollOffset = 0;
        this.scrollBarOffset = 0;
    }

    public void scrollToBottom() {
        this.scrollOffset = this.maxScroll;
        updateScrollLogic();
    }

    @Override
    public void render(
            // ? if >=1.20 {
            GuiGraphics ctx
            // ?} else {
            /*PoseStack ctx*/
            // ?}
            , int mouseX, int mouseY, float delta) {
        int aX = (this.horizontal) ? this.getX() - scrollOffset : this.getX();
        int aY = (!this.horizontal) ? this.getY() - scrollOffset : this.getY();

        GUIUtils.matricesUtil(ctx, aX, aY, 1, () -> {
            int mX = (!isMouseInside(mouseX, mouseY)) ? -10000 : mouseX - aX;
            int mY = (!isMouseInside(mouseX, mouseY)) ? -10000 : mouseY - aY;
            drawElements.forEach(element -> element.render(ctx, mX, mY));
            renderables.forEach(element -> element.render(ctx, mX, mY, delta));
        });

        if (this.scrollableArea > this.height) {
            GUIUtils.matricesUtil(ctx, 0, 0, 2, () -> {
                int scrollbarX = this.getX() + this.width - 4;
                int scrollbarY = this.getY() + 1 + scrollBarOffset;
                boolean isHoveringScrollbar = isInsideScrollbar(mouseX, mouseY);
                int scrollbarColor = (isHoveringScrollbar) ? 0x80000000 : 0x40000000;
                GUIUtils.drawFill(ctx, scrollbarX + (isHoveringScrollbar ? 0 : 1), scrollbarY,
                        scrollbarX + (isHoveringScrollbar ? 4 : 3), scrollbarY + barSize, scrollbarColor);
            });
        }
    }

    public List<? extends GuiEventListener> children() {
        return children;
    }

    public void removeElementsOfType(Class<?> type) {
        children.removeIf(type::isInstance);
    }

    public List<DrawElement> drawChildren() {
        return drawElements;
    }

    // ? if >=1.17 {
    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return NarratableEntry.NarrationPriority.NONE;
    }
    // ?}

    public boolean isMouseInside(double mouseX, double mouseY) {
        int w = 0;
        int h = 0;
        if (horizontal) {
            h = (scrollableArea > width) ? 5 : 0;
        } else {
            w = (scrollableArea > height) ? 5 : 0;
        }
        return mouseX >= this.getX() && mouseX < this.getX() + this.width - w && mouseY >= this.getY()
                && mouseY < this.getY() + this.height - h;
    }

    public void updateScrollLogic() {
        this.maxScroll = Math.max(0, scrollableArea - height);

        int trackHeight = height - 2;
        if (scrollableArea > 0) {
            this.barSize = Math.max(20, (int) ((height / (float) scrollableArea) * trackHeight));
        } else {
            this.barSize = trackHeight;
        }
        int scrollArea = trackHeight - barSize;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        float scrollProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0f;
        scrollBarOffset = (int) (scrollProgress * scrollArea);
    }

    private boolean scrollLogic(double mouseX, double mouseY, double amount) {
        if (!isMouseInside(mouseX, mouseY))
            return false;
        long window = Minecraft.getInstance().getWindow().getWindow();
        boolean shift = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
        int scrollSpeed = (shift) ? 10 : 5;

        scrollOffset = Mth.clamp(scrollOffset - ((int) amount * scrollSpeed), 0, maxScroll);
        float scrollProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0;
        scrollBarOffset = (int) ((height - 2 - barSize) * scrollProgress);
        return true;
    }

    // ? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return scrollLogic(mouseX, mouseY, verticalAmount);
    }
    // ?} else {
    /*
     @Override
     public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
     return scrollLogic(mouseX, mouseY, amount);
     }
     */// ?}

    private boolean isInsideScrollbar(double mouseX, double mouseY) {
        int scrollbarX = this.getX() + this.width - 4;
        int scrollbarY = this.getY() + 1 + scrollBarOffset;
        return mouseX >= scrollbarX && mouseX <= scrollbarX + 4 &&
                mouseY >= scrollbarY && mouseY <= scrollbarY + barSize;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int aX = (this.horizontal) ? this.getX() - scrollOffset : this.getX();
        int aY = (!this.horizontal) ? this.getY() - scrollOffset : this.getY();

        boolean clicked = false;

        if (button == 0 && isInsideScrollbar(mouseX, mouseY)) {
            isDraggingScrollbar = true;
            dragStartY = (int) mouseY;
            dragStartScrollOffset = scrollOffset;
            clicked = true;
        }

        if (isMouseInside(mouseX, mouseY)) {
            for (GuiEventListener element : new ArrayList<>(this.children())) {
                if (element.mouseClicked(mouseX - aX, mouseY - aY, button)) {
                    this.setFocused(element);
                    if (button == 0) {
                        this.setDragging(true);
                    }
                    clicked = true;
                }
            }
        }

        return clicked;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        int aX = (this.horizontal) ? this.getX() - scrollOffset : this.getX();
        int aY = (!this.horizontal) ? this.getY() - scrollOffset : this.getY();
        if (button == 0 && isDraggingScrollbar) {
            isDraggingScrollbar = false;
        }
        return super.mouseReleased(mouseX - aX, mouseY - aY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDraggingScrollbar && button == 0) {
            int trackHeight = height - 2;
            int scrollArea = trackHeight - barSize;

            int dy = (int) mouseY - dragStartY;

            float scrollProgress = (float) dy / scrollArea;
            scrollOffset = Mth.clamp(dragStartScrollOffset + Math.round(scrollProgress * maxScroll), 0, maxScroll);

            float newProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0f;
            scrollBarOffset = (int) (newProgress * scrollArea);
        }

        for (GuiEventListener child : new ArrayList<>(children)) {
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GuiEventListener child : this.children) {
            if (child.keyPressed(keyCode, scanCode, modifiers))
                return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (GuiEventListener child : new ArrayList<>(children)) {
            if (child.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    // ? if >=1.17 {
    @Override
    public void updateNarration(NarrationElementOutput builder) {

    }
    // ?}
}
