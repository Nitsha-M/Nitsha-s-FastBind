package com.nitsha.binds.gui.widget;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.gui.utils.GUIUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SmallTextButton extends ClickableWidget {
    private static final Identifier NORMAL = MainClass.id("textures/gui/btns/smallbtn_normal.png");
    private static final Identifier HOVER = MainClass.id("textures/gui/btns/smallbtn_hover.png");
    private final Runnable onClick;
    private final String name;
    private final int color;

    private int x, y;

    public SmallTextButton(String name, int x, int y, int width, int color, Runnable onClick) {
        super(x, y, width, 9, Text.of(""));
        this.name = name;
        this.onClick = onClick;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void onClick(double mouseX, double mouseY) {
        this.onClick.run();
    }

    //? if >1.20.2 {
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    //? } else if >=1.20 {
    /*@Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///? } else {
    /*@Override
    public void renderButton(MatrixStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///? }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textWidth = textRenderer.getWidth(name);

        GUIUtils.drawResizableBox(ctx, (hovered) ? HOVER : NORMAL, getX(), getY(), getWidth(), getHeight(), 2, 5, color);

        GUIUtils.addText(
                ctx, Text.of(name), 0,
                this.getX() + ((getWidth() / 2) - (textWidth / 2)),
                this.getY() + ((this.height / 2) - (textRenderer.fontHeight / 2)),
                "top", "left", 0xFFFFFFFF, false
        );
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
