package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

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
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        this.isHovered = isMouseOver(mouseX, mouseY);

        GUIUtils.drawResizableBox(ctx, isActiveState() ? getHoverTexture() : getNormalTexture(), getX(), getY(), getWidth(), getHeight(), 2, 5, isActiveState() ? hoverColor : color);

        GUIUtils.addText(ctx, toggleName, 0,
                this.getX() + (width / 2) + textOffsetX,
                this.getY() + (this.height / 2) + textOffsetY,
                "center", "center",
                isActiveState() ? hoverTextColor : textColor, false);
    }
}