package com.nitsha.binds.guinew.widget.button;

import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class BedrockIconButton extends BedrockButton {

    private Identifier icon;
    private ItemStack itemStack;
    private int xO = 0;
    private int yO = 0;

    private BedrockIconButton(Identifier icon, ItemStack stack, int x, int y, int width, int height, boolean isEnabled,
                              Runnable onClick, int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        super(TextUtils.empty(), x, y, width, height, isEnabled, onClick, btnColor, btnHoverColor, textColor, textHoverColor);
        this.xO = (width - 16) / 2;
        this.yO = (height - 16) / 2;
        if (icon != null) {
            this.icon = icon;
            this.itemStack = null;
        } else {
            this.icon = null;
            this.itemStack = stack;
        }
    }

    public BedrockIconButton(int x, int y, int width, int height, Identifier icon, boolean isEnabled, Runnable onClick) {
        this(icon, null, x, y, width, height, isEnabled, onClick, 0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    public BedrockIconButton(int x, int y, int width, int height, Identifier icon, boolean isEnabled, Runnable onClick,
                             int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        this(icon, null, x, y, width, height, isEnabled, onClick, btnColor, btnHoverColor, textColor, textHoverColor);
    }

    public BedrockIconButton(int x, int y, int width, int height, boolean isEnabled, Runnable onClick, ItemStack icon) {
        this(null, icon, x, y, width, height, isEnabled, onClick, 0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    public BedrockIconButton(int x, int y, int width, int height, boolean isEnabled, Runnable onClick, ItemStack icon,
                             int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        this(null, icon, x, y, width, height, isEnabled, onClick, btnColor, btnHoverColor, textColor, textHoverColor);
    }

    public void setIcon(Identifier icon) {
        this.icon = icon;
        this.itemStack = null;
    }

    public void setItemStack(ItemStack itemStack) {
        this.icon = null;
        this.itemStack = itemStack;
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.onRender(ctx, mouseX, mouseY, delta);

        if (itemStack == null) {
            RenderUtils.drawTexture(ctx, icon, this.getX() + xO, this.getY() + yO + Math.round(this.getOffsetY()), 0, 0, 16, 14, 16, 14,
                    (this.isEnabled()) ? (isHovered || this.isPressed()) ? this.getTextHoverColor() : this.getTextColor() : 0x40FFFFFF);
        } else {
            float scale = 0.8f;
            RenderUtils.matricesScale(ctx, scale, () -> {
                RenderUtils.matricesUtil(ctx, (this.getX() + (xO / scale)) / scale, (this.getY() + ((yO + Math.round(this.getOffsetY())) / scale)) / scale, 0, () -> {
                    RenderUtils.drawItem(ctx, this.itemStack, 0, 0);
                });
            });
        }
    }
}