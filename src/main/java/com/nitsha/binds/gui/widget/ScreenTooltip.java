package com.nitsha.binds.gui.widget;

//~ !widget

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ScreenTooltip extends AbstractWidget {

    private static final ResourceLocation TOOLTIP = Main.id("textures/gui/btns/tooltip.png");

    private String text = "";
    private int color = 0xFFFFFFFF;

    private int x, y, width, height;

    public ScreenTooltip(int x, int y, int width) {
        super(x, y, width, 0, TextUtils.empty());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 13;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        Component tooltip = TextUtils.literal(this.text);
        int tooltipWidth = Minecraft.getInstance().font.width(tooltip);

        int tx = this.x + ((this.width - tooltipWidth) / 2);
        int ty = this.y;

        int transparentColor = (color & 0x00FFFFFF) | (0xCC << 24);
        GUIUtils.matricesUtil(ctx, tx, ty, 200, () -> {
            GUIUtils.drawResizableBox(ctx, TOOLTIP, 0, 0, tooltipWidth + 6, this.height, 3, 7, transparentColor);
            GUIUtils.addText(ctx, tooltip, tooltipWidth + 6, 0, 7, "center", "center", 0xFFFFFFFF, false);
        });
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
