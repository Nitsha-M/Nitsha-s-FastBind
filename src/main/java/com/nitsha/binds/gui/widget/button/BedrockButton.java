package com.nitsha.binds.gui.widget.button;

import com.google.common.collect.Lists;
import com.nitsha.binds.FBLogger;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.DrawElement;
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

import java.util.List;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;*/
//? }

public class BedrockButton extends AbstractButton {

    public ResourceLocation BEDROCK_ATLAS = Main.id("textures/gui/btns/bedrock.png");

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

    private int outlineColor;
    private int outlineHoverColor;

    private int x, y;

    private int dir;

    private BedrockButton neighbor;
    private BedrockButton neighbor2;

    private DrawElement bottomLvl = null;
    private DrawElement topLvl = null;

    public BedrockButton(String name, int x, int y, int width, int height, boolean isEnabled, Runnable onClick, int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        super(x, y, width, height, TextUtils.empty());
        this.name = name;
        this.onClick = onClick;
        this.isEnabled = isEnabled;
        setColors(btnColor, btnHoverColor, textColor, textHoverColor);
        this.x = x;
        this.y = y;

        setButtonDirection(0);
    }

    public BedrockButton(String name, int x, int y, int width, int height, Runnable onClick) {
        this(name, x, y, width, height, true, onClick, 0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    public BedrockButton(String name, int x, int y, int width, int height, boolean isEnabled, Runnable onClick) {
        this(name, x, y, width, height, isEnabled, onClick, 0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
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
        setOutlineColor(0xFF000000, GUIUtils.darkenColor(btnHoverColor, 0.4f));
    }

    public void setOutlineColor(int outlineColor, int outlineHoverColor) {
        this.outlineColor = outlineColor;
        this.outlineHoverColor = outlineHoverColor;
    }

    public int getOutlineHoverColor() {
        return outlineHoverColor;
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

    @Override
    public void setX(int x) {
        super.setX(x);
        this.x = x;
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.y = y;
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
    //  if (isMouseOver(event.x(), event.y()) && isEnabled && isPressed) this.onClick.run();
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

    public void setBottomLvl(DrawElement bottomLvl) {
        this.bottomLvl = bottomLvl;
    }

    public void setTopLvl(DrawElement topLvl) {
        this.topLvl = topLvl;
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

        int xOff = 0;
        int wOff = 0;

        int oC = ((isHovered || isPressed) && isEnabled) ? outlineHoverColor : outlineColor;

        if (neighbor != null) {
            wOff = 1;
            int color = (neighbor.isHovered()) ? neighbor.getOutlineHoverColor() : outlineColor;
            if (dir == 1) {
                GUIUtils.drawFill(ctx, fX + fW - 1, fY + Math.round(neighbor.getOffsetY()), fX + fW, fY + fH, color);
            } else if (dir == 2) {
                xOff = 1;
                GUIUtils.drawFill(ctx, fX, fY + Math.round(neighbor.getOffsetY()), fX + 1, fY + fH, color);
            } else if (dir == 3) {
                wOff = 2;
                xOff = 1;
                GUIUtils.drawFill(ctx, fX + fW - 1, fY + Math.round(neighbor.getOffsetY()), fX + fW, fY + fH, color);
                GUIUtils.drawFill(ctx, fX, fY + Math.round(neighbor2.getOffsetY()), fX + 1, fY + fH, (neighbor2.isHovered()) ? neighbor2.getOutlineHoverColor() : outlineColor);
            }
        }

        int stateU = (isEnabled) ? 0 : 18;
        int stateV = dir * 9;

        GUIUtils.drawResizableBox(ctx, BEDROCK_ATLAS,
                fX + 1 - xOff, fY + Math.round(yOffset) + 1, fW - 2 + wOff, fH - Math.round(yOffset) - 2, stateU, stateV, 4, 64, 64, ((isHovered || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        if (bottomLvl != null) {
            bottomLvl.render(ctx, mouseX, mouseY);
        }

        GUIUtils.drawResizableBox(ctx, BEDROCK_ATLAS,
                fX + 1 - xOff, fY + Math.round(yOffset) + 1, fW - 2 + wOff, fH - 4, stateU + 9, stateV, 4, 64, 64, ((isHovered || isPressed) && isEnabled) ? btnHoverColor : btnColor);

        if (topLvl != null) {
            topLvl.render(ctx, mouseX, mouseY);
        }

        GUIUtils.addText(ctx, TextUtils.literal(name), 0,
                this.getX() + ((this.width / 2) - (textWidth / 2)),
                this.getY() + Math.round(yOffset) + ((this.height / 2)),
                "left", "center", (isEnabled && (isHovered || isPressed)) ? textHoverColor : textColor, false);

        GUIUtils.drawResizableBox(ctx, BEDROCK_ATLAS,
                fX, fY + Math.round(yOffset), fW, fH - Math.round(yOffset), 36, stateV, 2, 64, 64, oC);
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
            return true;
        }
        return false;
    }*/
    //? } else {
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!this.isEnabled() || !this.visible) return false;
        if (button == 0 && isPressed) {
            onRelease(mouseX, mouseY);
            return true;
        }
        return false;
    }
    //? }
}