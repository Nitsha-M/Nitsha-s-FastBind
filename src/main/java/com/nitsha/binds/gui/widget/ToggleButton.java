package com.nitsha.binds.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.utils.GUIUtils;
import net.minecraft.client.MinecraftClient;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.util.math.MatrixStack;
//? if >=1.17 {
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
//? }
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ToggleButton extends ClickableWidget {
    private static Identifier TEXTURE;

    private boolean firstRender = true;
    protected boolean toggled;
    private final Runnable onClick;
    private float pointX = 0.0f;
    private float speed = MainClass.GLOBAL_ANIMATION_SPEED + 0.2f;

    private final Text name;
    private int x, y;

    public ToggleButton(Text name, int x, int y, int width, int height, boolean border, boolean toggled, Runnable onClick) {
        super(x, y, width, height, Text.of(""));
        this.name = name;
        this.onClick = onClick;
        this.toggled = toggled;
        this.x = x;
        this.y = y;

        if (border) {
            TEXTURE = MainClass.id("textures/gui/gui_elements_border.png");
        } else {
            TEXTURE = MainClass.id("textures/gui/gui_elements.png");
        }

        int tX = this.getX() + width - 23 - 4;
        this.pointX = this.toggled ? tX + 11 : tX;
    }

    public void onClick(double mouseX, double mouseY) {
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
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    //? } else >=1.20 {
    /*@Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///? } else {
    /*@Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        rndr(matrices, mouseX, mouseY, delta);
    }
    *///? }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        int tX = this.getX() + this.width - 23 - 4;
        int tY = this.getY() + height / 2 - 6;
        if (firstRender) {
            pointX = toggled ? tX + 10 : tX;
            firstRender = false;
        } else {
            float targetPointX = toggled ? tX + 10 : tX;
            pointX = MathHelper.lerp(GUIUtils.clampSpeed(speed * delta), pointX, targetPointX);
        }

        //? <1.17 {
        // RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        //? }

        if (isHovered()) GUIUtils.drawFill(ctx, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x0D000000);

        GUIUtils.addText(ctx, name, 0, this.getX() + 4, this.getY() + (this.height / 2), "left", "center", 0xFF212121, false);

        // Button
        GUIUtils.adaptiveDrawTexture(ctx, TEXTURE, tX, tY, 0, 0, 23, 12, 256, 256);

        // Point
        GUIUtils.adaptiveDrawTexture(ctx, TEXTURE, Math.round(pointX), tY - 1, 0, 12, 13, 12, 256, 256);
    }

    //? if >=1.19.3 {
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
    //? } else if >=1.17 {
    /*@Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }*/
    //? }
}
