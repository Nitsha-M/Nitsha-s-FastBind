package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.mixin.KeyMappingAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

//? if >=1.21.9 {
/*import net.minecraft.client.input.InputWithModifiers;*/
//? }

public class KeyEventSelector extends BedrockButton {

    private static final ResourceLocation SELECT = Main.id("textures/gui/btns/select_icon.png");
    private String selectedItem = "";

    public KeyEventSelector(int x, int y, int width, int height, Runnable onRelease) {
        super("", x, y, width, height, true, onRelease);
        updateName();
    }

    private void updateName() {
        if (selectedItem.isEmpty()) {
            setName(TextUtils.translatable("nitsha.binds.advances.noSelectedKeyItem").getString());
        } else {
            Component name = TextUtils.translatable(KeyMappingAccessor.binds$getAll().get(selectedItem).getName());
            int maxWidth = this.width - 8;
            int avgCharWidth = 7;
            setName(GUIUtils.truncateString(name.getString(), maxWidth / avgCharWidth));
        }
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
    public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        super.renderWidget(ctx, mouseX, mouseY, delta);

        boolean isHovered = isMouseOver(mouseX, mouseY);
        GUIUtils.adaptiveDrawTexture(ctx, SELECT, this.getX() + 4, this.getY() + 7 + Math.round(this.getOffsetY()), 0, 0, 6, 5, 6, 5, (isHovered || this.isPressed()) ? this.getTextHoverColor() : this.getTextColor());
    }
}