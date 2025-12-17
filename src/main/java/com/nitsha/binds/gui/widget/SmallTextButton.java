package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import com.mojang.blaze3d.vertex.PoseStack;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiComponent;
 *///?}
    //? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class SmallTextButton extends AbstractWidget {
    private static final ResourceLocation NORMAL = Main.id("textures/gui/btns/smallbtn_normal.png");
    private static final ResourceLocation HOVER = Main.id("textures/gui/btns/smallbtn_hover.png");
    private final Runnable onClick;
    private final MutableComponent name;
    private final int color;
    private final ResourceLocation icon;

    private int x, y, width, height;

    public SmallTextButton(MutableComponent name, int x, int y, int color, int width, String align,
            ResourceLocation icon, Runnable onClick) {
        super(x, y, 0, 9, TextUtils.empty());
        this.name = name;
        this.onClick = onClick;
        this.color = color;
        this.icon = icon;
        this.y = y;

        Font font = Minecraft.getInstance().font;
        int iconWidth = (icon != null) ? 5 + 1 : 0; // 5px icon + 1px spacing
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
        this(name, x, y, color, width, align, null, onClick);
    }

    public SmallTextButton(MutableComponent name, int x, int y, int color, String align, Runnable onClick) {
        this(name, x, y, color, 0, align, null, onClick);
    }

    public SmallTextButton(MutableComponent name, int x, int y, int color, String align, ResourceLocation icon, Runnable onClick) {
        this(name, x, y, color, 0, align, icon, onClick);
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

    //? if >1.20.2 {
    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    //? } else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float
    delta) {
    rndr(context, mouseX, mouseY, delta);
    }*/
    //? } else if >=1.19.4 {
    /*@Override
    public void renderWidget(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }*/
    //? } else {
    /*@Override
    public void renderButton(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }*/
    //? }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(name);
        int iconWidth = (icon != null) ? 5 + 1 : 0;

        GUIUtils.drawResizableBox(ctx, (isHovered) ? HOVER : NORMAL, getX(), getY(), getWidth(), getHeight(), 2, 5,
                color);

        int contentX = this.getX() + ((getWidth() / 2) - ((iconWidth + textWidth) / 2));

        // Render icon if present
        if (icon != null) {
            GUIUtils.adaptiveDrawTexture(ctx, icon, contentX, this.getY() + 2, 0, 0, 5, 5, 5, 5);
            contentX += 6;
        }

        // Render text
        GUIUtils.addText(
                ctx, name, 0,
                contentX,
                this.getY() + ((this.height / 2) - (font.lineHeight / 2)),
                "top", "left", 0xFFFFFFFF, false);
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
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onClick.run();
            return true;
        }
        return false;
    }*/
    //? } else {
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && isMouseOver(mouseX, mouseY)) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onClick.run();
                return true;
            }
            return false;
        }
    //? }

    // ? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    // ?} else if >=1.17 {
    /*
     @Override
     public void updateNarration(NarrationElementOutput builder) {
     }
     */
    // ?}
}