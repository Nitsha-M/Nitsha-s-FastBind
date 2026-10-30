package com.nitsha.binds.gui.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.DrawElement;
import com.nitsha.binds.multiversions.TextUtils;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public class BedrockButton extends NitshaAbstractWidget {

    private final Runnable onClick;

    private MutableComponent text;
    private boolean isEnabled = false;
    private boolean isPressed = false;
    private boolean isToggle = false;
    private Component hoverTooltip;
    private boolean hoverTooltipEnabled;

    private float yOffset = 0;
    private float targetOffset = 0;
    private float speed = Main.GLOBAL_ANIMATION_SPEED + 0.2f;
    private int btnColor;
    private int btnHoverColor;
    private int textColor;
    private int textHoverColor;

    private int outlineColor;
    private int outlineHoverColor;
    private int dir;

    private BedrockButton neighbor;
    private BedrockButton neighbor2;

    private DrawElement bottomLvl = null;
    private DrawElement topLvl = null;

    public BedrockButton(MutableComponent text, int x, int y, int width, int height, boolean isEnabled, Runnable onClick, int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        super(x, y, width, height, TextUtils.empty());
        this.text = text;
        this.onClick = onClick;
        this.isEnabled = isEnabled;
        setColors(btnColor, btnHoverColor, textColor, textHoverColor);

        setButtonDirection(0);
    }

    public BedrockButton(MutableComponent text, int x, int y, int width, int height, Runnable onClick) {
        this(text, x, y, width, height, true, onClick, 0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    public BedrockButton(MutableComponent text, int x, int y, int width, int height, boolean isEnabled, Runnable onClick) {
        this(text, x, y, width, height, isEnabled, onClick, 0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    public void setNeighbor(BedrockButton neighbor) {
        this.neighbor = neighbor;
    }

    public void setNeighbor2(BedrockButton neighbor) {
        this.neighbor2 = neighbor;
    }

    public void setButtonDirection(int dir) {
        this.dir = Mth.clamp(dir, 0, 3);
    }

    public void setColors(int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        this.btnColor = btnColor;
        this.btnHoverColor = btnHoverColor;
        this.textColor = textColor;
        this.textHoverColor = textHoverColor;
        setOutlineColor(0xFF000000, RenderUtils.darkenColor(btnHoverColor, 0.4f));
    }

    public void setOutlineColor(int outlineColor, int outlineHoverColor) {
        this.outlineColor = outlineColor;
        this.outlineHoverColor = outlineHoverColor;
    }

    public int getOutlineHoverColor() {
        return outlineHoverColor;
    }

    public void setText(MutableComponent text) {
        this.text = text;
    }

    public void setHoverTooltip(Component tooltip) {
        hoverTooltip = tooltip;
        hoverTooltipEnabled = tooltip != null;
    }

    public void setHoverTooltipEnabled(boolean enabled) {
        hoverTooltipEnabled = enabled;
    }

    protected Component getHoverTooltip() {
        return hoverTooltip;
    }

    protected void renderHoverTooltip(GuiGraphicsExtractor ctx, int mouseX, int mouseY) {
        if (!visible || !isHovered() || !hoverTooltipEnabled) return;

        Component tooltip = getHoverTooltip();
        if (tooltip == null || tooltip.getString().isEmpty()) return;

        int transparentColor = (getBtnColor() & 0x00FFFFFF) | 0xCC000000;
        RenderUtils.queueHoverTooltip(tooltip, transparentColor);
    }

    public int getBtnColor() {
        return btnColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getTextHoverColor() {
        return textHoverColor;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enable) {
        isEnabled = enable;
    }

    public void setPressed(boolean pressed) {
        isToggle = pressed;
        isPressed = pressed;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public float getOffsetY() {
        return yOffset;
    }

    public void setBottomLvl(DrawElement bottomLvl) {
        this.bottomLvl = bottomLvl;
    }

    public void setTopLvl(DrawElement topLvl) {
        this.topLvl = topLvl;
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.onRender(ctx, mouseX, mouseY, delta);
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);
        targetOffset = (isPressed) ? 2 : 0;
        yOffset = Mth.lerp(RenderUtils.clampSpeed(speed * delta), yOffset, targetOffset);
        if (Math.abs(yOffset - targetOffset) < 0.001f) yOffset = targetOffset;

        int fX = this.getX();
        int fY = this.getY();
        int fW = this.getWidth();
        int fH = this.getHeight();

        int xOff = 0;
        int wOff = 0;

        int oC = (isHovered() || isPressed) ? outlineHoverColor : outlineColor;

        if (neighbor != null) {
            wOff = 1;
            int color = (neighbor.isHovered()) ? neighbor.getOutlineHoverColor() : outlineColor;
            if (dir == 1) {
                RenderUtils.drawFill(ctx, fX + fW - 1, fY + Math.round(neighbor.getOffsetY()), fX + fW, fY + fH, color);
            } else if (dir == 2) {
                xOff = 1;
                RenderUtils.drawFill(ctx, fX, fY + Math.round(neighbor.getOffsetY()), fX + 1, fY + fH, color);
            } else if (dir == 3) {
                wOff = 2;
                xOff = 1;
                RenderUtils.drawFill(ctx, fX + fW - 1, fY + Math.round(neighbor.getOffsetY()), fX + fW, fY + fH, color);
                RenderUtils.drawFill(ctx, fX, fY + Math.round(neighbor2.getOffsetY()), fX + 1, fY + fH, (neighbor2.isHovered()) ? neighbor2.getOutlineHoverColor() : outlineColor);
            }
        }

        int stateU = (isEnabled) ? 0 : 18;
        int stateV = dir * 9;

        RenderUtils.drawResizableBox(ctx, Textures.BEDROCK_ATLAS,
                fX + 1 - xOff, fY + Math.round(yOffset) + 1, fW - 2 + wOff, fH - Math.round(yOffset) - 2, stateU, stateV, 4, 64, 64, ((isHovered() || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        if (bottomLvl != null) {
            bottomLvl.render(ctx, mouseX, mouseY);
        }

        RenderUtils.drawResizableBox(ctx, Textures.BEDROCK_ATLAS,
                fX + 1 - xOff, fY + Math.round(yOffset) + 1, fW - 2 + wOff, fH - 4, stateU + 9, stateV, 4, 64, 64, ((isHovered() || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        if (topLvl != null) {
            topLvl.render(ctx, mouseX, mouseY);
        }

        RenderUtils.drawText(ctx, text, 0,
                this.getX() + ((this.width / 2) - (textWidth / 2)),
                this.getY() + Math.round(yOffset) + ((this.height / 2) - 1),
                "left", "center", (isEnabled && (isHovered() || isPressed)) ? textHoverColor : textColor, false);

        RenderUtils.drawResizableBox(ctx, Textures.BEDROCK_ATLAS,
                fX, fY + Math.round(yOffset), fW, fH - Math.round(yOffset), 36, stateV, 2, 64, 64, oC);

        renderHoverTooltip(ctx, mouseX, mouseY);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!isEnabled || !visible || button != InputConstants.MOUSE_BUTTON_LEFT || !isMouseOver(mouseX, mouseY)) {
            return false;
        }

        isPressed = true;
        playDownSound(Minecraft.getInstance().getSoundManager());
        return true;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (!this.isEnabled() || !this.visible) {
            return false;
        }

        if (button != InputConstants.MOUSE_BUTTON_LEFT || !isPressed) {
            return false;
        }

        if (isMouseOver(mouseX, mouseY)) {
            this.onClick.run();
        }

        if (!isToggle) {
            isPressed = false;
        }

        return true;
    }
}
