package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.panels.NewAction;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class NewActionItem extends AbstractButton {
    private final NewAction parent;

    private final String name;
    private final String typeId;
    private final String value;
    private final int color;

    private int x, y;

    private static final ResourceLocation ADD_NEW = Main.id("textures/gui/test/add_new_icon.png");

    public NewActionItem(NewAction parent, String name, int x, int y, int width, int height, String typeId, String value, int color) {
        super(x, y, width, height, TextUtils.empty());
        this.parent = parent;
        this.name = name;
        this.x = x;
        this.y = y;
        this.typeId = typeId;
        this.value = value;
        this.color = color;
    }

    public int getXPos() { return this.x; }
    public int getYPos() { return this.y; }

    @Override
    public void onPress() {}

    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        GUIUtils.drawFill(ctx, getXPos() + 3, getYPos() + 5, getXPos() + 5, getYPos() + 12, this.color);
        GUIUtils.drawFill(ctx, getXPos() + 2, getYPos(), getXPos() + getWidth() - 2, getYPos() + 1, 0xFF555555);
        GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.truncateString(this.name, 25)), 0, getXPos() + 7, getYPos() + 8, "left", "center", 0xFFFFFFFF, false);
        GUIUtils.adaptiveDrawTexture(ctx, ADD_NEW, this.getXPos() + this.getWidth() - 10, this.getYPos() + 6, 0, 0, 5, 5, 5, 5);
        if ((this.isMouseOver(mouseX, mouseY) || isHovered) && parent.isOpen())
            GUIUtils.drawFill(ctx, getXPos(), getYPos(), getXPos() + getWidth(), getYPos() + getHeight(), 0x0DFFFFFF);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        parent.openSelector(false);
        parent.addAction(this.typeId, this.value);
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
    protected void updateWidgetNarration(NarrationElementOutput builder) {}
    //?} else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) {}*/
    //?}
}