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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BedrockIconTextButton extends BedrockButton {
    private final Identifier ICON;
    private final String text;

    private int xO = 0;
    private int yO = 0;

    private BedrockIconTextButton(String iconName, String text, int x, int y, int width, int height, boolean isEnabled,
                                  Runnable onClick, int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        super("", x, y, width, height, isEnabled, onClick, btnColor, btnHoverColor, textColor, textHoverColor);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        this.xO = (width - 16 - textRenderer.getWidth(text) - 2) / 2;
        this.yO = (height - 16) / 2;

        this.ICON = MainClass.id("textures/gui/sprites/" + iconName + ".png");
        this.text = text;
    }

    public BedrockIconTextButton(int x, int y, int width, int height, String iconName, String text, boolean isEnabled, Runnable onClick) {
        this(iconName, text, x, y, width, height, isEnabled, onClick,
                0xFFFFFFFF, 0xFF3C8527, 0xFF212121, 0xFFFFFFFF);
    }

    //? if >1.20.2 {
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    //? } else if >=1.20{
    /*@Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    *///? } else {
    /*@Override
    public void renderButton(MatrixStack context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    *///? }

    protected void renderOverlay(Object ctx, int mouseX, int mouseY, float delta) {
        GUIUtils.adaptiveDrawTexture(ctx, ICON, this.getX() + xO, this.getY() + yO + Math.round(this.getOffsetY()), 0, 0, 16, 14, 16, 14, (this.isEnabled()) ? (hovered || this.isPressed()) ? this.getTextHoverColor() : this.getTextColor() : 0x40FFFFFF);
        GUIUtils.addText(ctx, Text.of(this.text), getWidth(), this.getX() + xO + 18, this.getY() + yO + 7 + Math.round(this.getOffsetY()), "left", "center", (this.isEnabled()) ? (hovered || this.isPressed()) ? this.getTextHoverColor() : this.getTextColor() : this.getTextColor(), false);
    }
}
