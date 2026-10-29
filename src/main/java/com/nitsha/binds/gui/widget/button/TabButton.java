package com.nitsha.binds.gui.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class TabButton extends NitshaAbstractWidget {

    private final Consumer<TabButton> onClick;
    private final Component name;
    private boolean selected;
    private float speed = Main.GLOBAL_ANIMATION_SPEED;
    private float pointY = 0.0f;

    private static final int FADE_WIDTH = 8;
    private ScrollableWindow scrollWindow;

    public TabButton(int x, int y, boolean selected, Component name, Consumer<TabButton> onClick) {
        super(x, y, 0, 16, TextUtils.empty());
        this.onClick = onClick;
        this.selected = selected;
        this.name = TextUtils.small(name);

        String userLanguage = RenderUtils.getUL();
        int widthOffset = 10;
        if (userLanguage.equals("ja_jp") || userLanguage.equals("ru_ru")) {
            widthOffset = 6;
        }

        int width = Minecraft.getInstance().font.width(this.name) + widthOffset;
        this.setWidth(width);
    }

    public void setScrollWindow(ScrollableWindow scrollWindow) {
        this.scrollWindow = scrollWindow;
    }

    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.onRender(ctx, mouseX, mouseY, delta);

        if (!visible) return;

        float targetY = selected ? getY() : getY() + 2;
        pointY = Mth.lerp(RenderUtils.clampSpeed(speed * delta), pointY, targetY);

        int renderY = Math.round(pointY);

        if (scrollWindow == null || scrollWindow.getMaxScroll() == 0) {
            drawTab(ctx, renderY, 255);
            return;
        }

        int offset = scrollWindow.getScrollOffset();
        int maxScroll = scrollWindow.getMaxScroll();
        int viewportLeft = offset;
        int viewportRight = viewportLeft + scrollWindow.getWidth();
        int visibleLeft = Math.max(getX(), viewportLeft);
        int visibleRight = Math.min(getX() + getWidth(), viewportRight);

        int x = visibleLeft;

        while (x < visibleRight) {
            int opacity = getFadeAlpha(x, viewportLeft, viewportRight, offset, maxScroll);
            int end = x + 1;

            while (end < visibleRight && getFadeAlpha(end, viewportLeft, viewportRight, offset, maxScroll) == opacity) {
                end++;
            }

            int stripX = x;
            int stripWidth = end - x;

            if (opacity > 0) {
                RenderUtils.customScissor(ctx, stripX, renderY, stripWidth, getHeight() + 10, () -> {
                    drawTab(ctx, renderY, opacity);
                });
            }

            x = end;
        }
    }

    private int getFadeAlpha(int pixelX, int left, int right, int offset, int maxScroll) {
        float leftStrength = Mth.clamp(offset / (float) FADE_WIDTH, 0.0F, 1.0F);
        float rightStrength = Mth.clamp((maxScroll - offset) / (float) FADE_WIDTH, 0.0F, 1.0F);

        float leftGradient = Mth.clamp((pixelX - left + 0.5F) / FADE_WIDTH, 0.0F, 1.0F);
        float rightGradient = Mth.clamp((right - pixelX - 0.5F) / FADE_WIDTH, 0.0F, 1.0F);

        float leftOpacity = 1.0F - leftStrength * (1.0F - leftGradient);
        float rightOpacity = 1.0F - rightStrength * (1.0F - rightGradient);

        return Math.round(Math.min(leftOpacity, rightOpacity) * 255.0F);
    }

    private void drawTab(GuiGraphicsExtractor ctx, int renderY, int opacity) {
        int backgroundColor = (opacity << 24) | 0x00FFFFFF;
        int textColor = (opacity << 24) | (selected ? 0x00FFFFFF : 0x00A8A8A8);

        RenderUtils.drawResizableBox(ctx, Textures.BACKGROUND_DARK, getX(), renderY, getWidth(), getHeight() + 10, 7, 15, backgroundColor);
        RenderUtils.drawText(ctx, name, getWidth(), getX(), renderY + 5, textColor, "center", "top");
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || button != InputConstants.MOUSE_BUTTON_LEFT || !isMouseOver(mouseX, mouseY)) {
            return false;
        }

        playDownSound(Minecraft.getInstance().getSoundManager());

        if (onClick != null) {
            onClick.accept(this);
        }

        return true;
    }
}