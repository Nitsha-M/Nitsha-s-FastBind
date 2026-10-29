package com.nitsha.binds.gui.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class SmallTextButton extends NitshaAbstractWidget {

    private Identifier NORMAL, HOVER;
    private final Runnable onClick;
    private final MutableComponent name;
    private Identifier icon;

    protected int color;
    protected int hoverColor;
    protected int textColor;
    protected int hoverTextColor;
    private final int iconSize;

    protected boolean isEnabled = true;
    protected boolean toggled = false;

    private String dir;

    public SmallTextButton(MutableComponent name, int x, int y, int iconSize, int color, int hoverColor, int textColor, int hoverTextColor, int width, String align,
                           Identifier icon, Runnable onClick) {
        super(x, y, 0, 9, TextUtils.empty());
        this.name = TextUtils.small(name);
        this.onClick = onClick;
        this.icon = icon;
        this.color = color;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        this.hoverTextColor = hoverTextColor;
        this.iconSize = iconSize;

        setButtonDirection("");

        Font font = Minecraft.getInstance().font;
        int iconWidth = (icon != null) ? iconSize + 1 : 0;
        int newWidth = iconWidth + font.width(this.name) + 4;
        this.width = (width > 0) ? width : newWidth;
        this.height = 9;

        switch (align) {
            case "left":
                this.setX(x);
                break;
            case "right":
                this.setX(x - getWidth());
                break;
        }
    }

    public void setButtonDirection(String dir) {
        this.dir = dir;
        NORMAL = Main.id("textures/gui/btns/smallbtn_normal" + dir + ".png");
        HOVER = Main.id("textures/gui/btns/smallbtn_hover" + dir + ".png");
    }

    public SmallTextButton(MutableComponent name, int x, int y, int color, int width, String align, Runnable onClick) {
        this(name, x, y, 5, color, color, 0xFFFFFFFF, 0xFFFFFFFF, width, align, null, onClick);
    }

    public SmallTextButton(MutableComponent name, int x, int y, int color, String align, Runnable onClick) {
        this(name, x, y, 5, color, color, 0xFFFFFFFF, 0xFFFFFFFF, 0, align, null, onClick);
    }

    public SmallTextButton(MutableComponent name, int x, int y, int color, String align, Identifier icon, Runnable onClick) {
        this(name, x, y, 5, color, color, 0xFFFFFFFF, 0xFFFFFFFF, 0, align, icon, onClick);
    }

    public SmallTextButton(MutableComponent name, int x, int y, int color, int width, String align, Identifier icon, Runnable onClick) {
        this(name, x, y, 5, color, color, 0xFFFFFFFF, 0xFFFFFFFF, width, align, icon, onClick);
    }

    public Identifier getHoverTexture() {
        return HOVER;
    }

    public Identifier getNormalTexture() {
        return NORMAL;
    }

    public boolean isEnabled() { return this.isEnabled; }
    public void setEnabled(boolean enabled) { this.isEnabled = enabled; }
    public void setIcon(Identifier icon) { this.icon = icon; }
    public boolean isToggled() { return toggled; }
    public void setToggled(boolean toggled) { this.toggled = toggled; }
    protected boolean isActiveState() {
        return (isHovered() && isEnabled) || toggled;
    }

    public void setColors(int btnColor, int btnHoverColor, int textColor, int textHoverColor) {
        this.color = btnColor;
        this.hoverColor = btnHoverColor;
        this.textColor = textColor;
        this.hoverTextColor = textHoverColor;
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.onRender(ctx, mouseX, mouseY, delta);

        if (!visible) return;

        renderContents(ctx, mouseX, mouseY, delta);
    }

    protected void renderContents(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(name);
        int iconWidth = (icon != null) ? this.iconSize + 1 : 0;

        RenderUtils.drawResizableBox(ctx, isActiveState() ? HOVER : NORMAL, getX(), getY(), getWidth(), getHeight(), 2, 5, isActiveState() ? hoverColor : color);

        int contentX = this.getX() + ((getWidth() / 2) - ((iconWidth + textWidth) / 2));

        if (icon != null) {
            RenderUtils.drawTexture(ctx, icon, contentX, this.getY() + ((this.height / 2) - (iconSize / 2)), 0, 0, iconSize, iconSize, iconSize, iconSize, isActiveState() ? hoverTextColor : textColor);
            contentX += iconWidth;
        }

        RenderUtils.drawText(ctx, name, 0, contentX, this.getY() + (this.height / 2), "left", "center", isActiveState() ? hoverTextColor : textColor, false);

        if (!this.isEnabled) {
            RenderUtils.drawResizableBox(ctx, NORMAL, getX(), getY(), getWidth(), getHeight(), 2, 5, 0x66000000);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible
                && mouseX >= (double) this.getX() && mouseY >= (double) this.getY()
                && mouseX < (double) (this.getX() + this.getWidth())
                && mouseY < (double) (this.getY() + this.getHeight());
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT && visible && isMouseOver(mouseX, mouseY) && this.isEnabled) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onClick.run();
            return true;
        }

        return false;
    }
}
