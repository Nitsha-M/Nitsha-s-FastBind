package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.gui.panels.PresetSelector;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import net.minecraft.client.Minecraft;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.vertex.PoseStack;
 *///?}
import net.minecraft.client.gui.components.AbstractWidget;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PresetListItem extends AbstractWidget {
    private final PresetSelector parent;
    private final TextField newNameField;

    private final AbstractWidget editBtn;
    private final AbstractWidget deleteBtn;
    private final AbstractWidget saveBtn;
    private final AbstractWidget deleteConfBtn;
    private static final ResourceLocation EDIT = Main.idSprite("editpreset_normal");
    private static final ResourceLocation EDIT_HOVER = Main.idSprite("editpreset_hover");
    private static final ResourceLocation SAVE = Main.idSprite("savepreset_normal");
    private static final ResourceLocation SAVE_HOVER = Main.idSprite("savepreset_hover");
    private static final ResourceLocation DELETE = Main.idSprite("delete_small_normal");
    private static final ResourceLocation DELETE_HOVER = Main.idSprite("delete_small_hover");

    private boolean isEditing = false;
    private String name;
    private int index;

    private int x, y;

    private long deleteConfirmationTime = 0;

    public PresetListItem(PresetSelector parent, String name, int x, int y, int width, int height, int index) {
        super(x, y, width, height, Component.literal(""));
        this.parent = parent;
        this.name = name;
        this.index = index;
        this.x = x;
        this.y = y;

        this.newNameField = new TextField(Minecraft.getInstance().font, x + 3, y + 3, width - 20,height - 5, 40, name,"Preset name");
        this.newNameField.setEnterEvent(this::savePreset);
        this.newNameField.setEscapeEvent(this::stopEditing);
        this.newNameField.setAnimatedPlaceholder(false);
        this.editBtn = GUIUtils.createTexturedBtn(x + 111, y + 6, 9, 9, new ResourceLocation[]{EDIT, EDIT_HOVER}, button -> {
            for (PresetListItem item : parent.getItems()) {
                if (item.isEditing()) {
                    item.stopEditing();
                }
            }
            isEditing = true;
            //? if >=1.19.4 {
            this.newNameField.setFocused(true);
            //?} else {
            /*this.newNameField.changeFocus(true);*/
            //?}
            TextField.setFocusedField(this.newNameField);
        });

        this.deleteBtn = GUIUtils.createTexturedBtn(x + 101, y + 6, 9, 9, new ResourceLocation[]{DELETE, DELETE_HOVER}, button -> {
            deleteConfirmationTime = System.currentTimeMillis();
        });

        this.deleteConfBtn = GUIUtils.createTexturedBtn(x + 91, y + 6, 9, 9, new ResourceLocation[]{SAVE, SAVE_HOVER}, button -> {
            if (index > 0 && index == BindsStorage.presets.size() - 1) parent.screen.selectPreset(index - 1);
            BindsStorage.removePreset(index);
            if (index == 0) parent.screen.selectPreset(0);
            parent.generatePresetsList();
        });

        this.saveBtn = GUIUtils.createTexturedBtn(x + 111, y + 6, 9, 9, new ResourceLocation[]{SAVE, SAVE_HOVER}, button -> {
            savePreset();
        });
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void stopEditing() {
        isEditing = false;
        this.newNameField.setText(this.name);
    }

    public void onClick(double mouseX, double mouseY) {
        if (!isEditing) {
            parent.screen.selectPreset(index);
            parent.openSelector(false);
        }
    }

    public boolean isDeleteConfirmation() {
        return System.currentTimeMillis() - deleteConfirmationTime < 5000;
    }


    public boolean isEditing() {
        return isEditing;
    }

    public void savePreset() {
        isEditing = false;
        String getName = this.newNameField.getText();
        String newName = (getName.isEmpty()) ? "Preset " +  (index + 1) : getName;
        BindsStorage.renamePreset(this.index, newName);
        this.newNameField.setText(newName);
        this.name = newName;
    }

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        GuiGraphics c = (GuiGraphics) ctx;
        //?} else {
        /*PoseStack c = (PoseStack) ctx;
         *///?}
        if (index > 0) GUIUtils.drawFill(ctx, getX() + 3, getY(), getX() + getWidth() - 3, getY() + 1, 0xFF555555);
        if (isEditing) {
            this.newNameField.render(c, mouseX, mouseY, delta);
            this.saveBtn.render(c, mouseX, mouseY, delta);
        } else {
            if (this.isHoveredOrFocused() && parent.isOpen()) GUIUtils.drawFill(c, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x0DFFFFFF);
            GUIUtils.addText(c, Component.literal(GUIUtils.truncateString(this.name, 15)), 0, getX() + 3, getY() + 7, "left", "top",0xFFFFFFFF, false);
            this.editBtn.render(c, mouseX, mouseY, delta);
            this.deleteBtn.render(c, mouseX, mouseY, delta);
            if (isDeleteConfirmation()) this.deleteConfBtn.render(c, mouseX, mouseY, delta);
        }
    }

    //? if >1.20.2 {
    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    //?} else if >=1.20 {
    /*@Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?} else {
    /*@Override
    public void renderWidget(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!parent.isOpen()) return false;

        if (isEditing) {
            if (newNameField.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (saveBtn.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        } else {
            if (editBtn.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (deleteBtn.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (isDeleteConfirmation()) {
                if (deleteConfBtn.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!parent.isOpen() || !isEditing) return false;
        return newNameField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!parent.isOpen() || !isEditing) return false;
        return newNameField.charTyped(codePoint, modifiers);
    }

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    //?} else if >=1.17 {
    /*@Override
    public void updateNarration(NarrationElementOutput builder) {
    }*/
    //?}
}
