package com.nitsha.binds.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.GUIUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

public class BedrockIconButton extends BedrockButton {
    private final Identifier ICON;
    private boolean itemIcon = false;
    private ItemStack iI;
    private int xO = 0;
    private int yO = 0;

    private BedrockIconButton(String iconName, ItemStack stack, int x, int y, int width, int height, boolean isEnabled,
                              Runnable onClick, int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        super("", x, y, width, height, isEnabled, onClick, btnColor, btnHoverColor, textColor, textHoverColor);

        this.xO = (width - 16) / 2;
        this.yO = (height - 16) / 2;

        if (iconName != null) {
            this.ICON = MainClass.id("textures/gui/sprites/" + iconName + ".png");
            this.itemIcon = false;
            this.iI = null;
        } else {
            this.ICON = null;
            this.itemIcon = true;
            this.iI = stack;
        }
    }

    public BedrockIconButton(int x, int y, int width, int height, String iconName, boolean isEnabled, Runnable onClick) {
        this(iconName, null, x, y, width, height, isEnabled, onClick,
                0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    public BedrockIconButton(int x, int y, int width, int height, String iconName, boolean isEnabled, Runnable onClick,
                             int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        this(iconName, null, x, y, width, height, isEnabled, onClick,
                btnColor, btnHoverColor, textColor, textHoverColor);
    }

    public BedrockIconButton(int x, int y, int width, int height, boolean isEnabled, Runnable onClick, ItemStack icon) {
        this(null, icon, x, y, width, height, isEnabled, onClick,
                0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    public BedrockIconButton(int x, int y, int width, int height, boolean isEnabled, Runnable onClick, ItemStack icon,
                             int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        this(null, icon, x, y, width, height, isEnabled, onClick,
                btnColor, btnHoverColor, textColor, textHoverColor);
    }



    //? if >1.20.2 {
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    //? } else {
    /*@Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    *///? }

    protected void renderOverlay(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!itemIcon) GUIUtils.adaptiveDrawTexture(context, ICON, this.getX() + xO, this.getY() + yO + Math.round(this.getOffsetY()), 0, 0, 16, 14, 16, 14, (this.isEnabled()) ? (hovered || this.isPressed()) ? this.getTextHoverColor() : this.getTextColor() : 0x40FFFFFF);
        if (itemIcon) {
            float scale = 0.8f;

            //? if >=1.21.6 {
            /*Matrix3x2fStack matrices = context.getMatrices();
            Matrix3x2f saved = new Matrix3x2f(matrices);

            matrices.scale(scale, scale);
            matrices.translate(
                    (this.getX() + (xO / scale)) / scale,
                    (this.getY() + ((yO + Math.round(this.getOffsetY())) / scale)) / scale
            );
            *///? } else {
            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.scale(scale, scale, 1.0f);
            matrices.translate(
                    (this.getX() + (xO / scale)) / scale,
                    (this.getY() + ((yO + Math.round(this.getOffsetY())) / scale)) / scale,
                    0
            );
            //? }

            context.drawItem(this.iI, 0, 0);

            //? if >=1.21.6 {
            /*matrices.set(saved);
            *///? } else {
            matrices.pop();
            //? }
        }
    }
}
