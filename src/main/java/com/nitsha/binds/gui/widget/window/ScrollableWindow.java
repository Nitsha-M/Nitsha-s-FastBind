package com.nitsha.binds.gui.widget.window;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.multiversions.NitshaContainer;
import com.nitsha.binds.multiversions.NitshaInput;
import com.nitsha.binds.multiversions.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;

public class ScrollableWindow extends NitshaContainer {

    private final boolean horizontal;
    private boolean showScrollbar = true;
    private boolean draggingScrollbar;

    private int scrollableArea;
    private int maxScroll;
    private int barSize;
    private int scrollBarOffset;
    private float smoothScrollOffset;
    private float targetScrollOffset;
    private double dragStartPosition;
    private float dragStartScrollOffset;
    private long lastRenderTime;

    public ScrollableWindow(int x, int y, int width, int height, boolean horizontal) {
        super(x, y, width, height);
        this.horizontal = horizontal;
        setVisible(true);
        updateScrollLogic();
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        updateScrollLogic();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        updateScrollLogic();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (!visible) {
            draggingScrollbar = false;
            lastRenderTime = 0;
        }
    }

    public void setShowScrollbar(boolean show) {
        showScrollbar = show;
    }

    public int getScrollOffset() {
        return Math.round(smoothScrollOffset);
    }

    public void setScrollOffset(int offset) {
        targetScrollOffset = Mth.clamp(offset, 0, maxScroll);
        smoothScrollOffset = targetScrollOffset;
        updateScrollbarPosition();
    }

    public void setScrollableArea(int scrollableArea) {
        this.scrollableArea = Math.max(0, scrollableArea);
        updateScrollLogic();
    }

    public void addScrollableArea(int delta) {
        setScrollableArea(scrollableArea + delta);
    }

    public void resetScroll() {
        setScrollOffset(0);
    }

    public void scrollToBottom() {
        setScrollOffset(maxScroll);
    }

    @Override
    public void clearChildren() {
        super.clearChildren();
        draggingScrollbar = false;
    }

    public void updateScrollLogic() {
        int trackSize = Math.max(0, horizontal ? getWidth() : getHeight());
        int trackLength = Math.max(0, trackSize - 2);
        maxScroll = Math.max(0, scrollableArea - trackSize);
        barSize = scrollableArea > 0 ? Math.min(trackLength, Math.max(20, Math.round(trackSize / (float) scrollableArea * trackLength))) : trackLength;
        targetScrollOffset = Mth.clamp(targetScrollOffset, 0, maxScroll);
        smoothScrollOffset = Mth.clamp(smoothScrollOffset, 0, maxScroll);
        updateScrollbarPosition();
    }

    private int getScrollbarTravel() {
        return Math.max(0, (horizontal ? getWidth() : getHeight()) - 2 - barSize);
    }

    private void updateScrollbarPosition() {
        scrollBarOffset = maxScroll > 0 ? Math.round(smoothScrollOffset / maxScroll * getScrollbarTravel()) : 0;
    }

    private boolean hasScrollbar() {
        return showScrollbar && maxScroll > 0;
    }

    private int getViewportWidth() {
        return Math.max(0, getWidth());
    }

    private int getViewportHeight() {
        return Math.max(0, getHeight());
    }

    private boolean isInsideViewport(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX < getX() + getWidth() && mouseY >= getY() && mouseY < getY() + getHeight();
    }

    @Override
    public boolean isMouseInside(double mouseX, double mouseY) {
        return isVisible() && isInsideViewport(mouseX, mouseY) && !isInsideScrollbar(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isVisible() && isInsideViewport(mouseX, mouseY);
    }

    @Override
    protected int getContentX() {
        return getX() - (horizontal ? getScrollOffset() : 0);
    }

    @Override
    protected int getContentY() {
        return getY() - (horizontal ? 0 : getScrollOffset());
    }

    public int getMaxScroll() {
        return maxScroll;
    }

    private void animateScroll() {
        long now = System.nanoTime();
        double elapsedFrames = lastRenderTime == 0 ? 1.0 : Math.max(0.0, (now - lastRenderTime) / (1_000_000_000.0 / 60.0));
        lastRenderTime = now;
        float factor = (float) (1.0 - Math.pow(0.8, elapsedFrames));
        smoothScrollOffset = Mth.lerp(factor, smoothScrollOffset, targetScrollOffset);

        if (Math.abs(smoothScrollOffset - targetScrollOffset) < 0.5F) {
            smoothScrollOffset = targetScrollOffset;
        }

        updateScrollbarPosition();
    }

    @Override
    public void renderWindow(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        if (!isVisible()) {
            lastRenderTime = 0;
            return;
        }

        animateScroll();

        if (getViewportWidth() > 0 && getViewportHeight() > 0) {
            int contentMouseX = isMouseInside(mouseX, mouseY) ? mouseX : -10000;
            int contentMouseY = isMouseInside(mouseX, mouseY) ? mouseY : -10000;

            RenderUtils.customScissor(ctx, getX(), getY(), getViewportWidth(), getViewportHeight(), () -> {
                super.renderWindow(ctx, contentMouseX, contentMouseY, delta);
            });
        }

        if (hasScrollbar()) {
            drawScrollbar(ctx, mouseX, mouseY);
        }
    }

    @Override
    protected void renderChild(GuiGraphicsExtractor ctx, GuiEventListener child, int contentX, int contentY, int mouseX, int mouseY, float delta) {
        if (child instanceof AbstractWidget widget) {
            int start = horizontal ? widget.getX() : widget.getY();
            int size = horizontal ? widget.getWidth() : widget.getHeight();
            int viewportSize = horizontal ? getViewportWidth() : getViewportHeight();

            if (start + size < getScrollOffset() || start > getScrollOffset() + viewportSize) {
                return;
            }
        }

        super.renderChild(ctx, child, contentX, contentY, mouseX, mouseY, delta);
    }

    private void drawScrollbar(GuiGraphicsExtractor ctx, int mouseX, int mouseY) {
        boolean highlighted = draggingScrollbar || isInsideScrollbar(mouseX, mouseY);
        int color = highlighted ? 0x80000000 : 0x40000000;

        if (horizontal) {
            int barX = getX() + 1 + scrollBarOffset;
            int barY = getY() + getHeight() - 4;
            RenderUtils.drawFill(ctx, barX, barY + (highlighted ? 0 : 1), barX + barSize, barY + (highlighted ? 4 : 3), color);
        } else {
            int barX = getX() + getWidth() - 4;
            int barY = getY() + 1 + scrollBarOffset;
            RenderUtils.drawFill(ctx, barX + (highlighted ? 0 : 1), barY, barX + (highlighted ? 4 : 3), barY + barSize, color);
        }
    }

    private boolean isInsideScrollbar(double mouseX, double mouseY) {
        if (!hasScrollbar() || barSize <= 0) return false;

        int barX = horizontal ? getX() + 1 + scrollBarOffset : getX() + getWidth() - 4;
        int barY = horizontal ? getY() + getHeight() - 4 : getY() + 1 + scrollBarOffset;
        int barWidth = horizontal ? barSize : 4;
        int barHeight = horizontal ? 4 : barSize;
        return mouseX >= barX && mouseX < barX + barWidth && mouseY >= barY && mouseY < barY + barHeight;
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!canClick()) return false;

        if (button == InputConstants.MOUSE_BUTTON_LEFT && isInsideScrollbar(mouseX, mouseY)) {
            draggingScrollbar = true;
            dragStartPosition = horizontal ? mouseX : mouseY;
            dragStartScrollOffset = smoothScrollOffset;
            return true;
        }

        return isMouseInside(mouseX, mouseY) && super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        boolean releasedScrollbar = button == InputConstants.MOUSE_BUTTON_LEFT && draggingScrollbar;

        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            draggingScrollbar = false;
        }

        boolean releasedChild = super.onMouseReleased(mouseX, mouseY, button);
        return releasedScrollbar || releasedChild;
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isVisible()) return false;

        if (draggingScrollbar && button == InputConstants.MOUSE_BUTTON_LEFT) {
            int travel = getScrollbarTravel();

            if (travel > 0) {
                double position = horizontal ? mouseX : mouseY;
                targetScrollOffset = (float) Mth.clamp(dragStartScrollOffset + (position - dragStartPosition) * maxScroll / travel, 0.0, maxScroll);
                smoothScrollOffset = targetScrollOffset;
                updateScrollbarPosition();
            }

            return true;
        }

        return super.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isVisible() || !isInsideViewport(mouseX, mouseY)) return false;

        if (isMouseInside(mouseX, mouseY) && super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }

        double amount = horizontal && horizontalAmount != 0.0 ? horizontalAmount : verticalAmount;

        if (amount == 0.0 || maxScroll == 0) return false;

        int speed = NitshaInput.hasShiftDown() ? 40 : 20;
        float nextOffset = (float) Mth.clamp(targetScrollOffset - amount * speed, 0.0, maxScroll);

        if (nextOffset == targetScrollOffset) return false;

        targetScrollOffset = nextOffset;
        return true;
    }
}
