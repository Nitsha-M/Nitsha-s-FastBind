package com.nitsha.binds.gui.widget;

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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class TabButton extends ClickableWidget {
    private static final Identifier BACKGROUND = MainClass.id("textures/gui/test/editor_bg_dark_flat.png");
    private final Consumer<TabButton> onClick;
    private final Text name;
    private boolean selected;
    private float speed = MainClass.GLOBAL_ANIMATION_SPEED;
    private float pointY = 0.0f;

    private int x, y;

    public TabButton(int x, int y, boolean selected, Text name, Consumer<TabButton> onClick) {
        super(x, y, 0, 16, Text.of(""));
        this.onClick = onClick;
        this.selected = selected;
        this.name = name;
        this.x = x;
        this.y = y;

        int width = MinecraftClient.getInstance().textRenderer.getWidth(name) + 10;
        this.setWidth(width);
    }

    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (onClick != null) {
            onClick.accept(this);
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
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

    private void rndr(Object context, int mouseX, int mouseY, float delta) {
        float targetPointX = (this.selected) ? this.getY() : this.getY() + 2;
        pointY = MathHelper.lerp(GUIUtils.clampSpeed(speed * delta), pointY, targetPointX);
        GUIUtils.drawResizableBox(context, BACKGROUND, this.getX(), Math.round(pointY), this.getWidth(), this.getHeight() + 10, 7, 15);
        GUIUtils.addText(context, name, 0, this.getX() + 5, Math.round(pointY) + 5, (this.selected) ? 0xFFFFFFFF : 0xFFA8A8A8);
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
