package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ScreenTooltip extends NitshaAbstractWidget {

    private static final Identifier TOOLTIP = Main.id("textures/gui/btns/tooltip.png");

    private String text = "";
    private int color = 0xFFFFFFFF;

    public ScreenTooltip(int x, int y, int width) {
        super(x, y, width, 13, TextUtils.empty());
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
    protected void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        Component tooltip = TextUtils.small(TextUtils.literal(this.text));
        int tooltipWidth = Minecraft.getInstance().font.width(tooltip);

        int tx = this.getX() + ((this.getWidth() - tooltipWidth) / 2);
        int ty = this.getY();

        int transparentColor = (color & 0x00FFFFFF) | (0xCC << 24);
        RenderUtils.matricesUtil(ctx, tx, ty, 200, () -> {
            RenderUtils.drawResizableBox(ctx, TOOLTIP, 0, 0, tooltipWidth + 6, this.height, 3, 7, transparentColor);
            RenderUtils.drawText(ctx, tooltip, tooltipWidth + 6, 0, 7, "center", "center", 0xFFFFFFFF, false);
        });
    }
}
