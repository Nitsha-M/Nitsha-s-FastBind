package com.nitsha.binds.gui.widget.button;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class TexturedButton extends NitshaAbstractWidget {

    private final Identifier defaultTexture;
    private final Identifier hoverTexture;
    private final Consumer<TexturedButton> onClick;

    public TexturedButton(int x, int y, int width, int height, Identifier defaultTexture, Identifier hoverTexture, Consumer<TexturedButton> onClick) {
        super(x, y, width, height, TextUtils.empty());
        this.defaultTexture = defaultTexture;
        this.hoverTexture = hoverTexture;
        this.onClick = onClick;
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.onRender(ctx, mouseX, mouseY, delta);

        if (!visible) return;

        RenderUtils.drawTexture(ctx, isHovered() ? hoverTexture : defaultTexture, getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
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