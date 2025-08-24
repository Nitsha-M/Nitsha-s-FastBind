package com.nitsha.binds.gui.widget;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.utils.GUIUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
//? if >=1.17 {
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
//? }
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BedrockButton extends ClickableWidget {
    private static final Identifier NORMAL = MainClass.id("textures/gui/btns/button_normal.png");
    private static final Identifier DISABLE = MainClass.id("textures/gui/btns/button_disable.png");
    private static final Identifier PRESSED_NORMAL = MainClass.id("textures/gui/btns/button_pressed_normal.png");
    private static final Identifier PRESSED_DISABLE = MainClass.id("textures/gui/btns/button_pressed_disable.png");
    private final Runnable onClick;

    private final String name;
    private boolean isEnabled = false;
    private boolean isPressed = false;
    private boolean isToggle = false;

    private float yOffset = 0;
    private float targetOffset = 0;
    private float speed = MainClass.GLOBAL_ANIMATION_SPEED + 0.2f;
    private int btnColor;
    private int btnHoverColor;
    private int textColor;
    private int textHoverColor;

    private int x, y;

    public BedrockButton(String name, int x, int y, int width, int height, boolean isEnabled, Runnable onClick, int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        super(x, y, width, height, Text.of(""));
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

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getTextHoverColor() {
        return textHoverColor;
    }

    public void onClick(double mouseX, double mouseY) {
        this.onClick.run();
        if (isEnabled) isPressed = true;
    }

    public void onRelease(double mouseX, double mouseY) {
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

    //? if >1.20.2 {
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    //? } else if >=1.20{
    /*@Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///? } else {
    /*@Override
    public void renderButton(MatrixStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///? }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        DrawContext c = (DrawContext) ctx;
        //? } else {
        /*MatrixStack c = (MatrixStack) ctx;
         *///? }
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textWidth = textRenderer.getWidth(name);
        targetOffset = (isPressed) ? 2 : 0;
        yOffset = MathHelper.lerp(GUIUtils.clampSpeed(speed * delta), yOffset, targetOffset);
        if (Math.abs(yOffset - targetOffset) < 0.001f) yOffset = targetOffset;

        int fX = this.getX() + 1;
        int fY = this.getY() + 1;
        int fW = this.getWidth() - 2;
        int fH = this.getHeight() - 2;

        // Border
        //? if >=1.20 {
        c.drawBorder(this.getX(), this.getY() + Math.round(yOffset), this.width, this.height - Math.round(yOffset), 0xFF000000);
        //? } else {
        /*DrawableHelper.fill(c, this.getX(), this.getY() + Math.round(yOffset),
        this.getX() + this.width, this.getY() + Math.round(yOffset) + 1, 0xFF000000);
        DrawableHelper.fill(c, this.getX(), this.getY() + this.height - 1,
                this.getX() + this.width, this.getY() + this.height, 0xFF000000);
        DrawableHelper.fill(c, this.getX(), this.getY() + Math.round(yOffset),
                this.getX() + 1, this.getY() + this.height, 0xFF000000);
        DrawableHelper.fill(c, this.getX() + this.width - 1, this.getY() + Math.round(yOffset),
                this.getX() + this.width, this.getY() + this.height, 0xFF000000);
        *///? }

        // Bottom texture
        GUIUtils.drawResizableBox(c, (!isEnabled) ? DISABLE : NORMAL, fX, fY + 2, fW, fH - 2, 3, 7, ((hovered || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        // Top texture
        GUIUtils.drawResizableBox(c, (!isEnabled) ? PRESSED_DISABLE : PRESSED_NORMAL, fX, fY + Math.round(yOffset), fW, fH - 2, 3, 7, ((hovered || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        GUIUtils.addText(c, Text.of(name), 0,
                this.getX() + ((this.width / 2) - (textWidth / 2)),
                this.getY() + Math.round(yOffset) + ((this.height / 2) - (textRenderer.fontHeight / 2)),
                "top", "left", (isEnabled && (hovered || isPressed)) ? textHoverColor : textColor, false);
    }

    //? if >=1.19.3 {
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
    //? } else if >=1.17 {
    /*@Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }*/
    //? }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isEnabled() || !this.visible) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
