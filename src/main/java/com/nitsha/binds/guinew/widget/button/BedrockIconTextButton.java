package com.nitsha.binds.guinew.widget.button;

import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class BedrockIconTextButton extends BedrockButton {

    private final Identifier icon;
    private final MutableComponent text;

    private int xO = 0;
    private int yO = 0;

    private BedrockIconTextButton(Identifier icon, MutableComponent text, int x, int y, int width, int height, boolean isEnabled,
                                  Runnable onClick, int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        super(TextUtils.empty(), x, y, width, height, isEnabled, onClick, btnColor, btnHoverColor, textColor, textHoverColor);

        Font font = Minecraft.getInstance().font;
        this.xO = (width - 16 - font.width(text) - 2) / 2;
        this.yO = (height - 16) / 2;

        this.icon = icon;
        this.text = text;
    }

    public BedrockIconTextButton(Identifier icon, MutableComponent text, int x, int y, int width, int height, boolean isEnabled, Runnable onClick) {
        this(icon, text, x, y, width, height, isEnabled, onClick,
                0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    public BedrockIconTextButton(int x, int y, int width, int height, Identifier icon, MutableComponent text, boolean isEnabled, Runnable onClick) {
        this(icon, text, x, y, width, height, isEnabled, onClick,
                0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.onRender(ctx, mouseX, mouseY, delta);

        RenderUtils.drawTexture(ctx, icon, this.getX() + xO, this.getY() + yO + Math.round(this.getOffsetY()), 0, 0, 16, 14, 16, 14,
                (this.isEnabled()) ? (isHovered() || this.isPressed()) ? this.getTextHoverColor() : this.getTextColor() : 0x40FFFFFF);
        RenderUtils.drawText(ctx, this.text, getWidth(), this.getX() + xO + 18, this.getY() + yO + 7 + Math.round(this.getOffsetY()),
                "left", "center", (this.isEnabled()) ? (isHovered() || this.isPressed()) ? this.getTextHoverColor() : this.getTextColor() : this.getTextColor(), false);
    }
}