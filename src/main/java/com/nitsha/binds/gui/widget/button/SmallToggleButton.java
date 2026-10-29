package com.nitsha.binds.gui.widget.button;

import com.nitsha.binds.multiversions.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class SmallToggleButton extends SmallTextButton {

    private Component toggleName;
    public int textOffsetX = 0;
    public int textOffsetY = 0;

    public SmallToggleButton(Component name, int x, int y, int width, int height, boolean toggled, Runnable onClick) {
        super(name.copy(), x, y, 5, 0xFFA9A9A9, 0xFF3C8527, 0xFF232425, 0xFFFFFFFF, width, "left", null, onClick);
        this.toggled = toggled;
        this.height = height;
        this.toggleName = name;
    }

    public void setName(Component name) {
        this.toggleName = name;
    }

    public void setTextOffset(int x, int y) {
        this.textOffsetX = x;
        this.textOffsetY = y;
    }

    @Override
    protected void renderContents(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        boolean activeState = isActiveState();

        RenderUtils.drawResizableBox(ctx, activeState ? getHoverTexture() : getNormalTexture(), getX(), getY(), getWidth(), getHeight(), 2, 5, activeState ? hoverColor : color);

        RenderUtils.drawText(ctx, toggleName, 0, getX() + (getWidth() / 2) + textOffsetX, getY() + (getHeight() / 2) + textOffsetY, "center", "center", activeState ? hoverTextColor : textColor, false);
    }
}
