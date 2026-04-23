package com.nitsha.binds.gui.widget;

import com.google.common.collect.Lists;
import com.nitsha.binds.FBLogger;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.DrawElement;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import com.nitsha.binds.utils.Renderable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class ScrollableWindow extends AbstractContainerEventHandler
        implements Renderable, GuiEventListener /*? if >=1.17 { */ , NarratableEntry /*? } */ {
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

    private float smoothScrollOffset = 0;
    private int targetScrollOffset = 0;

    private int x, y, realX, realY, width, height;

    private boolean isVisible = true;
    private boolean showScrollbar = true;

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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void setShowScrollbar(boolean show) {
        showScrollbar = show;
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

    public void setHeight(int height) {
        this.height = height;
        updateScrollLogic();
    }

    public void setRealY(int rY) {
        this.realY = rY;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(int offset) {
        this.scrollOffset = offset;
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
        drawElements.clear();
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

    //? if >=26.1 {
    // public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
    //? } else {
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        //? }
        renderWindow(ctx, mouseX, mouseY, delta);
    }

    public void renderWindow(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        if (!isVisible()) return;
        smoothScrollOffset = Mth.lerp(0.2f, smoothScrollOffset, targetScrollOffset);
        if (Math.abs(smoothScrollOffset - targetScrollOffset) < 0.5f) smoothScrollOffset = targetScrollOffset;

        scrollOffset = (int) smoothScrollOffset;

        int trackSize = horizontal ? width : height;
        float scrollProgress = maxScroll > 0 ? smoothScrollOffset / maxScroll : 0f;
        scrollBarOffset = (int) (scrollProgress * (trackSize - 2 - barSize));

        int aX = (this.horizontal) ? this.getX() - (int) smoothScrollOffset : this.getX();
        int aY = (!this.horizontal) ? this.getY() - (int) smoothScrollOffset : this.getY();

        int mX = (!isMouseInside(mouseX, mouseY)) ? -10000 : mouseX - aX;
        int mY = (!isMouseInside(mouseX, mouseY)) ? -10000 : mouseY - aY;

        int[] renderStats = new int[]{0};
        String[] hoverStats = new String[]{""};

        GUIUtils.matricesUtil(ctx, aX, aY, 1, () -> {
            drawElements.forEach(element -> element.render(ctx, mX, mY));
            //? if >=26.1 {
            /*for (GuiEventListener child : children) {
                if (child instanceof net.minecraft.client.gui.components.AbstractWidget w) {
                    if (horizontal) {
                        if (w.getX() + w.getWidth() < smoothScrollOffset || w.getX() > smoothScrollOffset + this.width) continue;
                    } else {
                        if (w.getY() + w.getHeight() < smoothScrollOffset || w.getY() > smoothScrollOffset + this.height) continue;
                    }
                    w.extractRenderState(ctx, mX, mY, delta);
                    renderStats[0]++;
                    if (w.isMouseOver(mX, mY)) hoverStats[0] = "Hover Y: " + w.getY() + " | mY: " + mY;
                } else {
                    Renderable r = RenderUtils.wrapRenderable(child);
                    if (r != null) { r.extractRenderState(ctx, mX, mY, delta); renderStats[0]++; }
                }
            }*/
            //? } else {
            for (GuiEventListener child : children) {
                if (child instanceof net.minecraft.client.gui.components.AbstractWidget w) {
                    if (horizontal) {
                        if (w.getX() + w.getWidth() < smoothScrollOffset || w.getX() > smoothScrollOffset + this.width) continue;
                    } else {
                        if (w.getY() + w.getHeight() < smoothScrollOffset || w.getY() > smoothScrollOffset + this.height) continue;
                    }
                    w.render(ctx, mX, mY, delta);
                    renderStats[0]++;
                    if (w.isMouseOver(mX, mY)) hoverStats[0] = "Hover Y: " + w.getY() + " | mY: " + mY;
                } else {
                    Renderable r = RenderUtils.wrapRenderable(child);
                    if (r != null) { r.render(ctx, mX, mY, delta); renderStats[0]++; }
                }
            }
            //? }
        });

        GUIUtils.matricesUtil(ctx, 0, 0, 500, () -> {
            // Text was drawn out of clipping bounds (scissored), so logging to console instead:
            if (System.currentTimeMillis() % 1000 < 15) {
                int totalW = 0;
                int hoveredY = -1;
                for (GuiEventListener child : children) {
                    if (child instanceof net.minecraft.client.gui.components.AbstractWidget w) {
                        totalW++;
                        if (w.isMouseOver(mX, mY)) hoveredY = w.getY();
                    }
                }
            }
        });

        if (this.showScrollbar && this.scrollableArea > trackSize) {
            GUIUtils.matricesUtil(ctx, 0, 0, 2, () -> {
                boolean isHoveringScrollbar = isInsideScrollbar(mouseX, mouseY);
                int scrollbarColor = (isHoveringScrollbar) ? 0x80000000 : 0x40000000;
                
                if (this.horizontal) {
                    int scrollbarX = this.getX() + 1 + scrollBarOffset;
                    int scrollbarY = this.getY() + this.height - 4;
                    GUIUtils.drawFill(ctx, scrollbarX, scrollbarY + (isHoveringScrollbar ? 0 : 1),
                            scrollbarX + barSize, scrollbarY + (isHoveringScrollbar ? 4 : 3), scrollbarColor);
                } else {
                    int scrollbarX = this.getX() + this.width - 4;
                    int scrollbarY = this.getY() + 1 + scrollBarOffset;
                    GUIUtils.drawFill(ctx, scrollbarX + (isHoveringScrollbar ? 0 : 1), scrollbarY,
                            scrollbarX + (isHoveringScrollbar ? 4 : 3), scrollbarY + barSize, scrollbarColor);
                }
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

    //? if >=1.17 {
    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return NarratableEntry.NarrationPriority.NONE;
    }
    //?}

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
        int trackSize = horizontal ? width : height;
        this.maxScroll = Math.max(0, scrollableArea - trackSize);

        int trackActiveBound = trackSize - 2;
        if (scrollableArea > 0) {
            this.barSize = Math.max(20, (int) ((trackSize / (float) scrollableArea) * trackActiveBound));
        } else {
            this.barSize = trackActiveBound;
        }
        int scrollArea = trackActiveBound - barSize;
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        targetScrollOffset = Math.max(0, Math.min(targetScrollOffset, maxScroll));

        float scrollProgress = maxScroll > 0 ? scrollOffset / (float) maxScroll : 0f;
        scrollBarOffset = (int) (scrollProgress * scrollArea);
    }

    private boolean scrollLogic(double mouseX, double mouseY, double amount) {
        if (!isMouseInside(mouseX, mouseY))
            return false;
        //? if >=1.21.9 {
        // Window window = Minecraft.getInstance().getWindow();
        //? } else {
        long window = Minecraft.getInstance().getWindow().getWindow();
        //? }
        boolean shift = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
        int scrollSpeed = (shift) ? 40 : 20;
        targetScrollOffset = Mth.clamp(targetScrollOffset - ((int) amount * scrollSpeed), 0, maxScroll);

        return true;
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isVisible()) return false;
        return scrollLogic(mouseX, mouseY, verticalAmount);
    }
    //?} else {
    /*
     @Override
     public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isVisible()) return false;
        return scrollLogic(mouseX, mouseY, amount);
     }
     *///?}

    private boolean isInsideScrollbar(double mouseX, double mouseY) {
        if (!showScrollbar) return false;
        int trackSize = horizontal ? width : height;
        if (scrollableArea <= trackSize) return false;
        
        if (horizontal) {
            int scrollbarX = this.getX() + 1 + scrollBarOffset;
            int scrollbarY = this.getY() + this.height - 4;
            return mouseX >= scrollbarX && mouseX <= scrollbarX + barSize &&
                   mouseY >= scrollbarY && mouseY <= scrollbarY + 4;
        } else {
            int scrollbarX = this.getX() + this.width - 4;
            int scrollbarY = this.getY() + 1 + scrollBarOffset;
            return mouseX >= scrollbarX && mouseX <= scrollbarX + 4 &&
                   mouseY >= scrollbarY && mouseY <= scrollbarY + barSize;
        }
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (!isVisible()) return false;
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.buttonInfo().button();
        int aX = (this.horizontal) ? this.getX() - scrollOffset : this.getX();
        int aY = (!this.horizontal) ? this.getY() - scrollOffset : this.getY();

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(
            event.x() - aX,
            event.y() - aY,
            event.buttonInfo()
        );

        boolean clicked = false;

        if (button == 0 && isInsideScrollbar(mouseX, mouseY)) {
            isDraggingScrollbar = true;
            dragStartY = (int) mouseY;
            dragStartScrollOffset = targetScrollOffset;
            clicked = true;
        }

        if (isMouseInside(mouseX, mouseY)) {
            for (GuiEventListener element : new ArrayList<>(this.children())) {
                if (element.mouseClicked(adjustedEvent, bl)) {
                    this.setFocused(element);
                    if (button == 0) {
                        this.setDragging(true);
                    }
                    clicked = true;
                }
            }
        }
        return clicked;
    }*/
    //? } else {
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isVisible()) return false;
            int aX = (this.horizontal) ? this.getX() - scrollOffset : this.getX();
            int aY = (!this.horizontal) ? this.getY() - scrollOffset : this.getY();

            boolean clicked = false;

            if (button == 0 && isInsideScrollbar(mouseX, mouseY)) {
                isDraggingScrollbar = true;
                dragStartY = horizontal ? (int) mouseX : (int) mouseY;
                dragStartScrollOffset = targetScrollOffset;
                clicked = true;
            }

            if (isMouseInside(mouseX, mouseY)) {
                for (GuiEventListener element : new ArrayList<>(this.children())) {
                    if (element instanceof net.minecraft.client.gui.components.AbstractWidget) {
                        net.minecraft.client.gui.components.AbstractWidget w = (net.minecraft.client.gui.components.AbstractWidget) element;
                        if (horizontal) {
                            if (w.getX() + w.getWidth() < scrollOffset || w.getX() > scrollOffset + this.width) continue;
                        } else {
                            if (w.getY() + w.getHeight() < scrollOffset || w.getY() > scrollOffset + this.height) continue;
                        }
                    }
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
    //? }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        if (!isVisible()) return false;
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.buttonInfo().button();
        int aX = (this.horizontal) ? this.getX() - scrollOffset : this.getX();
        int aY = (!this.horizontal) ? this.getY() - scrollOffset : this.getY();
        if (button == 0 && isDraggingScrollbar) {
            isDraggingScrollbar = false;
        }
        for (GuiEventListener child : new ArrayList<>(children)) {
                child.mouseReleased(new MouseButtonEvent(mouseX - aX, mouseY - aY, event.buttonInfo()));
        }
        return false;
    }*/
    //? } else {
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (!isVisible()) return false;
            int aX = (this.horizontal) ? this.getX() - scrollOffset : this.getX();
            int aY = (!this.horizontal) ? this.getY() - scrollOffset : this.getY();
            if (button == 0 && isDraggingScrollbar) {
                isDraggingScrollbar = false;
            }
            for (GuiEventListener child : new ArrayList<>(children)) {
                if (child instanceof net.minecraft.client.gui.components.AbstractWidget) {
                    net.minecraft.client.gui.components.AbstractWidget w = (net.minecraft.client.gui.components.AbstractWidget) child;
                    if (horizontal) {
                        if (w.getX() + w.getWidth() < scrollOffset || w.getX() > scrollOffset + this.width) continue;
                    } else {
                        if (w.getY() + w.getHeight() < scrollOffset || w.getY() > scrollOffset + this.height) continue;
                    }
                }
                child.mouseReleased(mouseX - aX, mouseY - aY, button);
            }
            return false;
        }
    //? }

        @Override
    //? if >=1.21.9 {
    /*public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (!isVisible()) return false;
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.buttonInfo().button();

        int aX = (this.horizontal) ? this.getX() - scrollOffset : this.getX();
        int aY = (!this.horizontal) ? this.getY() - scrollOffset : this.getY();

        MouseButtonEvent adjustedEvent = new MouseButtonEvent(
            event.x() - aX,
            event.y() - aY,
            event.buttonInfo()
        );

        boolean clicked = false;

        if (isDraggingScrollbar && button == 0) {
            int trackHeight = height - 2;
            int scrollArea = trackHeight - barSize;

            int dy = (int) mouseY - dragStartY;

            float scrollProgress = (float) dy / scrollArea;
            targetScrollOffset = Mth.clamp(dragStartScrollOffset + Math.round(scrollProgress * maxScroll), 0, maxScroll);

            clicked = true;
        }

        for (GuiEventListener child : new ArrayList<>(children)) {
            if (child.mouseDragged(adjustedEvent, deltaX, deltaY)) {
                clicked = true;
            }
        }
        return clicked;
    }*/
    //? } else {
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (!isVisible()) return false;
            int aX = (this.horizontal) ? this.getX() - scrollOffset : this.getX();
            int aY = (!this.horizontal) ? this.getY() - scrollOffset : this.getY();

            boolean clicked = false;

            if (isDraggingScrollbar && button == 0) {
                int trackSize = horizontal ? width : height;
                int trackActiveBound = trackSize - 2;
                int scrollArea = trackActiveBound - barSize;

                int delta = horizontal ? (int) mouseX - dragStartY : (int) mouseY - dragStartY;

                float scrollProgress = (float) delta / scrollArea;
                targetScrollOffset = Mth.clamp(dragStartScrollOffset + Math.round(scrollProgress * maxScroll), 0, maxScroll);

                clicked = true;
            }

            for (GuiEventListener child : new ArrayList<>(children)) {
                if (child instanceof net.minecraft.client.gui.components.AbstractWidget) {
                    net.minecraft.client.gui.components.AbstractWidget w = (net.minecraft.client.gui.components.AbstractWidget) child;
                    if (horizontal) {
                        if (w.getX() + w.getWidth() < scrollOffset || w.getX() > scrollOffset + this.width) continue;
                    } else {
                        if (w.getY() + w.getHeight() < scrollOffset || w.getY() > scrollOffset + this.height) continue;
                    }
                }
                if (child.mouseDragged(mouseX - aX, mouseY - aY, button, deltaX, deltaY)) {
                    clicked = true;
                }
            }
            return clicked;
        }
    //? }

        @Override
    //? if >=1.21.9 {
    /*public boolean keyPressed(KeyEvent event) {
        if (!isVisible()) return false;
        for (GuiEventListener child : this.children) {
            if (child.keyPressed(event))
                return true;
        }
        return super.keyPressed(event);
    }*/
    //? } else {
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!isVisible()) return false;
            for (GuiEventListener child : this.children) {
                if (child.keyPressed(keyCode, scanCode, modifiers))
                    return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    //? }

        @Override
    //? if >=1.21.9 {
    /*public boolean charTyped(CharacterEvent event) {
        if (!isVisible()) return false;
        for (GuiEventListener child : new ArrayList<>(children)) {
            if (child.charTyped(event)) {
                return true;
            }
        }
        return super.charTyped(event);
    }*/
    //? } else {
        public boolean charTyped(char codePoint, int modifiers) {
            if (!isVisible()) return false;
            for (GuiEventListener child : new ArrayList<>(children)) {
                if (child.charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
            return super.charTyped(codePoint, modifiers);
        }
    //? }

    //? if >=1.17 {
    @Override
    public void updateNarration(NarrationElementOutput builder) {
    }
    //?}
}
