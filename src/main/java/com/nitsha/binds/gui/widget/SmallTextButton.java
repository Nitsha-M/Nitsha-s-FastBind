package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.GuiGraphics;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class SmallTextButton extends AbstractButton {
    private static final ResourceLocation NORMAL = Main.id("textures/gui/btns/smallbtn_normal.png");
    private static final ResourceLocation HOVER = Main.id("textures/gui/btns/smallbtn_hover.png");
    private final Runnable onClick;
    private final MutableComponent name;
    private ResourceLocation icon;

    private int color;
    private int hoverColor;
    private int textColor;
    private int hoverTextColor;
    private final int iconSize;

    private int x, y, width, height;
    private boolean isEnabled = true;

    public SmallTextButton(MutableComponent name, int x, int y, int iconSize, int color, int hoverColor, int textColor, int hoverTextColor, int width, String align,
            ResourceLocation icon, Runnable onClick) {
        super(x, y, 0, 9, TextUtils.empty());
        this.name = name;
        this.onClick = onClick;
        this.icon = icon;
        this.y = y;
        this.color = color;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        this.hoverTextColor = hoverTextColor;
        this.iconSize = iconSize;

        Font font = Minecraft.getInstance().font;
        int iconWidth = (icon != null) ? iconSize + 1 : 0;
        int newWidth = iconWidth + font.width(name) + 4;
        this.width = (width > 0) ? width : newWidth;
        this.height = 9;

        switch (align) {
            case "left":
                this.x = x;
                break;
            case "right":
                this.x = x - getWidth();
                break;
        }
    }

    public SmallTextButton(MutableComponent name, int x, int y, int color, int width, String align, Runnable onClick) {
        this(name, x, y, 5, color, color, 0xFFFFFFFF, 0xFFFFFFFF, width, align, null, onClick);
    }

    public SmallTextButton(MutableComponent name, int x, int y, int color, String align, Runnable onClick) {
        this(name, x, y, 5, color, color, 0xFFFFFFFF, 0xFFFFFFFF, 0, align, null, onClick);
    }

    public SmallTextButton(MutableComponent name, int x, int y, int color, String align, ResourceLocation icon, Runnable onClick) {
        this(name, x, y, 5, color, color, 0xFFFFFFFF, 0xFFFFFFFF, 0, align, icon, onClick);
    }

    public SmallTextButton(MutableComponent name, int x, int y, int color, int width, String align, ResourceLocation icon, Runnable onClick) {
        this(name, x, y, 5, color, color, 0xFFFFFFFF, 0xFFFFFFFF, width, align, icon, onClick);
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setWidth(int newWidth) {
        this.width = newWidth;
    }

    public void setHeight(int newHeight) {
        this.height = newHeight;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }

    public void setColors(int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        this.color = btnColor;
        this.hoverColor = btnHoverColor;
        this.textColor = textColor;
        this.hoverTextColor = textHoverColor;
    }

    @Override
    public void onPress() {}

    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        this.isHovered = isMouseOver(mouseX, mouseY);
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(name);
        int iconWidth = (icon != null) ? this.iconSize + 1 : 0;

        GUIUtils.drawResizableBox(ctx, (isHovered && isEnabled) ? HOVER : NORMAL, getX(), getY(), getWidth(), getHeight(), 2, 5, (isHovered && isEnabled) ? hoverColor : color);

        int contentX = this.getX() + ((getWidth() / 2) - ((iconWidth + textWidth) / 2));

        if (icon != null) {
            GUIUtils.adaptiveDrawTexture(ctx, icon, contentX, this.getY() + ((this.height / 2) - (iconSize / 2)), 0, 0, iconSize, iconSize, iconSize, iconSize, (isHovered && isEnabled) ? hoverTextColor : textColor);
            contentX += iconWidth;
        }

        GUIUtils.addText(
                ctx, name, 0,
                contentX,
                this.getY() + (this.height / 2),
                "left", "center", (isHovered && isEnabled) ? hoverTextColor : textColor, false);

        if (!this.isEnabled) {
            GUIUtils.drawResizableBox(ctx, NORMAL, getX(), getY(), getWidth(), getHeight(), 2, 5, 0x66000000);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible
                && mouseX >= (double)this.getX() && mouseY >= (double)this.getY()
                && mouseX < (double)(this.getX() + this.getWidth())
                && mouseY < (double)(this.getY() + this.getHeight());
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        int button = event.buttonInfo().button();
        double mouseX = event.x();
        double mouseY = event.y();
        if (button == 0 && isMouseOver(mouseX, mouseY) && this.isEnabled) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onClick.run();
            return true;
        }
        return false;
    }*/
    //? } else {
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && isMouseOver(mouseX, mouseY) && this.isEnabled) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onClick.run();
                return true;
            }
            return false;
        }
    //? }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    //? } else if >=1.17 {
    /*@Override
     public void updateNarration(NarrationElementOutput builder) { }*/
    //? }
}