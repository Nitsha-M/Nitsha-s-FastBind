package com.nitsha.binds.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.widget.AnimatedWindow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
//? if >1.20.1 {
import net.minecraft.client.gui.screen.ButtonTextures;
//? }
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
//?if >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
*///?} else {
import net.minecraft.client.render.RenderLayer;
//? }

public class GUIUtils {
    // Cut a string
    public static String truncateString(String str, int maxLength) {
        if (str.length() > maxLength) {
            return str.substring(0, maxLength) + "...";
        }
        return str;
    }

    public static void addText(DrawContext ctx, Text text, int width, int offsetX, int offsetY) {
        addText(ctx, text, width, offsetX, offsetY, "left", "top", 0xFFFFFFFF);
    }

    public static void addText(DrawContext ctx, Text text, int width, int offsetX, int offsetY, String hAlign, String vAlign) {
        addText(ctx, text, width, offsetX, offsetY, hAlign, vAlign, 0xFFFFFFFF);
    }

    public static void addText(DrawContext ctx, Text text, int width, int offsetX, int offsetY, int color) {
        addText(ctx, text, width, offsetX, offsetY, "left", "top", color);
    }

    public static void addText(DrawContext ctx, Text text, int width, int offsetX, int offsetY, String hAlign, String vAlign, int color) {
        if (text == null || text.getString().isBlank()) return;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int chWidth = textRenderer.getWidth(text);
        int chHeight = textRenderer.fontHeight;

        int alignCoordX = switch (hAlign) {
            case "left" -> offsetX;
            case "center" -> (width / 2) - (chWidth / 2) + offsetX;
            case "right" -> offsetX - chWidth;
            default -> offsetX;
        };

        int alignCoordY = switch (vAlign) {
            case "top" -> offsetY;
            case "center" -> offsetY - (chHeight / 2);
            case "bottom" -> offsetY - chHeight;
            default -> offsetY;
        };

        ctx.drawTextWithShadow(textRenderer, text, alignCoordX, alignCoordY, color);
    }

    public static void drawResizableBox(DrawContext ctx, Identifier texture, int x, int y, int width, int height, int edge, int tS) {
        drawResizableBox(ctx, texture, x, y, width, height, edge, tS, 0xFFFFFFFF);
    }

    public static void adaptiveDrawTexture(DrawContext ctx, Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int color) {
        //? if >=1.21.6 {
        /*ctx.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, textureWidth, textureHeight, color);
         *///?} else if <= 1.21.1 {
        /*RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float red   = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue  = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        RenderSystem.setShaderColor(red, green, blue, alpha);
        ctx.drawTexture(texture, x, y, 0, u, v,width, height, textureWidth, textureHeight);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        *///?} else {
        ctx.drawTexture(RenderLayer::getGuiTextured, texture, x, y, u, v, width, height, textureWidth, textureHeight, color);
        //? }
    }

    public static void adaptiveDrawTexture(DrawContext ctx, Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        adaptiveDrawTexture(ctx, texture, x, y, u, v, width, height, textureWidth, textureHeight, 0xFFFFFFFF);
    }

    public static void adaptiveDrawTexture(DrawContext ctx, Identifier texture, int x, int y, int u, int v, int width, int height, int textureWidth) {
        adaptiveDrawTexture(ctx, texture, x, y, u, v, width, height, textureWidth, textureWidth, 0xFFFFFFFF);
    }

    public static void adaptiveDrawTexture(DrawContext ctx, Identifier texture, int x, int y, int u, int v, int width, int height) {
        adaptiveDrawTexture(ctx, texture, x, y, u, v, width, height, 256, 256, 0xFFFFFFFF);
    }

    public static TexturedButtonWidget createTexturedBtn(int x, int y, int width, int height, Identifier[] textures, ButtonWidget.PressAction onClick) {
        Identifier defaultTexture = textures[0];
        Identifier hoverTexture = textures[1];

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
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                return false;
            }
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
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                return false;
            }
            @Override
            public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                super.renderButton(context, mouseX, mouseY, delta);
                RenderSystem.disableBlend();
            }
        };
        *///? } else {
        /*return new TexturedButtonWidget(x, y, width, height, 0, 0, defaultTexture, onClick) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                return false;
            }
            @Override
            public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Identifier current = this.isHovered() ? hoverTexture : defaultTexture;
                context.drawTexture(current, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
                RenderSystem.disableBlend();
            }
        };
         *///? }
    }

        // Draw resizable box
    public static void drawResizableBox(DrawContext ctx, Identifier texture, int x, int y, int width, int height, int edge, int tS, int color) {
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
}