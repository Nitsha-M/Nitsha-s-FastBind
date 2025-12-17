package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.vertex.PoseStack;
 *///?}
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class ToggleButton extends AbstractWidget {
    private static ResourceLocation TEXTURE;

    private boolean firstRender = true;
    protected boolean toggled;
    private final Runnable onClick;
    private float pointX = 0.0f;
    private float speed = Main.GLOBAL_ANIMATION_SPEED + 0.2f;

    private final Component name;
    private int x, y;

    public ToggleButton(Component name, int x, int y, int width, int height, boolean border, boolean toggled, Runnable onClick) {
        super(x, y, width, height, TextUtils.empty());
        this.name = name;
        this.onClick = onClick;
        this.toggled = toggled;
        this.x = x;
        this.y = y;

        if (border) {
            TEXTURE = Main.id("textures/gui/gui_elements_border.png");
        } else {
            TEXTURE = Main.id("textures/gui/gui_elements.png");
        }

        int tX = this.getX() + width - 23 - 4;
        this.pointX = this.toggled ? tX + 11 : tX;
    }

    @Override
    //? <1.21.9 {
    public void onClick(double mouseX, double mouseY) {
    //? } else {
    // public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
    //? }
        this.onClick.run();
        this.toggled = !this.toggled;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getHeight() {
        return this.height;
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

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        GuiGraphics c = (GuiGraphics) ctx;
        //?} else {
        /*PoseStack c = (PoseStack) ctx;
         *///?}
        int tX = this.getX() + this.width - 23 - 4;
        int tY = this.getY() + height / 2 - 6;
        if (firstRender) {
            pointX = toggled ? tX + 10 : tX;
            firstRender = false;
        } else {
            float targetPointX = toggled ? tX + 10 : tX;
            pointX = Mth.lerp(GUIUtils.clampSpeed(speed * delta), pointX, targetPointX);
        }

        //? <1.17 {
        // RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        //?}

        if (this.isHovered) GUIUtils.drawFill(c, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x0D000000);

        GUIUtils.addText(c, name, 0, this.getX() + 4, this.getY() + (this.height / 2), "left", "center", 0xFF212121, false);

        // Button
        GUIUtils.adaptiveDrawTexture(c, TEXTURE, tX, tY, 0, 0, 23, 12, 256, 256);

        // Point
        GUIUtils.adaptiveDrawTexture(c, TEXTURE, Math.round(pointX), tY - 1, 0, 12, 13, 12, 256, 256);
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
