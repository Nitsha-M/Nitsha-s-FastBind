package com.nitsha.binds.gui.widget;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.GUIUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
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
    private float speed = 0.8f;
    private int btnColor;
    private int btnHoverColor;
    private int textColor;
    private int textHoverColor;

    public BedrockButton(String name, int x, int y, int width, int height, boolean isEnabled, Runnable onClick, int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        super(x, y, width, height, Text.empty());
        this.name = name;
        this.onClick = onClick;
        this.isEnabled = isEnabled;
        this.btnColor = btnColor;
        this.btnHoverColor = btnHoverColor;
        this.textColor = textColor;
        this.textHoverColor = textHoverColor;
    }

    public BedrockButton(String name, int x, int y, int width, int height, Runnable onClick) {
        this(name, x, y, width, height, true, onClick, 0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    public BedrockButton(String name, int x, int y, int width, int height, boolean isEnabled, Runnable onClick) {
        this(name, x, y, width, height, isEnabled, onClick, 0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
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

    public void onRelease(double mouseX, double mouseY) {
        if (!isToggle) isPressed = false;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public float getOffsetY() {
        return yOffset;
    }

    private float clampSpeed(float value) {
        return MathHelper.clamp(value, 0.001f, 1.0f);
    }

    //? if >1.20.2 {
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    //? } else {
    /*@Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///? }

    private void rndr(DrawContext context, int mouseX, int mouseY, float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textWidth = textRenderer.getWidth(name);
        targetOffset = (isPressed) ? 2 : 0;
        yOffset = MathHelper.lerp(clampSpeed(speed * delta), yOffset, targetOffset);
        if (Math.abs(yOffset - targetOffset) < 0.001f) yOffset = targetOffset;

        int fX = this.getX() + 1;
        int fY = this.getY() + 1;
        int fW = this.getWidth() - 2;
        int fH = this.getHeight() - 2;

        // Border
        context.drawBorder(this.getX(), this.getY() + Math.round(yOffset), this.width, this.height - Math.round(yOffset), 0xFF000000);

        // Bottom texture
        GUIUtils.drawResizableBox(context, (!isEnabled) ? DISABLE : NORMAL, fX, fY + 2, fW, fH - 2, 3, 7, ((hovered || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        // Top texture
        GUIUtils.drawResizableBox(context, (!isEnabled) ? PRESSED_DISABLE : PRESSED_NORMAL, fX, fY + Math.round(yOffset), fW, fH - 2, 3, 7, ((hovered || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        context.drawText(
                textRenderer, Text.of(name),
                this.getX() + ((this.width / 2) - (textWidth / 2)),
                this.getY() + Math.round(yOffset) + ((this.height / 2) - (textRenderer.fontHeight / 2)),
                (isEnabled && (hovered || isPressed)) ? textHoverColor : textColor,
                false
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isEnabled() || !this.visible) return false;
        if (!this.isValidClickButton(button)) return false;

        if (this.isMouseOver(mouseX, mouseY)) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onClick(mouseX, mouseY);
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            this.onRelease(mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
