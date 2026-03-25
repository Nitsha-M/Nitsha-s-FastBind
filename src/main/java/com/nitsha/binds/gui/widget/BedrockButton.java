package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;*/
//? }

public class BedrockButton extends AbstractButton {
    private ResourceLocation NORMAL = Main.id("textures/gui/btns/bedrock_normal_bottom.png");
    private final ResourceLocation DISABLE = Main.id("textures/gui/btns/bedrock_disabled_bottom.png");
    private ResourceLocation PRESSED_NORMAL = Main.id("textures/gui/btns/bedrock_normal_top.png");
    private final ResourceLocation PRESSED_DISABLE = Main.id("textures/gui/btns/bedrock_disabled_top.png");
    private final Runnable onClick;

    private String name;
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

    private int x, y;

    public BedrockButton(String name, int x, int y, int width, int height, boolean isEnabled, Runnable onClick, int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        super(x, y, width, height, TextUtils.empty());
        this.name = name;
        this.onClick = onClick;
        this.isEnabled = isEnabled;
        this.btnColor = btnColor;
        this.btnHoverColor = btnHoverColor;
        this.textColor = textColor;
        this.textHoverColor = textHoverColor;
        this.x = x;
        this.y = y;
    }

    public BedrockButton(String name, int x, int y, int width, int height, Runnable onClick) {
        this(name, x, y, width, height, true, onClick, 0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    public BedrockButton(String name, int x, int y, int width, int height, boolean isEnabled, Runnable onClick) {
        this(name, x, y, width, height, isEnabled, onClick, 0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
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

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
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

    @Override
    public void onPress() {
        if (isEnabled) isPressed = true;
    }

    @Override
    //? if >=1.21.9 {
    // public void onRelease(MouseButtonEvent event) {
    //  if (isMouseOver(event.x(), event.y())) this.onClick.run();
    //? } else {
    public void onRelease(double mouseX, double mouseY) {
        if (isMouseOver(mouseX, mouseY) && isEnabled && isPressed) this.onClick.run();
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

    public int getHeight() {
        return this.height;
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
        boolean isHovered = isMouseOver(mouseX, mouseY);
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(name);
        targetOffset = (isPressed) ? 2 : 0;
        yOffset = Mth.lerp(GUIUtils.clampSpeed(speed * delta), yOffset, targetOffset);
        if (Math.abs(yOffset - targetOffset) < 0.001f) yOffset = targetOffset;

        int fX = this.getX();
        int fY = this.getY();
        int fW = this.getWidth();
        int fH = this.getHeight();


        // Bottom texture
        GUIUtils.drawResizableBox(ctx, (!isEnabled) ? DISABLE : NORMAL, fX, fY + 2, fW, fH - 2, 5, 11, ((isHovered || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        // Top texture
        GUIUtils.drawResizableBox(ctx, (!isEnabled) ? PRESSED_DISABLE : PRESSED_NORMAL, fX, fY + Math.round(yOffset), fW, fH - 2, 5, 11, ((isHovered || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        GUIUtils.addText(ctx, TextUtils.literal(name), 0,
                this.getX() + ((this.width / 2) - (textWidth / 2)),
                this.getY() + Math.round(yOffset) + ((this.height / 2)),
                "left", "center", (isEnabled && (isHovered || isPressed)) ? textHoverColor : textColor, false);
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
        return super.mouseClicked(event, bl);
    }*/
    //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isEnabled() || !this.visible) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    //? }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        if (!this.isEnabled() || !this.visible) return false;
        if (event.button() == 0 && isPressed) {
            onRelease(event);
        }
        return super.mouseReleased(event);
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
}