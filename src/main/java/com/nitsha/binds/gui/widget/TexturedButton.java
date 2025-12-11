package com.nitsha.binds.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
*///?}
import net.minecraft.client.gui.components.ImageButton;
//? if >=1.20.2 {
import net.minecraft.client.gui.components.WidgetSprites;
//?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
//? if <1.17 {
/*import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
*///?}
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class TexturedButton extends ImageButton {
    private final ResourceLocation defaultTexture;
    private final ResourceLocation hoverTexture;

    private int x, y, width, height;

    //? if >=1.20.2 {
    public TexturedButton(int x, int y, int width, int height, ResourceLocation defaultTexture,
                          ResourceLocation hoverTexture, Button.OnPress onClick) {
        super(x, y, width, height, new WidgetSprites(defaultTexture, hoverTexture), onClick);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.defaultTexture = defaultTexture;
        this.hoverTexture = hoverTexture;
    }
    //?} else {
    /*public TexturedButton(int x, int y, int width, int height, ResourceLocation defaultTexture,
                         ResourceLocation hoverTexture, Button.OnPress onClick) {
        //? if >=1.17 {
        super(x, y, width, height, 0, 0, defaultTexture, onClick);
        //? } else {
        super(x, y, width, height, 0, 0, 0, defaultTexture, onClick);
        //? }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.defaultTexture = defaultTexture;
        this.hoverTexture = hoverTexture;
    }*/
    //?}

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean keyPressed(KeyEvent event) {
        return false;
    }*/
    //? } else {
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return false;
        }
    //? }

    //? if >=1.21.5 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
    }*/
    //? } else if >=1.20.2 {
    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        super.renderWidget(context, mouseX, mouseY, delta);
        RenderSystem.disableBlend();
    }
    //? } else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ResourceLocation current = this.isHovered ? hoverTexture : defaultTexture;
        ctx.blit(current, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();
    }
    *///? } else if >=1.19.4 {
    /*@Override
    public void renderWidget(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ResourceLocation current = this.isHovered ? hoverTexture : defaultTexture;
        RenderSystem.setShaderTexture(0, current);
        GuiComponent.blit(matrices, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();
    }
    *///? } else if >=1.17 {
    /*@Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ResourceLocation current = this.isHovered ? hoverTexture : defaultTexture;
        RenderSystem.setShaderTexture(0, current);
        GuiComponent.blit(matrices, this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();
    }
    *///?} else {
    /*@Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation current = this.isHovered ? hoverTexture : defaultTexture;
        mc.getTextureManager().bind(current);
        blit(matrices, this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }*/
    //?}
}