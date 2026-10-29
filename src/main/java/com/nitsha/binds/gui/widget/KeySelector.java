package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.widget.button.BedrockButton;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public abstract class KeySelector extends BedrockButton {

    private static final Identifier SELECT = Main.id("textures/gui/btns/select_icon.png");
    private String selectedItem = "";

    public KeySelector(int x, int y, int width, int height, Runnable onRelease) {
        super(TextUtils.empty(), x, y, width, height, true, onRelease);
        updateName();
    }

    public void setWidth(int width) {
        this.width = width;
        updateName();
    }

    public void setSelectedItem(String sI) {
        this.selectedItem = sI;
        updateName();
    }

    public String getSelectedItem() {
        return this.selectedItem;
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.onRender(ctx, mouseX, mouseY, delta);

        boolean isHovered = isMouseOver(mouseX, mouseY);
        RenderUtils.drawTexture(ctx, SELECT, this.getX() + 4, this.getY() + 7 + Math.round(this.getOffsetY()), 0, 0, 6, 5, 6, 5, (isHovered || this.isPressed()) ? this.getTextHoverColor() : this.getTextColor());
    }

    protected void updateName() {
        if (selectedItem.isEmpty()) {
            setText(TextUtils.translatable("nitsha.binds.advances.noSelectedKeyItem"));
        }
    };
}