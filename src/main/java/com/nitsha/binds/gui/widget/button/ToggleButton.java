package com.nitsha.binds.gui.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class ToggleButton extends NitshaAbstractWidget {
    private static Identifier TEXTURE;

    private boolean firstRender = true;
    protected boolean toggled;
    private final Runnable onClick;
    private float pointX = 0.0f;
    private float speed = Main.GLOBAL_ANIMATION_SPEED + 0.2f;

    private final MutableComponent name;

    public ToggleButton(MutableComponent name, int x, int y, int width, int height, boolean border, boolean toggled, Runnable onClick) {
        super(x, y, width, height, TextUtils.empty());
        this.name = TextUtils.small(name);
        this.onClick = onClick;
        this.toggled = toggled;

        if (border) {
            TEXTURE = Main.id("textures/gui/gui_elements_border.png");
        } else {
            TEXTURE = Main.id("textures/gui/gui_elements.png");
        }

        int tX = this.getX() + width - 23 - 4;
        this.pointX = this.toggled ? tX + 11 : tX;
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        int tX = this.getX() + this.width - 23 - 4;
        int tY = this.getY() + height / 2 - 6;
        if (firstRender) {
            pointX = toggled ? tX + 10 : tX;
            firstRender = false;
        } else {
            float targetPointX = toggled ? tX + 10 : tX;
            pointX = Mth.lerp(RenderUtils.clampSpeed(speed * delta), pointX, targetPointX);
        }

        //? <1.17 {
        // RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        //?}

        if (isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawFill(ctx, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x0D000000);
        }

        RenderUtils.drawText(ctx, name, 0, this.getX() + 4, this.getY() + (this.height / 2), "left", "center", 0xFF212121, false);

        // Button
        RenderUtils.drawTexture(ctx, TEXTURE, tX, tY, 0, 0, 23, 12, 256, 256);

        // Point
        RenderUtils.drawTexture(ctx, TEXTURE, Math.round(pointX), tY - 1, 0, 12, 13, 12, 256, 256);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || button != InputConstants.MOUSE_BUTTON_LEFT || !isMouseOver(mouseX, mouseY)) {
            return false;
        }

        playDownSound(Minecraft.getInstance().getSoundManager());
        this.onClick.run();
        this.toggled = !this.toggled;

        return true;
    }
}
