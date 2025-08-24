package com.nitsha.binds.gui.utils;

//? if <1.21.5 {
import com.mojang.blaze3d.platform.GlStateManager;
//? }
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
*///? }
//? if >1.20.1 {
import net.minecraft.client.gui.screen.ButtonTextures;
//? }
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Identifier;
//? if >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
*///? }
//? if <1.21.5 {
import net.minecraft.client.render.model.BakedModel;
//?}
import com.mojang.blaze3d.systems.RenderSystem;
//? if >1.19.2 {
import net.minecraft.world.World;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Vector4f;
//? } else {
/*import net.minecraft.util.math.Matrix4f;*/
//? }
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class GUIUtils {
    private static final MinecraftClient MC = MinecraftClient.getInstance();
    // Cut a string
    public static String truncateString(String str, int maxLength) {
        if (str.length() > maxLength) {
            return str.substring(0, maxLength) + "...";
        }
        return str;
    }

    public static void addText(Object ctx, Text text, int width, int offsetX, int offsetY) {
        addText(ctx, text, width, offsetX, offsetY, "left", "top", 0xFFFFFFFF, true);
    }

    public static void addText(Object ctx, Text text, int width, int offsetX, int offsetY, String hAlign, String vAlign) {
        addText(ctx, text, width, offsetX, offsetY, hAlign, vAlign, 0xFFFFFFFF, true);
    }

    public static void addText(Object ctx, Text text, int width, int offsetX, int offsetY, int color) {
        addText(ctx, text, width, offsetX, offsetY, "left", "top", color, true);
    }

    public static void addText(Object ctx, Text text, int width, int offsetX, int offsetY, String hAlign, String vAlign, int color) {
        addText(ctx, text, width, offsetX, offsetY, hAlign, vAlign, color, true);
    }

    // ---------------- Универсальный метод ----------------

    public static void addText(Object ctx, Text text, int width, int offsetX, int offsetY, String hAlign, String vAlign, int color, boolean shadow) {
        if (text == null || text.getString().isBlank()) return;
        TextRenderer tr = MC.textRenderer;
        int chWidth = tr.getWidth(text);
        int chHeight = tr.fontHeight;

        int alignCoordX = switch (hAlign) {
            case "center" -> (width / 2) - (chWidth / 2) + offsetX;
            case "right"  -> offsetX - chWidth;
            default       -> offsetX;
        };

        int alignCoordY = switch (vAlign) {
            case "center" -> offsetY - (chHeight / 2);
            case "bottom" -> offsetY - chHeight;
            default       -> offsetY;
        };

        //? if >=1.20 {
        ((DrawContext)ctx).drawText(tr, text, alignCoordX, alignCoordY, color, shadow);
        //? } else {
        /*((MatrixStack)ctx).push();
        if (shadow) {
            tr.drawWithShadow((MatrixStack)ctx, text, alignCoordX, alignCoordY, color);
        } else {
            tr.draw((MatrixStack)ctx, text, alignCoordX, alignCoordY, color);
        }
        ((MatrixStack)ctx).pop();
        *///? }
    }

    public static void adaptiveDrawTexture(Object ctx, Identifier texture, int x, int y, int u, int v,
                                           int width, int height, int textureWidth, int textureHeight, int color) {
        //? if >=1.21.6 {
        /*((DrawContext)ctx).drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v,
                width, height, textureWidth, textureHeight, color);
        *///?} else if >1.21.1 {
        ((DrawContext)ctx).drawTexture(RenderLayer::getGuiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight, color);
        //? } else if >=1.20 {
        /*RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        RenderSystem.setShaderColor(red, green, blue, alpha);
        ((DrawContext) ctx).drawTexture(texture, x, y, 0, u, v,width, height, textureWidth, textureHeight);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        *///? } else if >=1.17 {
        /*
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float red   = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue  = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        RenderSystem.setShaderColor(red, green, blue, alpha);

        RenderSystem.setShader(
           //? if >1.19.2 {
          GameRenderer::getPositionTexProgram
          //?} else {
          GameRenderer::getPositionTexShader
          //?}
         );
        RenderSystem.setShaderTexture(0, texture);

        // здесь именно перегрузка с размером атласа
        DrawableHelper.drawTexture(
            (MatrixStack) ctx,
            x, y,
            u, v,
            width, height,
            textureWidth,
            textureHeight
        );

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        *///? } else {
        /*
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

        float red   = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue  = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        RenderSystem.color4f(red, green, blue, alpha);

        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        DrawableHelper.drawTexture(
                (MatrixStack) ctx,
                x, y,
                u, v,
                width, height,
                textureWidth, textureHeight
        );

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();*/
        //? }
    }

    public static void adaptiveDrawTexture(Object ctx, Identifier texture, int x, int y, int u, int v,
                                           int width, int height, int textureWidth, int textureHeight) {
        adaptiveDrawTexture(ctx, texture, x, y, u, v, width, height, textureWidth, textureHeight, 0xFFFFFFFF);
    }

    public static void adaptiveDrawTexture(Object ctx, Identifier texture, int x, int y, int u, int v,
                                           int width, int height, int textureWidth) {
        adaptiveDrawTexture(ctx, texture, x, y, u, v, width, height, textureWidth, textureWidth, 0xFFFFFFFF);
    }

    public static void adaptiveDrawTexture(Object ctx, Identifier texture, int x, int y, int u, int v,
                                           int width, int height) {
        adaptiveDrawTexture(ctx, texture, x, y, u, v, width, height, 256, 256, 0xFFFFFFFF);
    }

    public static PressableWidget createTexturedBtn(int x, int y, int width, int height,
                                                    Identifier[] textures, ButtonWidget.PressAction onClick) {
        Identifier defaultTexture = textures[0];
        Identifier hoverTexture   = textures[1];

        //? if >1.21.1 {
        return new TexturedButtonWidget(x, y, width, height, new ButtonTextures(defaultTexture, hoverTexture), onClick) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                return false;
            }
        };
        //? } else if >=1.20.3 {
        /*return new TexturedButtonWidget(x, y, width, height, new ButtonTextures(defaultTexture, hoverTexture), onClick) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                super.renderWidget(context, mouseX, mouseY, delta);
                RenderSystem.disableBlend();
            }
        };
        *///? } else if >=1.20.2 {
        /*return new TexturedButtonWidget(x, y, width, height, new ButtonTextures(defaultTexture, hoverTexture), onClick) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
            @Override
            public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                super.renderButton(context, mouseX, mouseY, delta);
                RenderSystem.disableBlend();
            }
        };
        *///? } else >=1.20 {
        /*return new TexturedButtonWidget(x, y, width, height, 0, 0, defaultTexture, onClick) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
            @Override
            public void renderButton(DrawContext ctx, int mouseX, int mouseY, float delta) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Identifier current = this.isHovered() ? hoverTexture : defaultTexture;
                ctx.drawTexture(current, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
                RenderSystem.disableBlend();
            }
        };
        *///? } else >=1.19.3 {
        /*return new TexturedButtonWidget(x, y, width, height, 0, 0, defaultTexture, onClick) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Identifier current = this.isHovered() ? hoverTexture : defaultTexture;
                RenderSystem.setShaderTexture(0, current);
                DrawableHelper.drawTexture(matrices, this.getX(), this.getY(),
                        0, 0, this.width, this.height, this.width, this.height);
                RenderSystem.disableBlend();
            }
        };
        *///? } else if >=1.17 {
        /*return new TexturedButtonWidget(x, y, width, height, 0, 0, defaultTexture, onClick) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Identifier current = this.isHovered() ? hoverTexture : defaultTexture;
                RenderSystem.setShaderTexture(0, current);
                DrawableHelper.drawTexture(matrices, this.x, this.y,
                        0, 0, this.width, this.height, this.width, this.height);
                RenderSystem.disableBlend();
            }
        };*/
        //? } else {
        /*return new TexturedButtonWidget(x, y, width, height, 0, 0, 0, defaultTexture, onClick) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                return false;
            }

            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.enableAlphaTest();
                RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                Identifier current = this.isHovered() ? hoverTexture : defaultTexture;
                minecraftClient.getTextureManager().bindTexture(current);
                drawTexture(matrices, this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
                RenderSystem.disableAlphaTest();
                RenderSystem.disableBlend();
            }
        };*/
        //? }
    }

    // ------------------- RESIZABLE BOX -------------------

    public static void drawResizableBox(Object ctx, Identifier texture,
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

    public static void drawResizableBox(Object ctx, Identifier texture, int x, int y, int width, int height, int edge, int tS) {
        drawResizableBox(ctx, texture, x, y, width, height, edge, tS, 0xFFFFFFFF);
    }

    public static float clampSpeed(float value) {
        return MathHelper.clamp(value, 0.001f, 1.0f);
    }

    public static void matricesUtil(Object ctx, float x, float y, int zIndex, Runnable action) {
        //? if >=1.21.6 {
        /*
        ((DrawContext)ctx).getMatrices().pushMatrix();
        ((DrawContext)ctx).getMatrices().translate(x, y);
        action.run();
        ((DrawContext)ctx).getMatrices().popMatrix();
        */
        //? } else if >=1.20 {
        ((DrawContext)ctx).getMatrices().push();
        ((DrawContext)ctx).getMatrices().translate(x, y, zIndex);
        action.run();
        ((DrawContext)ctx).getMatrices().translate(-x, -y, 0);
        ((DrawContext)ctx).getMatrices().pop();
        //? } else {
        /*MatrixStack matrices = (MatrixStack) ctx;
        matrices.push();
        matrices.translate(x, y, zIndex);
        action.run();
        matrices.translate(-x, -y, 0);
        matrices.pop();
        *///? }
    }

    public static void customScissor(Object ctx, int x, int y, int width, int height, Runnable action) {
        //? if >=1.21.6 {
        /*
        ((DrawContext)ctx).enableScissor(0, 0, 10000, 10000);
        ((DrawContext)ctx).getMatrices().pushMatrix();
        ((DrawContext)ctx).enableScissor(x, y, x + width, y + height);
        action.run();
        ((DrawContext)ctx).disableScissor();
        ((DrawContext)ctx).getMatrices().popMatrix();
        ((DrawContext)ctx).disableScissor();
        */
        //? } else if >=1.16 {
        MatrixStack matrices = /*? if >=1.20 { */ ((DrawContext)ctx).getMatrices()/*? } else {*/ /*(MatrixStack) ctx*/ /*? }*/;
        matrices.push();
        MinecraftClient mc = MinecraftClient.getInstance();
        Window win = mc.getWindow();
        double scale = win.getScaleFactor();
        int fbHeight = win.getFramebufferHeight();
        int sx = (int) Math.round(x * scale);
        int sy = (int) Math.round(fbHeight - (y + height) * scale);
        int sw = (int) Math.round(width * scale);
        int sh = (int) Math.round(height * scale);

        GUIUtils.drawFill(ctx, 0, 0, 0, 0, 0x00000000);
        RenderSystem.enableScissor(sx, sy, sw, sh);
        action.run();
        GUIUtils.drawFill(ctx, 0, 0, 0, 0, 0x00000000);
        RenderSystem.disableScissor();
        matrices.pop();
        //? }
    }

    public static void matricesScale(Object ctx, float scale, Runnable action) {
        //? if >=1.21.6 {
        /*
        ((DrawContext)ctx).getMatrices().pushMatrix();
        ((DrawContext)ctx).getMatrices().scale(scale, scale);
        action.run();
        ((DrawContext)ctx).getMatrices().popMatrix();
        */
        //? } else if >=1.20 {
        DrawContext dctx = (DrawContext) ctx;
        dctx.getMatrices().push();
        dctx.getMatrices().scale(scale, scale, 1.0f);
        action.run();
        dctx.getMatrices().pop();
        //? } else {
        /*MatrixStack matrices = (MatrixStack) ctx;
        matrices.push();
        matrices.scale(scale, scale, 1.0f);
        action.run();
        matrices.pop();
        *///? }
    }

    // Draw

    public static void drawFill(Object ctx, int x1, int y1, int x2, int y2, int color) {
        //? if >=1.20 {
        DrawContext context = (DrawContext) ctx;
        context.fill(x1, y1, x2, y2, color);
        //? } else {
        /*MatrixStack matrices = (MatrixStack) ctx;
        DrawableHelper.fill(matrices, x1, y1, x2, y2, color);
        *///? }
    }

    public static void drawItem(Object ctx, ItemStack stack, int x, int y, float scale) {
        //? if >=1.20 {
        ((DrawContext)ctx).drawItem(stack, x, y);
       //?} else if >=1.19.4 {
        /*ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        itemRenderer.renderInGui((MatrixStack) ctx, stack, x, y);
        *///?} else if >=1.17 {
        /*
        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();
        MatrixStack matrices = (MatrixStack) ctx;
        matrices.push();
        matrices.translate((float)x, (float)y, 150.0F);
        matrices.translate(8.0F, 8.0F, 0.0F);
        //? if >=1.19.3 {
        matrices.multiplyPositionMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
        //? } else if >=1.18 {
        matrices.peek().getPositionMatrix().multiply(Matrix4f.scale(1.0F, -1.0F, 1.0F));
        //? } else {
        matrices.peek().getModel().multiply(Matrix4f.scale(1.0F, -1.0F, 1.0F));
        //? }
        matrices.scale(16.0F, 16.0F, 16.0F);
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        //? if >=1.18 {
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);
        //? } else if >=1.17 {
        BakedModel model = itemRenderer.getHeldItemModel(stack, null, null, 0);
        //? } else {
        BakedModel model = itemRenderer.getHeldItemModel(stack, null, null);
        //? }
        boolean bl = !model.isSideLit();
        if (bl) DiffuseLighting.disableGuiDepthLighting();

        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        //? if >=1.18 {
        matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        RenderSystem.applyModelViewMatrix();
        //? } else {
        matrixStack.method_34425(matrices.peek().getModel());
        RenderSystem.applyModelViewMatrix();
        //? }

        itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, new MatrixStack(), immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) DiffuseLighting.enableGuiDepthLighting();


        matrices.pop();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        */
        //? } else {
        /*MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();
        MatrixStack matrices = (MatrixStack) ctx;
        RenderSystem.pushMatrix();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.multMatrix(matrices.peek().getModel());
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, 150.0F);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);
        BakedModel model = itemRenderer.getHeldItemModel(stack, null, null);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) DiffuseLighting.disableGuiDepthLighting();

        MatrixStack matrixStack = new MatrixStack();

        itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) DiffuseLighting.enableGuiDepthLighting();

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();*/
        //? }
    }
    public static void drawItem(Object ctx, ItemStack stack, int x, int y) {
        drawItem(ctx, stack, x, y, 1);
    }
}