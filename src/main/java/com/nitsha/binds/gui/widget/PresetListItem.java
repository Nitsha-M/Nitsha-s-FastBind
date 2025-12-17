package com.nitsha.binds.gui.widget;

import com.nitsha.binds.Main;
import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.gui.panels.PresetSelector;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
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

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class PresetListItem extends AbstractWidget {
    private final PresetSelector parent;
    private final ScrollableWindow scrollableList;
    private final TextField newNameField;

    private final AbstractWidget editBtn;
    private final AbstractWidget deleteBtn;
    private final AbstractWidget saveBtn;
    private final AbstractWidget deleteConfBtn;
    private final AbstractWidget moveTop;
    private final AbstractWidget moveBottom;
    private static final ResourceLocation EDIT = Main.idSprite("editpreset_normal");
    private static final ResourceLocation EDIT_HOVER = Main.idSprite("editpreset_hover");
    private static final ResourceLocation SAVE = Main.idSprite("savepreset_normal");
    private static final ResourceLocation SAVE_HOVER = Main.idSprite("savepreset_hover");
    private static final ResourceLocation DELETE = Main.idSprite("delete_small_normal");
    private static final ResourceLocation DELETE_HOVER = Main.idSprite("delete_small_hover");
    private static final ResourceLocation TOP = Main.idSprite("top_normal");
    private static final ResourceLocation TOP_HOVER = Main.idSprite("top_hover");
    private static final ResourceLocation BOTTOM = Main.idSprite("bottom_normal");
    private static final ResourceLocation BOTTOM_HOVER = Main.idSprite("bottom_hover");
    private static final ResourceLocation ARROW_DISABLED = Main.id("textures/gui/test/arrow_disabled.png");


    private boolean isEditing = false;
    private String name;
    private int index;

    private int x, y;

    private long deleteConfirmationTime = 0;

    public PresetListItem(PresetSelector parent, ScrollableWindow scrollableList, String name, int x, int y, int width, int height, int index) {
        super(x, y, width, height, TextUtils.empty());
        this.parent = parent;
        this.scrollableList = scrollableList;
        this.name = name;
        this.index = index;
        this.x = x;
        this.y = y;

        this.newNameField = new TextField(Minecraft.getInstance().font, x + 3, y + 3, width - 20,height - 5, 40, name,"Preset name");
        this.newNameField.setEnterEvent(this::savePreset);
        this.newNameField.setEscapeEvent(this::stopEditing);
        this.newNameField.setAnimatedPlaceholder(false);
        this.editBtn = GUIUtils.createTexturedBtn(x + 111, y + 7, 9, 9, new ResourceLocation[]{EDIT, EDIT_HOVER}, button -> {
            for (PresetListItem item : parent.getItems()) {
                if (item.isEditing()) {
                    item.stopEditing();
                }
            }
            isEditing = true;
            this.newNameField.setFocus();
        });

        this.deleteBtn = GUIUtils.createTexturedBtn(x + 101, y + 7, 9, 9, new ResourceLocation[]{DELETE, DELETE_HOVER}, button -> {
            deleteConfirmationTime = System.currentTimeMillis();
        });

        this.deleteConfBtn = GUIUtils.createTexturedBtn(x + 91, y + 7, 9, 9, new ResourceLocation[]{SAVE, SAVE_HOVER}, button -> {
            int totalPresets = BindsStorage.presets.size();
            int currentPreset = BindsEditor.getCurrentPreset();

            if (totalPresets <= 1) return;

            BindsStorage.removePreset(index);

            if (index < currentPreset) {
                parent.screen.selectPreset(currentPreset - 1);
            } else if (index == currentPreset) {
                parent.screen.selectPreset(Math.max(0, currentPreset - 1));
            } else {
                parent.screen.selectPreset(currentPreset);
            }

            parent.generatePresetsList();
        });

        this.saveBtn = GUIUtils.createTexturedBtn(x + 111, y + 7, 9, 9, new ResourceLocation[]{SAVE, SAVE_HOVER}, button -> {
            savePreset();
        });

        this.moveTop = GUIUtils.createTexturedBtn(x + 3, y + 2, 9, 9, new ResourceLocation[]{TOP, TOP_HOVER}, button -> {
            movePreset(-1);
        });

        this.moveBottom = GUIUtils.createTexturedBtn(x + 3, y + 12, 9, 9, new ResourceLocation[]{BOTTOM, BOTTOM_HOVER}, button -> {
            movePreset(1);
        });
    }

    public void movePreset(int dir) {
        int index = this.index;
        int last = BindsStorage.presets.size() - 1;
        if (dir == -1 && index == 0) return;
        if (dir == 1 && index == last) return;

        int scrollOffset = scrollableList.getScrollOffset();

        int newIndex = index + dir;
        int currentSelected = BindsEditor.getCurrentPreset();

        BindsStorage.swapPresets(index, newIndex);
        this.index = newIndex;

        if (currentSelected == index) {
            parent.screen.selectPreset(newIndex);
        } else if (currentSelected == newIndex) {
            parent.screen.selectPreset(index);
        }
        parent.generatePresetsList();
        scrollableList.setScrollOffset(scrollOffset);
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

    @Override
    //? <1.21.9 {
    public void onClick(double mouseX, double mouseY) {
    //? } else {
    // public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
    //? }
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
            int nameX = 15;
            int maxLength = (isDeleteConfirmation()) ? 12 : 13;
            if (BindsStorage.presets.size() == 1) {
                nameX = 3;
                maxLength = (isDeleteConfirmation()) ? 13 : 14;
            } else {
                GUIUtils.adaptiveDrawTexture(ctx, ARROW_DISABLED, getX() + 3, getY() + 2, 0, 0, 9, 19, 9, 19);
            }
            if (this.isHovered && parent.isOpen() && index != BindsEditor.getCurrentPreset()) GUIUtils.drawFill(c, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x0DFFFFFF);
            if (index == BindsEditor.getCurrentPreset()) GUIUtils.drawFill(c, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x1A4AFF00);
            GUIUtils.addText(c, TextUtils.literal(GUIUtils.truncateString(this.name, maxLength)), 0, getX() + nameX, getY() + 1 + (getHeight() / 2), "left", "center",0xFFFFFFFF, false);
            this.editBtn.render(c, mouseX, mouseY, delta);
            this.deleteBtn.render(c, mouseX, mouseY, delta);
            if (BindsStorage.presets.size() > 1) {
                if (index > 0) this.moveTop.render(c, mouseX, mouseY, delta);
                if (index < BindsStorage.presets.size() - 1) this.moveBottom.render(c, mouseX, mouseY, delta);
            }
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
    *///?} else if >=1.19.4 {
    /*@Override
    public void renderWidget(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    *///?} else {
    /*@Override
    public void renderButton(PoseStack context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    */
    //? }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (!parent.isOpen()) return false;

        if (isEditing) {
            if (newNameField.mouseClicked(event, bl) ||
                    saveBtn.mouseClicked(event, bl)) {
                return true;
            }
        } else {
            if (editBtn.mouseClicked(event, bl) ||
                    deleteBtn.mouseClicked(event, bl) ||
                    moveTop.mouseClicked(event, bl) ||
                    moveBottom.mouseClicked(event, bl)) {
                return true;
            }
            if (isDeleteConfirmation()) {
                if (deleteConfBtn.mouseClicked(event, bl)) return true;
            }
        }
        return super.mouseClicked(event, bl);
    }*/
    //? } else {
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!parent.isOpen()) return false;

            if (isEditing) {
                if (newNameField.mouseClicked(mouseX, mouseY, button) ||
                        saveBtn.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            } else {
                if (editBtn.mouseClicked(mouseX, mouseY, button) ||
                        deleteBtn.mouseClicked(mouseX, mouseY, button) ||
                        moveTop.mouseClicked(mouseX, mouseY, button) ||
                        moveBottom.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
                if (isDeleteConfirmation()) {
                    if (deleteConfBtn.mouseClicked(mouseX, mouseY, button)) return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
    //? }

        @Override
    //? if >=1.21.9 {
    /*public boolean keyPressed(KeyEvent event) {
        if (!parent.isOpen() || !isEditing) return false;
        return newNameField.keyPressed(event);
    }*/
    //? } else {
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!parent.isOpen() || !isEditing) return false;
            return newNameField.keyPressed(keyCode, scanCode, modifiers);
        }
    //? }

        @Override
    //? if >=1.21.9 {
    /*public boolean charTyped(CharacterEvent event) {
        if (!parent.isOpen() || !isEditing) return false;
        return newNameField.charTyped(event);
    }*/
    //? } else {
        public boolean charTyped(char codePoint, int modifiers) {
            if (!parent.isOpen() || !isEditing) return false;
            return newNameField.charTyped(codePoint, modifiers);
        }
    //? }

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
