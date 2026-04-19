package com.nitsha.binds.gui.widget;

import com.nitsha.binds.FBLogger;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;*/
//? }

public class Slider extends AbstractButton {
    private ResourceLocation NORMAL = Main.id("textures/gui/btns/bedrock_normal_bottom.png");
    private final ResourceLocation DISABLE = Main.id("textures/gui/btns/bedrock_disabled_bottom.png");
    private ResourceLocation PRESSED_NORMAL = Main.id("textures/gui/btns/bedrock_normal_top.png");
    private final ResourceLocation PRESSED_DISABLE = Main.id("textures/gui/btns/bedrock_disabled_top.png");

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

    private int sliderOffset = 0;

    private int x, y, width, height;

    private float value, max, min;

    public Slider(int x, int y, int width, int height, boolean isEnabled, float min, float max, float value) {
        super(x, y, width, height, TextUtils.empty());
        this.isEnabled = isEnabled;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
    }

    public void setNormalTextures(ResourceLocation t1, ResourceLocation t2) {
        NORMAL = t1;
        PRESSED_NORMAL = t2;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
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

    @Override
    //? if >=1.21.9 {
    // public void onPress(InputWithModifiers inputWithModifiers) {
    //? } else {
    public void onPress() {
    //? }
    }

    @Override
    //? if >=1.21.9 {
    // public void onRelease(MouseButtonEvent event) {
    //? } else {
    public void onRelease(double mouseX, double mouseY) {
        //? }
        if (!isToggle) isPressed = false;
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

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int sliderH = this.height;

        this.isHovered = isMouseOver(mouseX, mouseY);

        targetOffset = (isPressed) ? 2 : 0;
        yOffset = Mth.lerp(GUIUtils.clampSpeed(speed * delta), yOffset, targetOffset);
        if (Math.abs(yOffset - targetOffset) < 0.001f) yOffset = targetOffset;

        int fX = this.getX();
        int fY = this.getY();
        int fW = this.getWidth();

        int outlineColor = (isHovered || isPressed) ? 0xFFFFFFFF : 0xFF000000;
        GUIUtils.drawFill(ctx, fX + 1, fY, fX + getWidth() - 1, fY + sliderH, outlineColor);
        GUIUtils.drawFill(ctx, fX, fY +  1, fX + getWidth(), fY + sliderH - 1, outlineColor);
        GUIUtils.drawFill(ctx, fX + 1, fY + 1, fX + getWidth() - 1, fY + sliderH - 1, 0xFF212121);

        // fill line
        GUIUtils.drawFill(ctx, fX + 1, fY + 1, fX + sliderOffset + 3, fY + sliderH - 1, 0xFF3C8527);

        GUIUtils.drawFill(ctx, fX + 1, fY + 1, fX + sliderOffset + 3, fY + 2, 0xFF639D52);
        GUIUtils.drawFill(ctx, fX + 1, fY + 2, fX + 2, fY + sliderH - 2, 0xFF639D52);
        GUIUtils.drawFill(ctx, fX + 1, fY + sliderH - 2, fX + 2, fY + sliderH - 1, 0xFF72A763);
        GUIUtils.drawFill(ctx, fX + 2, fY + sliderH - 2, fX + sliderOffset + 3, fY + sliderH - 1, 0xFF4F913C);

        // Bottom texture
        GUIUtils.drawResizableBox(ctx, (!isEnabled) ? DISABLE : NORMAL, fX + 1 + sliderOffset, fY, 10, sliderH - 1, 5, 11, ((isInsideKnob(mouseX, mouseY) || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        // Top texture
        GUIUtils.drawResizableBox(ctx, (!isEnabled) ? PRESSED_DISABLE : PRESSED_NORMAL, fX + 1 + sliderOffset, fY + Math.round(yOffset) - 2, 10, sliderH - 1, 5, 11, ((isInsideKnob(mouseX, mouseY) || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        int scrollbarColor = (isInsideKnob(mouseX, mouseY) || isPressed) ? 0xFFFFFFFF : 0xFF8B8B8B;
        GUIUtils.drawFill(ctx,
                fX + 1 + sliderOffset + 3,
                fY + Math.round(yOffset) + 2,
                fX + 1 + sliderOffset + 7,
                fY + Math.round(yOffset) + 3,
                scrollbarColor);

        GUIUtils.drawFill(ctx,
                fX + 1 + sliderOffset + 3,
                fY + Math.round(yOffset) + 4,
                fX + 1 + sliderOffset + 7,
                fY + Math.round(yOffset) + 5,
                scrollbarColor);

        GUIUtils.drawFill(ctx,
                fX + 1 + sliderOffset + 3,
                fY + Math.round(yOffset) + 6,
                fX + 1 + sliderOffset + 7,
                fY + Math.round(yOffset) + 7,
                scrollbarColor);
    }

    public boolean isInsideKnob(double mouseX, double mouseY) {
        int knobWidth = 10;
        int knobHeight = this.height + 1;
        int knobY = this.getY() - 2;
        int knobX = this.getX() + 1 + sliderOffset;
        return mouseX >= knobX && mouseX <= knobX + knobWidth && mouseY >= knobY && mouseY <= knobY + knobHeight;
    }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    //?} else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) {
    }*/
    //?}

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (!this.isEnabled() || !this.visible) return false;
        if (event.button() != 0) return false;

        if (isInsideKnob(event.x(), event.y())) {
            isPressed = true;
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return false;
    }*/
    //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isEnabled() || !this.visible) return false;
        if (button != 0) return false;

        if (isInsideKnob(mouseX, mouseY)) {
            isPressed = true;
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        return false;
    }
    //? }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        if (!this.isEnabled() || !this.visible) return false;
        if (event.button() == 0 && isPressed) {
            onRelease(event);
        }
        return false;
    }*/
    //? } else {
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!this.isEnabled() || !this.visible) return false;
        if (button == 0 && isPressed) {
            onRelease(mouseX, mouseY);
        }
        return false;
    }
    //? }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        FBLogger.info("edcce3222");
        if (!isPressed || event.button() != 0) return false;

        int knobWidth = 10;
        int trackX = this.getX() + 1;
        int trackWidth = this.getWidth() - 2;
        int scrollArea = trackWidth - knobWidth;

        sliderOffset = Mth.clamp((int) event.x() - trackX - knobWidth / 2, 0, scrollArea);

        float t = scrollArea > 0 ? sliderOffset / (float) scrollArea : 0.0f;
        value = Math.round((min + t * (max - min)) * 10f) / 10f;

        return true;
    }*/
    //? } else {
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isPressed || button != 0) return false;

        int knobWidth = 10;
        int trackX = this.getX() + 1;
        int trackWidth = this.getWidth() - 2;
        int scrollArea = trackWidth - knobWidth;

        sliderOffset = Mth.clamp((int) mouseX - trackX - knobWidth / 2, 0, scrollArea);

        float t = scrollArea > 0 ? sliderOffset / (float) scrollArea : 0.0f;
        value = Math.round((min + t * (max - min)) * 10f) / 10f;

        return true;
    }
    //? }
}