package com.nitsha.binds.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.MainClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GUIUtils {
    // Cut a string
    public static String truncateString(String str, int maxLength) {
        if (str.length() > maxLength) {
            return str.substring(0, maxLength) + "...";
        }
        return str;
    }

    public static void addText(DrawContext ctx, Text text, int width, int offsetX, int offsetY) {
        addText(ctx, text, width, offsetX, offsetY, "left", "top");
    }

    public static void addText(DrawContext ctx, Text text, int width, int offsetX, int offsetY, String hAlign, String vAlign) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        int chWidth = textRenderer.getWidth(text);
        int chHeight = textRenderer.fontHeight;
        int alignCoordX = switch (hAlign) {
            case "left" -> offsetX;
            case "center" -> (width / 2) - (chWidth / 2) + offsetX;
            case "right" -> width - chWidth + offsetX;
            default -> offsetX;
        };
        int alignCoordY = switch (vAlign) {
            case "top" -> offsetY;
            case "center" -> offsetY - (chHeight / 2);
            case "bottom" -> offsetY - chHeight;
            default -> offsetY;
        };
        ctx.drawText(textRenderer, text, alignCoordX, alignCoordY, 0xFFFFFFFF, true);
    }

    // Draw resizable box
    public static void drawResizableBox(DrawContext ctx, Identifier texture, int x, int y, int width, int height, int edge, int tS) {
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
            ctx.drawTexture(RenderLayer::getGuiTextured, texture,
                    positions[i][0], positions[i][1], parts[i][0], parts[i][1],
                    sizes[i][0], sizes[i][1], tSizes[i][0], tSizes[i][1]);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class IconButton extends PressableWidget {
        private final Runnable onClick;
        private boolean enabled = true;
        private final Identifier ICON;
        int iconSize, iconX, iconY;

        public IconButton(int x, int y, int width, int height, int iconSize, int iconX, int iconY, String iconName, Runnable onClick) {
            super(x, y, width, height, Text.empty());
            this.onClick = onClick;
            this.iconSize = iconSize;
            this.iconX = iconX;
            this.iconY = iconY;
            this.ICON = MainClass.id("textures/gui/sprites/" + iconName + ".png");
        }

        @Override
        public void onPress() {
            if (enabled) this.onClick.run();
        }

        public void setEnabledStatus(boolean s) {
            this.enabled = s;
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderWidget(context, mouseX, mouseY, delta);
            renderOverlay(context, mouseX, mouseY, delta);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }

        protected void renderOverlay(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawTexture(RenderLayer::getGuiTextured, ICON, this.getX() + iconX, this.getY() + iconY, 0, 0, iconSize, iconSize, 20, 20);
        }
    }
}
