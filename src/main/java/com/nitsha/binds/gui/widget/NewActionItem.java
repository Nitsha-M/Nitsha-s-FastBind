package com.nitsha.binds.gui.widget;

import com.nitsha.binds.gui.panels.NewAction;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nitsha.binds.gui.utils.TextUtils;
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
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class NewActionItem extends AbstractWidget {
    private final NewAction parent;

    private String name;

    private int x, y;

    private int type;
    private String value;
    private int color = 0xFFFFFFFF;

    public NewActionItem(NewAction parent, String name, int x, int y, int width, int height, int type, String value, int color) {
        super(x, y, width, height, TextUtils.empty());
        this.parent = parent;
        this.name = name;
        this.x = x;
        this.y = y;
        this.type = type;
        this.value = value;
        this.color = color;
    }

    public int getXPos() {
        return this.x;
    }

    public int getYPos() {
        return this.y;
    }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        GuiGraphics c = (GuiGraphics) ctx;
        //?} else {
        /*PoseStack c = (PoseStack) ctx;
         *///?}
        GUIUtils.drawFill(ctx, getXPos() + 3, getYPos() + 5, getXPos() + 5, getYPos() + 12, this.color);
        GUIUtils.drawFill(ctx, getXPos() + 2, getYPos(), getXPos() + getWidth() - 2, getYPos() + 1, 0xFF555555);
        GUIUtils.addText(c, TextUtils.literal(GUIUtils.truncateString(this.name, 25)), 0, getXPos() + 7, getYPos() + 4, "left", "top", 0xFFFFFFFF, false);
        if (this.isHovered && parent.isOpen())
            GUIUtils.drawFill(c, getXPos(), getYPos(), getXPos() + getWidth(), getYPos() + getHeight(), 0x0DFFFFFF);
    }

    //? if >1.20.2 {
    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    //?} else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?} else if >=1.19.4 {
    /*@Override
    public void renderWidget(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?} else {
    /*@Override
    public void renderButton(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    */
    //? }

    @Override
    //? <1.21.9 {
    public void onClick(double mouseX, double mouseY) {
    //? } else {
    // public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
    //? }
        parent.openSelector(false);
        parent.addAction(this.name, this.type, this.value);
    }

        @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (!parent.isOpen()) return false;
        return super.mouseClicked(event, bl);
    }*/
    //? } else {
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!parent.isOpen()) return false;
            return super.mouseClicked(mouseX, mouseY, button);
        }
        //? }
        @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        return super.mouseReleased(event);
    }*/
    //? } else {
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return super.mouseReleased(mouseX, mouseY, button);
        }
    //? }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    //?} else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) {
    }*/
    //?}
}
