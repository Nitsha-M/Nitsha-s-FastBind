package com.nitsha.binds.gui.utils;

//? if <1.21.5 {
import com.mojang.blaze3d.platform.GlStateManager;
//?}
import com.mojang.blaze3d.platform.Lighting;import com.mojang.blaze3d.platform.Window;
import com.nitsha.binds.gui.widget.TexturedButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.platform.GlStateManager;
*///?}
//? if >1.20.1 {
import net.minecraft.client.gui.components.WidgetSprites;
//?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.OverlayTexture;import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
*///?}
//? if <1.21.5 {
import net.minecraft.client.resources.model.BakedModel;
//?}
import com.mojang.blaze3d.systems.RenderSystem;
//? if >=1.19.3 {
import org.joml.Matrix4f;
//? } else {
// import com.mojang.math.Matrix4f;
//? }
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class GUIUtils {
    private static final Minecraft MC = Minecraft.getInstance();

    // Cut a string
    public static String truncateString(String str, int maxLength) {
        if (str.length() > maxLength) {
            return str.substring(0, maxLength) + "...";
        }
        return str;
    }

    public static void addText(Object ctx, Component text, int width, int offsetX, int offsetY) {
        addText(ctx, text, width, offsetX, offsetY, "left", "top", 0xFFFFFFFF, true);
    }

    public static void addText(Object ctx, Component text, int width, int offsetX, int offsetY, String hAlign, String vAlign) {
        addText(ctx, text, width, offsetX, offsetY, hAlign, vAlign, 0xFFFFFFFF, true);
    }

    public static void addText(Object ctx, Component text, int width, int offsetX, int offsetY, int color) {
        addText(ctx, text, width, offsetX, offsetY, "left", "top", color, true);
    }

    public static void addText(Object ctx, Component text, int width, int offsetX, int offsetY, String hAlign, String vAlign, int color) {
        addText(ctx, text, width, offsetX, offsetY, hAlign, vAlign, color, true);
    }

    // ---------------- Универсальный метод ----------------

    public static void addText(Object ctx, Component text, int width, int offsetX, int offsetY, String hAlign, String vAlign, int color, boolean shadow) {
        if (text == null || text.getString().isBlank()) return;
        Font font = MC.font;
        int chWidth = font.width(text);
        int chHeight = font.lineHeight;

        int alignCoordX;
        switch (hAlign) {
            case "center":
                alignCoordX = (width / 2) - (chWidth / 2) + offsetX;
                break;
            case "right":
                alignCoordX = offsetX - chWidth;
                break;
            default:
                alignCoordX = offsetX;
                break;
        }

        int alignCoordY;
        switch (vAlign) {
            case "center":
                alignCoordY = offsetY - (chHeight / 2);
                break;
            case "bottom":
                alignCoordY = offsetY - chHeight;
                break;
            default:
                alignCoordY = offsetY;
                break;
        }

        //? if >=1.20 {
        ((GuiGraphics)ctx).drawString(font, text, alignCoordX, alignCoordY, color, shadow);
        //?} else {
        /*((PoseStack)ctx).pushPose();
        if (shadow) {
            font.drawShadow((PoseStack)ctx, text, alignCoordX, alignCoordY, color);
        } else {
            font.draw((PoseStack)ctx, text, alignCoordX, alignCoordY, color);
        }
        ((PoseStack)ctx).popPose();
        *///?}
    }

    public static void adaptiveDrawTexture(Object ctx, ResourceLocation texture, int x, int y, int u, int v,
                                           int width, int height, int textureWidth, int textureHeight, int color) {
        //? if >=1.21.6 {
        /*((GuiGraphics)ctx).blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v,
                width, height, textureWidth, textureHeight, color);
        *///?} else if >1.21.1 {
        ((GuiGraphics)ctx).blit(RenderType::guiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight, color);
        //?} else if >=1.20 {
        /*RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        RenderSystem.setShaderColor(red, green, blue, alpha);
        ((GuiGraphics) ctx).blit(texture, x, y, 0, u, v, width, height, textureWidth, textureHeight);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        *///?} else if >=1.17 {
        /*RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float red   = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue  = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        RenderSystem.setShaderColor(red, green, blue, alpha);

        RenderSystem.setShader(
           //? if >1.19.2 {
          GameRenderer::getPositionTexShader
          //?} else {
          GameRenderer::getPositionTexShader
          //?}
         );
        RenderSystem.setShaderTexture(0, texture);

        GuiComponent.blit(
            (PoseStack) ctx,
            x, y,
            u, v,
            width, height,
            textureWidth,
            textureHeight
        );

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        *///?} else {
        /*RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float red   = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue  = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        RenderSystem.color4f(red, green, blue, alpha);

        Minecraft.getInstance().getTextureManager().bind(texture);
        GuiComponent.blit(
                (PoseStack) ctx,
                x, y,
                u, v,
                width, height,
                textureWidth, textureHeight
        );

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        *///?}
    }

    public static void adaptiveDrawTexture(Object ctx, ResourceLocation texture, int x, int y, int u, int v,
                                           int width, int height, int textureWidth, int textureHeight) {
        adaptiveDrawTexture(ctx, texture, x, y, u, v, width, height, textureWidth, textureHeight, 0xFFFFFFFF);
    }

    public static void adaptiveDrawTexture(Object ctx, ResourceLocation texture, int x, int y, int u, int v,
                                           int width, int height, int textureWidth) {
        adaptiveDrawTexture(ctx, texture, x, y, u, v, width, height, textureWidth, textureWidth, 0xFFFFFFFF);
    }

    public static void adaptiveDrawTexture(Object ctx, ResourceLocation texture, int x, int y, int u, int v,
                                           int width, int height) {
        adaptiveDrawTexture(ctx, texture, x, y, u, v, width, height, 256, 256, 0xFFFFFFFF);
    }

    public static TexturedButton createTexturedBtn(int x, int y, int width, int height,
                                                   ResourceLocation[] textures, Button.OnPress onClick) {
        return new TexturedButton(x, y, width, height, textures[0], textures[1], onClick);
    }

    // ------------------- RESIZABLE BOX -------------------

    public static void drawResizableBox(Object ctx, ResourceLocation texture,
                                        int x, int y, int width, int height, int edge, int tS, int color) {
        int iW = width - edge * 2;
        int iH = height - edge * 2;

        int[][] parts = {
                {0, 0}, {edge * iW, 0}, {tS - edge, 0},
                {0, edge * iH}, {edge * iW, edge * iH}, {tS - edge, edge * iH},
                {0, tS - edge}, {edge * iW, tS - edge}, {tS - edge, tS - edge}
        };

        int[][] positions = {
                {x, y}, {x + edge, y}, {x + width - edge, y},
                {x, y + edge}, {x + edge, y + edge}, {x + width - edge, y + edge},
                {x, y + height - edge}, {x + edge, y + height - edge}, {x + width - edge, y + height - edge}
        };

        int[][] sizes = {
                {edge, edge}, {iW, edge}, {edge, edge},
                {edge, iH}, {iW, iH}, {edge, iH},
                {edge, edge}, {iW, edge}, {edge, edge}
        };

        int[][] tSizes = {
                {tS, tS}, {tS * iW, tS}, {tS, tS},
                {tS, tS * iH}, {tS * iW, tS * iH}, {tS, tS * iH},
                {tS, tS}, {tS * iW, tS}, {tS, tS}
        };

        for (int i = 0; i < 9; i++) {
            adaptiveDrawTexture(ctx, texture,
                    positions[i][0], positions[i][1], parts[i][0], parts[i][1],
                    sizes[i][0], sizes[i][1], tSizes[i][0], tSizes[i][1], color);
        }
    }

    public static void drawResizableBox(Object ctx, ResourceLocation texture, int x, int y, int width, int height, int edge, int tS) {
        drawResizableBox(ctx, texture, x, y, width, height, edge, tS, 0xFFFFFFFF);
    }

    public static float clampSpeed(float value) {
        return Mth.clamp(value, 0.001f, 1.0f);
    }

    public static void matricesUtil(Object ctx, float x, float y, int zIndex, Runnable action) {
        //? if >=1.21.6 {
        /* GuiGraphics graphics = (GuiGraphics)ctx;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        action.run();
        graphics.pose().popMatrix();
        *///?} else if >=1.20 {
        ((GuiGraphics)ctx).pose().pushPose();
        ((GuiGraphics)ctx).pose().translate(x, y, zIndex);
        action.run();
        ((GuiGraphics)ctx).pose().translate(-x, -y, 0);
        ((GuiGraphics)ctx).pose().popPose();
        //?} else {
        /*PoseStack matrices = (PoseStack) ctx;
        matrices.pushPose();
        matrices.translate(x, y, zIndex);
        action.run();
        matrices.translate(-x, -y, 0);
        matrices.popPose();
        *///?}
    }

    public static void customScissor(Object ctx, int x, int y, int width, int height, Runnable action) {
        //? if >=1.21.6 {
        /*GuiGraphics graphics = (GuiGraphics)ctx;
        graphics.enableScissor(0, 0, 10000, 10000);
        graphics.pose().pushMatrix();
        graphics.enableScissor(x, y, x + width, y + height);
        action.run();
        graphics.disableScissor();
        graphics.pose().popMatrix();
        graphics.disableScissor();*/
        //? } else if >=1.21.4 {
        /*GuiGraphics graphics = (GuiGraphics)ctx;
        graphics.enableScissor(0, 0, 10000, 10000);
        graphics.pose().pushPose();
        graphics.enableScissor(x, y, x + width, y + height);
        action.run();
        graphics.disableScissor();
        graphics.pose().popPose();
        graphics.disableScissor();
        *///?} else if >=1.20 {
        ((GuiGraphics)ctx).enableScissor(0, 0, 10000, 10000);
        ((GuiGraphics)ctx).pose().pushPose();
        ((GuiGraphics)ctx).enableScissor(x, y, x + width, y + height);
        action.run();
        ((GuiGraphics)ctx).disableScissor();
        ((GuiGraphics)ctx).pose().popPose();
        ((GuiGraphics)ctx).disableScissor();
        //?} else {
        /*
        Window window = Minecraft.getInstance().getWindow();
        int i = window.getHeight();
        double d = window.getGuiScale();
        double e = (double)x * d;
        double f = (double)i - ((double)y + (double)height) * d;  // ← ИСПРАВЛЕНО ЗДЕСЬ
        double g = (double)width * d;
        double h = (double)height * d;
        RenderSystem.enableScissor(0, 0, 10000, 10000);
        ((PoseStack)ctx).pushPose();
        RenderSystem.enableScissor((int)e, (int)f, Math.max(0, (int)g), Math.max(0, (int)h));
        action.run();
        RenderSystem.disableScissor();
        ((PoseStack)ctx).popPose();
        RenderSystem.disableScissor();
        */
        //?}
    }

    public static void matricesScale(Object ctx, float scale, Runnable action) {
        //? if >=1.21.6 {
        /*GuiGraphics graphics = (GuiGraphics)ctx;
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        action.run();
        graphics.pose().popMatrix();
        *///?} else if >=1.20 {
        GuiGraphics gctx = (GuiGraphics) ctx;
        gctx.pose().pushPose();
        gctx.pose().scale(scale, scale, 1.0f);
        action.run();
        gctx.pose().popPose();
        //?} else {
        /*PoseStack matrices = (PoseStack) ctx;
        matrices.pushPose();
        matrices.scale(scale, scale, 1.0f);
        action.run();
        matrices.popPose();
        *///?}
    }

    // Draw

    public static void drawFill(Object ctx, int x1, int y1, int x2, int y2, int color) {
        //? if >=1.20 {
        GuiGraphics context = (GuiGraphics) ctx;
        context.fill(x1, y1, x2, y2, color);
        //?} else {
        /*PoseStack matrices = (PoseStack) ctx;
        GuiComponent.fill(matrices, x1, y1, x2, y2, color);
        *///?}
    }

    public static void drawItem(Object ctx, ItemStack stack, int x, int y, float scale) {
        //? if >=1.20 {
        ((GuiGraphics)ctx).renderItem(stack, x, y);
        //?} else if >=1.19.4 {
        /*ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderGuiItem((PoseStack) ctx, stack, x, y);
        *///?} else if >=1.17 {
        /*Minecraft client = Minecraft.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();
        PoseStack matrices = (PoseStack) ctx;
        matrices.pushPose();
        matrices.translate((float)x, (float)y, 150.0F);
        matrices.translate(8.0F, 8.0F, 0.0F);
        //? if >=1.19.3 {
        matrices.last().pose().mul((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
        //? } else if >=1.18 {
        matrices.last().pose().multiply(Matrix4f.createScaleMatrix(1.0F, -1.0F, 1.0F));
        //? } else {
        matrices.last().pose().multiply(Matrix4f.createScaleMatrix(1.0F, -1.0F, 1.0F));
        //? }
        matrices.scale(16.0F, 16.0F, 16.0F);
        MultiBufferSource.BufferSource immediate = client.renderBuffers().bufferSource();
        //? if >=1.18 {
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);
        //? } else {
        BakedModel model = itemRenderer.getItemModelShaper().getItemModel(stack);
        //? }
        boolean bl = !model.usesBlockLight();
        if (bl) Lighting.setupForFlatItems();

        PoseStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushPose();
        //? if >=1.18 {
        matrixStack.mulPoseMatrix(matrices.last().pose());
        RenderSystem.applyModelViewMatrix();
        //? } else {
        matrixStack.mulPoseMatrix(matrices.last().pose());
        RenderSystem.applyModelViewMatrix();
        //? }

        itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, new PoseStack(), immediate, 15728880, OverlayTexture.NO_OVERLAY, model);
        immediate.endBatch();
        RenderSystem.enableDepthTest();
        if (bl) Lighting.setupFor3DItems();

        matrices.popPose();
        matrixStack.popPose();
        RenderSystem.applyModelViewMatrix();
        *///?} else {
        /*Minecraft client = Minecraft.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();
        PoseStack matrices = (PoseStack) ctx;
        RenderSystem.pushMatrix();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.multMatrix(matrices.last().pose());
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, 150.0F);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);
        BakedModel model = itemRenderer.getItemModelShaper().getItemModel(stack);
        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean bl = !model.usesBlockLight();
        if (bl) Lighting.setupForFlatItems();

        PoseStack matrixStack = new PoseStack();

        itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, matrixStack, immediate, 15728880, OverlayTexture.NO_OVERLAY, model);
        immediate.endBatch();
        RenderSystem.enableDepthTest();
        if (bl) Lighting.setupFor3DItems();

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
        *///?}
    }

    public static void drawItem(Object ctx, ItemStack stack, int x, int y) {
        drawItem(ctx, stack, x, y, 1);
    }
}