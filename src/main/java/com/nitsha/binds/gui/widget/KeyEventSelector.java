package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.mixin.KeyMappingAccessor;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.Minecraft;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
 *///?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

//? if >=1.21.9 {
/*import net.minecraft.client.input.InputWithModifiers;*/
//? }

public class KeyEventSelector extends BedrockButton {

    private static final ResourceLocation SELECT = Main.id("textures/gui/btns/select_icon.png");
    private String selectedItem = "";

    public KeyEventSelector(int x, int y, int width, int height) {
        super("", x, y, width, height, true, () ->{});
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
        //? <1.21.9 {
    public void onPress() {
        super.onPress();
        //? } else {
        /*public void onPress(InputWithModifiers inputWithModifiers) {
        super.onPress(inputWithModifiers);
        */
        //? }
        EventBus.on("selectKeyEvent.open", (Void v) -> {});
        EventBus.emit("selectKeyEvent.open", null);
    }

    //? if >=1.21.11 {
    /*@Override
    public void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderContents(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }*/
    //? } else if >1.20.2 {
    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    //? } else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    *///? } else if >=1.19.4 {
    /*@Override
    public void render(PoseStack context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    *///?} else {
    /*@Override
    public void render(PoseStack context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
        renderOverlay(context, mouseX, mouseY, delta);
    }
    */
    //? }

    protected void renderOverlay(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        GuiGraphics context = (GuiGraphics) ctx;
        //?} else {
        /*PoseStack context = (PoseStack) ctx;*/
        //?}
        GUIUtils.adaptiveDrawTexture(ctx, SELECT, this.getX() + 4, this.getY() + 7 + Math.round(this.getOffsetY()), 0, 0, 6, 5, 6, 5, (isHovered || this.isPressed()) ? this.getTextHoverColor() : this.getTextColor());
    }
}