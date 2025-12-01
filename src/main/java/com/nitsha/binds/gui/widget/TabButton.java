package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import net.minecraft.client.Minecraft;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class TabButton extends AbstractWidget {
    private static final ResourceLocation BACKGROUND = Main.id("textures/gui/test/editor_bg_dark_flat.png");
    private final Consumer<TabButton> onClick;
    private final Component name;
    private boolean selected;
    private float speed = Main.GLOBAL_ANIMATION_SPEED;
    private float pointY = 0.0f;

    private int x, y;

    public TabButton(int x, int y, boolean selected, Component name, Consumer<TabButton> onClick) {
        super(x, y, 0, 16, Component.literal(""));
        this.onClick = onClick;
        this.selected = selected;
        this.name = name;
        this.x = x;
        this.y = y;

        int width = Minecraft.getInstance().font.width(name) + 10;
        this.setWidth(width);
    }

    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (onClick != null) {
            onClick.accept(this);
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    //? if >1.20.2 {
    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    //?} else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?} else {
    /*@Override
    public void renderWidget(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?}

    private void rndr(Object context, int mouseX, int mouseY, float delta) {
        float targetPointX = (this.selected) ? this.getY() : this.getY() + 2;
        pointY = Mth.lerp(GUIUtils.clampSpeed(speed * delta), pointY, targetPointX);
        GUIUtils.drawResizableBox(context, BACKGROUND, this.getX(), Math.round(pointY), this.getWidth(), this.getHeight() + 10, 7, 15);
        GUIUtils.addText(context, name, 0, this.getX() + 5, Math.round(pointY) + 5, (this.selected) ? 0xFFFFFFFF : 0xFFA8A8A8);
    }

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