package com.nitsha.binds.multiversions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

//? if >=1.19.3 {
import net.minecraft.client.gui.components.Renderable;
//? } else {
/*import net.minecraft.client.gui.components.Widget;*/
//? }

public class RenderUtils {

    private static final Minecraft MC = Minecraft.getInstance();

    public static void renderElement(GuiGraphicsExtractor ctx, GuiEventListener element, int mouseX, int mouseY, float delta) {
        //? if >=1.19.3 {
        if (element instanceof Renderable renderable) {
            //? if >=26.1 {
            renderable.extractRenderState(ctx, mouseX, mouseY, delta);
            //? } else {
            /*renderable.render(ctx, mouseX, mouseY, delta);*/
            //? }
        }
        //? } else {
    /*if (element instanceof Widget widget) {
        widget.render(ctx, mouseX, mouseY, delta);
    }*/
        //? }
    }

    public static float clampSpeed(float value) {
        return Mth.clamp(value, 0.001f, 1.0f);
    }

    public static String truncateString(String str, int maxLength) {
        if (str.length() > maxLength) {
            return str.substring(0, maxLength) + "...";
        }
        return str;
    }

    public static int darkenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return (a << 24) | ((int)(r * factor) << 16) | ((int)(g * factor) << 8) | (int)(b * factor);
    }

    public static String getUL() {
        //? if >=1.19.4 {
        return Minecraft.getInstance().getLanguageManager().getSelected().toString();
        //? } else {
        // return Minecraft.getInstance().getLanguageManager().getSelected().getCode().toString();
        //? }
    }

    public static void customScissor(GuiGraphicsExtractor ctx, int x, int y, int width, int height, Runnable action) {
        //? if >=1.21.6 {
        ctx.pose().pushMatrix();
        ctx.enableScissor(x, y, x + width, y + height);
        action.run();
        ctx.disableScissor();
        ctx.pose().popMatrix();
        //? } else if >=1.21.4 {
        /*ctx.pose().pushPose();
        ctx.enableScissor(x, y, x + width, y + height);
        action.run();
        ctx.disableScissor();
        ctx.pose().popPose();
        *///?} else if >=1.20 {
        /*ctx.pose().pushPose();
        ctx.enableScissor(x, y, x + width, y + height);
        action.run();
        ctx.disableScissor();
        ctx.pose().popPose();
        *///?} else {
        /*
        Window window = Minecraft.getInstance().getWindow();
        int i = window.getHeight();
        double d = window.getGuiScale();
        double e = (double)x * d;
        double f = (double)i - ((double)y + (double)height) * d;  // ← ИСПРАВЛЕНО ЗДЕСЬ
        double g = (double)width * d;
        double h = (double)height * d;
        RenderSystem.enableScissor(0, 0, 10000, 10000);
        ctx.pushPose();
        RenderSystem.enableScissor((int)e, (int)f, Math.max(0, (int)g), Math.max(0, (int)h));
        action.run();
        RenderSystem.disableScissor();
        ctx.popPose();
        RenderSystem.disableScissor();
        */
        //?}
    }

    // ========================================================
    // Multiversion DrawText
    // ========================================================

    public static void drawText(GuiGraphicsExtractor ctx, Component text, int width, int offsetX, int offsetY, String hAlign, String vAlign, int color, boolean shadow) {
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

        //? if >=26.1 {
        ctx.text(font, text, alignCoordX, alignCoordY, color, shadow);
        //? } else if >=1.20 {
        /*ctx.drawString(font, text, alignCoordX, alignCoordY, color, shadow);
         *///?} else {
        /*ctx.pushPose();
        if (shadow) {
            font.drawShadow(ctx, text, alignCoordX, alignCoordY, color);
        } else {
            font.draw(ctx, text, alignCoordX, alignCoordY, color);
        }
        ctx.popPose();
        *///?}
    }

    public static void drawText(GuiGraphicsExtractor ctx, Component text, int width, int offsetX, int offsetY) {
        drawText(ctx, text, width, offsetX, offsetY, "left", "top", 0xFFFFFFFF, true);
    }

    public static void drawText(GuiGraphicsExtractor ctx, Component text, int width, int offsetX, int offsetY, String hAlign, String vAlign) {
        drawText(ctx, text, width, offsetX, offsetY, hAlign, vAlign, 0xFFFFFFFF, true);
    }

    public static void drawText(GuiGraphicsExtractor ctx, Component text, int width, int offsetX, int offsetY, int color) {
        drawText(ctx, text, width, offsetX, offsetY, "left", "top", color, true);
    }

    public static void drawText(GuiGraphicsExtractor ctx, Component text, int width, int offsetX, int offsetY, int color, String hAlign, String vAlign) {
        drawText(ctx, text, width, offsetX, offsetY, hAlign, vAlign, color, true);
    }

    public static void drawText(GuiGraphicsExtractor ctx, Component text, int width, int offsetX, int offsetY, String hAlign, String vAlign, int color) {
        drawText(ctx, text, width, offsetX, offsetY, hAlign, vAlign, color, true);
    }

    // ========================================================
    // Multiversion DrawTexture

    public static void drawTexture(GuiGraphicsExtractor ctx, Identifier texture, int x, int y, int u, int v,
                                   int width, int height, int uWidth, int uHeight, int textureWidth, int textureHeight, int color) {
        //? if >=1.21.6 {
        ctx.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, uWidth, uHeight, textureWidth, textureHeight, color);
        //?} else if >1.21.1 {
        /*ctx.blit(RenderType::guiTextured, texture, x, y, width, height, u, v, uWidth, uHeight, textureWidth, textureHeight, color);
         *///?} else if >=1.20 {
        /*RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        RenderSystem.setShaderColor(red, green, blue, alpha);
        ctx.blit(texture, x, y, width, height, u, v, uWidth, uHeight, textureWidth, textureHeight);
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
            ctx,
            x, y,
            width, height,
            u, v,
            uWidth, uHeight,
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
                ctx,
                x, y,
                width, height,
                u, v,
                uWidth, uHeight,
                textureWidth, textureHeight
        );

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        *///?}
    }

    public static void drawTexture(GuiGraphicsExtractor ctx, Identifier texture, int x, int y, int u, int v,
                                   int width, int height, int textureWidth, int textureHeight, int color) {
        drawTexture(ctx, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight, color);
    }

    public static void drawTexture(GuiGraphicsExtractor ctx, Identifier texture, int x, int y, int u, int v,
                                   int width, int height, int textureWidth, int textureHeight) {
        drawTexture(ctx, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight, 0xFFFFFFFF);
    }

    public static void drawTexture(GuiGraphicsExtractor ctx, Identifier texture, int x, int y, int u, int v,
                                   int width, int height, int textureWidth) {
        drawTexture(ctx, texture, x, y, u, v, width, height, width, height, textureWidth, textureWidth, 0xFFFFFFFF);
    }

    public static void drawTexture(GuiGraphicsExtractor ctx, Identifier texture, int x, int y, int u, int v,
                                   int width, int height) {
        drawTexture(ctx, texture, x, y, u, v, width, height, width, height, 256, 256, 0xFFFFFFFF);
    }


    // ========================================================
    // Multiversion DrawItem
    // ========================================================

    //? if >=26.1 {
    public static void drawItem(GuiGraphicsExtractor ctx, ItemStack stack, int x, int y, float scale) {
        ctx.item(stack, x, y);
    }

    public static void drawItem(GuiGraphicsExtractor ctx, ItemStack stack, int x, int y) {
        drawItem(ctx, stack, x, y, 1);
    }
    //? } else {
    /*public static void drawItem(GuiGraphicsExtractor ctx, ItemStack stack, int x, int y, float scale) {
        //? if >=1.20 {
        ctx.renderItem(stack, x, y);
        //? } else if >=1.19.4 {
        /^ItemEntityRenderer itemRenderer = Minecraft.getInstance().getItemEntityRenderer();
        itemRenderer.renderGuiItem(ctx, stack, x, y);
        ^///? } else if >=1.17 {
        /^Minecraft client = Minecraft.getInstance();
        ItemEntityRenderer itemRenderer = client.getItemEntityRenderer();
        ctx.pushPose();
        ctx.translate((float)x, (float)y, 150.0F);
        ctx.translate(8.0F, 8.0F, 0.0F);
        //? if >=1.19.3 {
        ctx.last().pose().mul((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
        //? } else if >=1.18 {
        ctx.last().pose().multiply(Matrix4f.createScaleMatrix(1.0F, -1.0F, 1.0F));
        //? } else {
        ctx.last().pose().multiply(Matrix4f.createScaleMatrix(1.0F, -1.0F, 1.0F));
        //? }
        ctx.scale(16.0F, 16.0F, 16.0F);
        MultiBufferSource.BufferSource immediate = client.renderBuffers().bufferSource();
        //? if >=1.18 {
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);
        //? } else {
        BakedModel model = itemRenderer.getItemModelShaper().getItemModel(stack);
        //? }
        boolean bl = !model.usesBlockLight();
        if (bl) Lighting.setupForFlatItems();

        GuiGraphicsExtractor matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushPose();
        //? if >=1.18 {
        matrixStack.mulPoseMatrix(((GuiGraphicsExtractor) ctx).last().pose());
        RenderSystem.applyModelViewMatrix();
        //? } else {
        matrixStack.mulPoseMatrix(((GuiGraphicsExtractor) ctx).last().pose());
        RenderSystem.applyModelViewMatrix();
        //? }

        itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, new GuiGraphicsExtractor(), immediate, 15728880, OverlayTexture.NO_OVERLAY, model);
        immediate.endBatch();
        RenderSystem.enableDepthTest();
        if (bl) Lighting.setupFor3DItems();

        ctx.popPose();
        matrixStack.popPose();
        RenderSystem.applyModelViewMatrix();
        ^///? } else {
        /^Minecraft client = Minecraft.getInstance();
        ItemEntityRenderer itemRenderer = client.getItemEntityRenderer();
        RenderSystem.pushMatrix();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.multMatrix(((GuiGraphicsExtractor) ctx).last().pose());
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, 150.0F);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);
        BakedModel model = itemRenderer.getItemModelShaper().getItemModel(stack);
        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean bl = !model.usesBlockLight();
        if (bl) Lighting.setupForFlatItems();

        GuiGraphicsExtractor matrixStack = new GuiGraphicsExtractor();

        itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, matrixStack, immediate, 15728880, OverlayTexture.NO_OVERLAY, model);
        immediate.endBatch();
        RenderSystem.enableDepthTest();
        if (bl) Lighting.setupFor3DItems();

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
        ^///? }
    }

    public static void drawItem(GuiGraphicsExtractor ctx, ItemStack stack, int x, int y) {
        drawItem(ctx, stack, x, y, 1);
    }
    *///? }

    // ========================================================
    // Resizable box
    // ========================================================


    public static void drawResizableBox(GuiGraphicsExtractor ctx, Identifier texture, int x, int y, int width, int height, int uOffset, int vOffset, int edge, int textureWidth, int textureHeight, int color) {
        if (width <= 0 || height <= 0 || edge < 0) return;

        int partW = width - edge * 2;
        int partH = height - edge * 2;

        if (partW < 0 || partH < 0) return;

        int middleX = x + edge;
        int middleY = y + edge;
        int rightX = x + width - edge;
        int bottomY = y + height - edge;

        int middleU = uOffset + edge;
        int middleV = vOffset + edge;
        int rightU = middleU + 1;
        int bottomV = middleV + 1;

        if (edge > 0) {
            drawTexture(ctx, texture, x, y, uOffset, vOffset, edge, edge, edge, edge, textureWidth, textureHeight, color);

            if (partW > 0) {
                drawTexture(ctx, texture, middleX, y, middleU, vOffset, partW, edge, 1, edge, textureWidth, textureHeight, color);
            }

            drawTexture(ctx, texture, rightX, y, rightU, vOffset, edge, edge, edge, edge, textureWidth, textureHeight, color);
        }

        if (partH > 0) {
            if (edge > 0) {
                drawTexture(ctx, texture, x, middleY, uOffset, middleV, edge, partH, edge, 1, textureWidth, textureHeight, color);
            }

            if (partW > 0) {
                drawTexture(ctx, texture, middleX, middleY, middleU, middleV, partW, partH, 1, 1, textureWidth, textureHeight, color);
            }

            if (edge > 0) {
                drawTexture(ctx, texture, rightX, middleY, rightU, middleV, edge, partH, edge, 1, textureWidth, textureHeight, color);
            }
        }

        if (edge > 0) {
            drawTexture(ctx, texture, x, bottomY, uOffset, bottomV, edge, edge, edge, edge, textureWidth, textureHeight, color);

            if (partW > 0) {
                drawTexture(ctx, texture, middleX, bottomY, middleU, bottomV, partW, edge, 1, edge, textureWidth, textureHeight, color);
            }

            drawTexture(ctx, texture, rightX, bottomY, rightU, bottomV, edge, edge, edge, edge, textureWidth, textureHeight, color);
        }
    }

    public static void drawResizableBox(GuiGraphicsExtractor ctx, Identifier texture,
                                        int x, int y, int width, int height, int edge, int tS, int color) {
        drawResizableBox(ctx, texture, x, y, width, height, 0, 0, edge, tS, tS, color);
    }

    public static void drawResizableBox(GuiGraphicsExtractor ctx, Identifier texture, int x, int y, int width, int height, int edge, int tS) {
        drawResizableBox(ctx, texture, x, y, width, height, 0,  0, edge, tS, tS, 0xFFFFFFFF);
    }

    // ========================================================
    // Others useful functions
    // ========================================================

    public static void matricesUtil(GuiGraphicsExtractor ctx, float x, float y, int zIndex, Runnable action) {
        //? if >=1.21.6 {
        ctx.pose().pushMatrix();
        ctx.pose().translate(x, y);
        action.run();
        ctx.pose().popMatrix();
        //?} else if >=1.20 {
        /*ctx.pose().pushPose();
        ctx.pose().translate(x, y, zIndex);
        action.run();
        ctx.pose().translate(-x, -y, 0);
        ctx.pose().popPose();
        *///?} else {
        /*ctx.pushPose();
        ctx.translate(x, y, zIndex);
        action.run();
        ctx.translate(-x, -y, 0);
        ctx.popPose();
        *///?}
    }

    public static void matricesScale(GuiGraphicsExtractor ctx, float scale, Runnable action) {
        //? if >=1.21.6 {
        ctx.pose().pushMatrix();
        ctx.pose().scale(scale, scale);
        action.run();
        ctx.pose().popMatrix();
        //?} else if >=1.20 {
        /*ctx.pose().pushPose();
        ctx.pose().scale(scale, scale, 1.0f);
        action.run();
        ctx.pose().popPose();
        *///?} else {
        /*ctx.pushPose();
        ctx.scale(scale, scale, 1.0f);
        action.run();
        ctx.popPose();
        *///?}
    }

    // Draw

    public static void drawFill(GuiGraphicsExtractor ctx, int x1, int y1, int x2, int y2, int color) {
        //? if >=1.20 {
        ctx.fill(x1, y1, x2, y2, color);
        //?} else {
        /*GuiComponent.fill(ctx, x1, y1, x2, y2, color);
         *///?}
    }
}
