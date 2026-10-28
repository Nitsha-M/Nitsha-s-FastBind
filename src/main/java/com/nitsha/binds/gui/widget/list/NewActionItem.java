package com.nitsha.binds.gui.widget.list;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.panels.NewAction;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class NewActionItem extends NitshaAbstractWidget {
    private static final Identifier ADD_NEW = Main.id("textures/gui/test/add_new_icon.png");

    private final NewAction parent;
    private final String name;
    private final String typeId;
    private final String value;
    private final int color;

    public NewActionItem(NewAction parent, String name, int x, int y, int width, int height, String typeId, String value, int color) {
        super(x, y, width, height, TextUtils.empty());
        this.parent = parent;
        this.name = name;
        this.typeId = typeId;
        this.value = value;
        this.color = color;
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.drawFill(ctx, getX() + 3, getY() + 5, getX() + 5, getY() + 12, color);
        RenderUtils.drawFill(ctx, getX() + 2, getY(), getX() + getWidth() - 2, getY() + 1, 0xFF555555);
        RenderUtils.drawText(ctx, TextUtils.small(TextUtils.literal(GUIUtils.truncateString(name, 25))), 0, getX() + 7, getY() + 8, "left", "center", 0xFFFFFFFF, false);
        RenderUtils.drawTexture(ctx, ADD_NEW, getX() + getWidth() - 10, getY() + 6, 0, 0, 5, 5, 5, 5);

        if (isMouseOver(mouseX, mouseY) && parent.isOpen()) {
            RenderUtils.drawFill(ctx, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x0DFFFFFF);
        }
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || !parent.isOpen() || button != InputConstants.MOUSE_BUTTON_LEFT || !isMouseOver(mouseX, mouseY)) {
            return false;
        }

        playDownSound(Minecraft.getInstance().getSoundManager());
        parent.openSelector(false);
        parent.addAction(typeId, value);
        return true;
    }
}
