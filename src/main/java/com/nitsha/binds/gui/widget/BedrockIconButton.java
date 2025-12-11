package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
 *///?}
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class BedrockIconButton extends BedrockButton {
    private final ResourceLocation ICON;
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
            this.ICON = Main.id("textures/gui/sprites/" + iconName + ".png");
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
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    //? } else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    *///? } else if >=1.19.4 {
    /*@Override
    public void render(PoseStack context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    *///?} else {
    /*@Override
    public void render(PoseStack context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    */
    //? }

    protected void renderOverlay(Object ctx, int mouseX, int mouseY, float delta) {
        if (!itemIcon) {
            GUIUtils.adaptiveDrawTexture(ctx, ICON, this.getX() + xO, this.getY() + yO + Math.round(this.getOffsetY()), 0, 0, 16, 14, 16, 14,
                    (this.isEnabled()) ? (isHovered || this.isPressed()) ? this.getTextHoverColor() : this.getTextColor() : 0x40FFFFFF);
        }
        if (itemIcon) {
            float scale = 0.8f;
            GUIUtils.matricesScale(ctx, scale, () -> {
                GUIUtils.matricesUtil(ctx, (this.getX() + (xO / scale)) / scale, (this.getY() + ((yO + Math.round(this.getOffsetY())) / scale)) / scale, 0, () -> {
                    GUIUtils.drawItem(ctx, this.iI, 0, 0);
                });
            });
        }
    }
}