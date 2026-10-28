package com.nitsha.binds.guinew.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.guinew.utils.Textures;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class TabButton extends NitshaAbstractWidget {

    private final Consumer<TabButton> onClick;
    private final Component name;
    private boolean selected;
    private float speed = Main.GLOBAL_ANIMATION_SPEED;
    private float pointY = 0.0f;

    public TabButton(int x, int y, boolean selected, Component name, Consumer<TabButton> onClick) {
        super(x, y, 0, 16, TextUtils.empty());
        this.onClick = onClick;
        this.selected = selected;
        this.name = TextUtils.small(name);

        String userLanguage = GUIUtils.getUL();
        int widthOffset = 10;
        if (userLanguage.equals("ja_jp") || userLanguage.equals("ru_ru")) {
            widthOffset = 6;
        }

        int width = Minecraft.getInstance().font.width(this.name) + widthOffset;
        this.setWidth(width);
    }

    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        float targetPointX = (this.selected) ? this.getY() : this.getY() + 2;
        pointY = Mth.lerp(GUIUtils.clampSpeed(speed * delta), pointY, targetPointX);
        RenderUtils.drawResizableBox(ctx, Textures.BACKGROUND_DARK, this.getX(), Math.round(pointY), this.getWidth(), this.getHeight() + 10, 7, 15);
        RenderUtils.drawText(ctx, name, this.width, this.getX(), Math.round(pointY) + 5, (this.selected) ? 0xFFFFFFFF : 0xFFA8A8A8, "center", "top");
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || button != InputConstants.MOUSE_BUTTON_LEFT || !isMouseOver(mouseX, mouseY)) {
            return false;
        }

        playDownSound(Minecraft.getInstance().getSoundManager());

        if (onClick != null) {
            onClick.accept(this);
        }

        return true;
    }
}