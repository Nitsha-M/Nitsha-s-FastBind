package com.nitsha.binds.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

public class Slider extends NitshaAbstractWidget {

    private boolean isEnabled = false;
    private boolean isPressed = false;
    private boolean isToggle = false;

    private float yOffset = 0;
    private float targetOffset = 0;
    private float speed = Main.GLOBAL_ANIMATION_SPEED + 0.2f;
    private int btnColor;
    private int btnHoverColor;
    private int textColor;
    private int textHoverColor;

    private int outlineColor;
    private int outlineHoverColor;

    private int sliderOffset = 0;

    private float value, max, min;

    public Slider(int x, int y, int width, int height, boolean isEnabled, float min, float max, float value) {
        super(x, y, width, height, TextUtils.empty());
        this.isEnabled = isEnabled;
        this.min = min;
        this.max = max;
        this.value = value;

        int knobWidth = 10;
        int trackWidth = this.getWidth() - 2;
        int scrollArea = trackWidth - knobWidth;

        float t = (value - min) / (max - min);
        t = Mth.clamp(t, 0.0f, 1.0f);

        sliderOffset = Math.round(t * scrollArea);

        this.setColors(0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
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

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = Mth.clamp(value, min, max);

        int knobWidth = 10;
        int trackWidth = this.getWidth() - 2;
        int scrollArea = trackWidth - knobWidth;

        float t = (this.value - min) / (max - min);
        t = Mth.clamp(t, 0.0f, 1.0f);

        sliderOffset = Math.round(t * scrollArea);
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

    public float getMax() {
        return max;
    }

    public float getMin() {
        return min;
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

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        int sliderH = this.height;

        this.isHovered = isMouseOver(mouseX, mouseY);

        targetOffset = (isPressed) ? 2 : 0;
        yOffset = Mth.lerp(RenderUtils.clampSpeed(speed * delta), yOffset, targetOffset);
        if (Math.abs(yOffset - targetOffset) < 0.001f) yOffset = targetOffset;

        int fX = this.getX();
        int fY = this.getY();
        int fW = this.getWidth();

        int oC = (isHovered || isPressed || isInsideKnob(mouseX, mouseY)) ? outlineHoverColor : outlineColor;

        RenderUtils.drawFill(ctx, fX + 1, fY, fX + getWidth() - 1, fY + sliderH, oC);
        RenderUtils.drawFill(ctx, fX, fY +  1, fX + getWidth(), fY + sliderH - 1, oC);
        RenderUtils.drawFill(ctx, fX + 1, fY + 1, fX + getWidth() - 1, fY + sliderH - 1, 0xFF212121);

        // fill line
        RenderUtils.drawFill(ctx, fX + 1, fY + 1, fX + sliderOffset + 3, fY + sliderH - 1, 0xFF3C8527);

        RenderUtils.drawFill(ctx, fX + 1, fY + 1, fX + sliderOffset + 3, fY + 2, 0xFF639D52);
        RenderUtils.drawFill(ctx, fX + 1, fY + 2, fX + 2, fY + sliderH - 2, 0xFF639D52);
        RenderUtils.drawFill(ctx, fX + 1, fY + sliderH - 2, fX + 2, fY + sliderH - 1, 0xFF72A763);
        RenderUtils.drawFill(ctx, fX + 2, fY + sliderH - 2, fX + sliderOffset + 3, fY + sliderH - 1, 0xFF4F913C);

        renderDivisions(ctx);

        boolean knobHighlighted = isInsideKnob(mouseX, mouseY) || isPressed;
        int knobColor = (knobHighlighted && isEnabled) ? btnHoverColor : btnColor;
        int knobX = fX + sliderOffset;
        int knobY = fY + Math.round(yOffset) - 2;

        RenderUtils.drawResizableBox(ctx, Textures.BEDROCK_ATLAS, knobX + 1, knobY + 1, 10, sliderH - Math.round(yOffset), 0, 0, 4, 64, 64, knobColor);
        RenderUtils.drawResizableBox(ctx, Textures.BEDROCK_ATLAS, knobX + 1, knobY + 1, 10, sliderH - 2, 9, 0, 4, 64, 64, knobColor);
        RenderUtils.drawResizableBox(ctx, Textures.BEDROCK_ATLAS, knobX, knobY, 12, sliderH + 2 - Math.round(yOffset), 36, 0, 2, 64, 64, knobHighlighted ? 0xFFFFFFFF : oC);

        int scrollbarColor = knobHighlighted ? 0xFFFFFFFF : 0xFF8B8B8B;
        RenderUtils.drawFill(ctx,
                fX + 1 + sliderOffset + 3,
                fY + Math.round(yOffset) + 2,
                fX + 1 + sliderOffset + 7,
                fY + Math.round(yOffset) + 3,
                scrollbarColor);

        RenderUtils.drawFill(ctx,
                fX + 1 + sliderOffset + 3,
                fY + Math.round(yOffset) + 5,
                fX + 1 + sliderOffset + 7,
                fY + Math.round(yOffset) + 6,
                scrollbarColor);
//
//        RenderUtils.drawFill(ctx,
//                fX + 1 + sliderOffset + 3,
//                fY + Math.round(yOffset) + 6,
//                fX + 1 + sliderOffset + 7,
//                fY + Math.round(yOffset) + 7,
//                scrollbarColor);
    }

    protected void renderDivisions(GuiGraphicsExtractor ctx) {
    }

    public boolean isInsideKnob(double mouseX, double mouseY) {
        int knobWidth = 12;
        int knobHeight = this.height + 2 - Math.round(yOffset);
        int knobY = this.getY() + Math.round(yOffset) - 2;
        int knobX = this.getX() + sliderOffset;
        return mouseX >= knobX && mouseX <= knobX + knobWidth && mouseY >= knobY && mouseY <= knobY + knobHeight;
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isEnabled() || !this.visible) return false;
        if (button != InputConstants.MOUSE_BUTTON_LEFT) return false;

        if (isInsideKnob(mouseX, mouseY)) {
            isPressed = true;
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        } else if (isMouseOver(mouseX, mouseY)){
            int knobWidth = 10;
            int trackX = this.getX() + 1;
            int trackWidth = this.getWidth() - 2;
            int scrollArea = trackWidth - knobWidth;

            sliderOffset = Mth.clamp((int) mouseX - trackX - knobWidth / 2, 0, scrollArea);

            float t = scrollArea > 0 ? sliderOffset / (float) scrollArea : 0.0f;
            value = Math.round((min + t * (max - min)) * 10f) / 10f;

            isPressed = true;
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (!this.isEnabled() || !this.visible) return false;
        if (button == InputConstants.MOUSE_BUTTON_LEFT && isPressed) {
            if (!isToggle) {
                isPressed = false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isPressed || button != InputConstants.MOUSE_BUTTON_LEFT) return false;

        int knobWidth = 10;
        int trackX = this.getX() + 1;
        int scrollArea = Math.max(0, this.getWidth() - 2 - knobWidth);

        sliderOffset = Mth.clamp((int) mouseX - trackX - knobWidth / 2, 0, scrollArea);

        float t = scrollArea > 0 ? sliderOffset / (float) scrollArea : 0.0F;
        value = Math.round((min + t * (max - min)) * 10F) / 10F;

        return true;
    }
}
