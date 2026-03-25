package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.GuiGraphics;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

//? if >=1.21.9 {
// import net.minecraft.client.input.MouseButtonEvent;
// import net.minecraft.client.input.InputWithModifiers;
//? }

public class TabButton extends AbstractButton {
    private static final ResourceLocation BACKGROUND = Main.id("textures/gui/test/editor_bg_dark_flat.png");
    private final Consumer<TabButton> onClick;
    private final Component name;
    private boolean selected;
    private float speed = Main.GLOBAL_ANIMATION_SPEED;
    private float pointY = 0.0f;

    private int x, y;

    public TabButton(int x, int y, boolean selected, Component name, Consumer<TabButton> onClick) {
        super(x, y, 0, 16, TextUtils.empty());
        this.onClick = onClick;
        this.selected = selected;
        this.name = name;
        this.x = x;
        this.y = y;

        String userLanguage = GUIUtils.getUL();
        int widthOffset = 10;
        if (userLanguage.equals("ja_jp") || userLanguage.equals("ru_ru")) {
            widthOffset = 6;
        }

        int width = Minecraft.getInstance().font.width(name) + widthOffset;
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

    @Override
    public void onPress() {}

    @Override
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        float targetPointX = (this.selected) ? this.getY() : this.getY() + 2;
        pointY = Mth.lerp(GUIUtils.clampSpeed(speed * delta), pointY, targetPointX);
        GUIUtils.drawResizableBox(ctx, BACKGROUND, this.getX(), Math.round(pointY), this.getWidth(), this.getHeight() + 10, 7, 15);
        GUIUtils.addText(ctx, name, this.width, this.getX(), Math.round(pointY) + 5, (this.selected) ? 0xFFFFFFFF : 0xFFA8A8A8, "center", "top");
    }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    //? } else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) {
    }*/
    //? }
}