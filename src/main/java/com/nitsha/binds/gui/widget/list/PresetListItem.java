package com.nitsha.binds.gui.widget.list;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.preset.PresetData;
import com.nitsha.binds.gui.panels.PresetSelector;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.widget.TextField;
import com.nitsha.binds.gui.widget.button.TexturedButton;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.multiversions.NitshaAbstractWidget;
import com.nitsha.binds.multiversions.NitshaInput;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.List;

public class PresetListItem extends NitshaAbstractWidget {

    private final PresetSelector parent;
    private final TextField newNameField;

    private final TexturedButton editBtn;
    private final TexturedButton deleteBtn;
    private final TexturedButton saveBtn;
    private final TexturedButton deleteConfBtn;
    private final TexturedButton moveTop;
    private final TexturedButton moveBottom;

    private boolean isEditing = false;
    private String name;
    private final String presetId;
    private final int index;

    private long deleteConfirmationTime = 0;

    public PresetListItem(PresetSelector parent, PresetData preset, int x, int y, int width, int height, int index) {
        super(x, y, width, height, TextUtils.empty());
        this.parent = parent;
        this.presetId = preset.id;
        this.name = preset.name;
        this.index = index;

        this.newNameField = new TextField(Minecraft.getInstance().font, x + 3, y + 3, width - 20,height - 5, 40, name,"Preset name");
        this.newNameField.setEnterEvent(this::savePreset);
        this.newNameField.setEscapeEvent(this::stopEditing);
        this.newNameField.setAnimatedPlaceholder(false);
        this.editBtn = new TexturedButton(x + 111, y + 7, 9, 9, Textures.EDIT, Textures.EDIT_HOVER, button -> {
            for (PresetListItem item : parent.getItems()) {
                if (item.isEditing()) {
                    item.stopEditing();
                }
            }
            isEditing = true;
            this.newNameField.setFocus();
        });

        this.deleteBtn = new TexturedButton(x + 101, y + 7, 9, 9, Textures.DELETE, Textures.DELETE_HOVER, button -> {
            deleteConfirmationTime = System.currentTimeMillis();
        });

        this.deleteConfBtn = new TexturedButton(x + 91, y + 7, 9, 9,Textures.SAVE, Textures.SAVE_HOVER, button -> {
            String selectedId = BindsEditor.activePresetId;

            if (Storage.deletePreset(presetId)) {
                parent.screen.selectPreset(selectedId);
                parent.generatePresetsList();
            }
        });

        this.saveBtn = new TexturedButton(x + 111, y + 7, 9, 9, Textures.SAVE, Textures.SAVE_HOVER, button -> {
            savePreset();
        });

        this.moveTop = new TexturedButton(x + 3, y + 2, 9, 9, Textures.ICON_SMALL_TOP, Textures.ICON_SMALL_TOP_HOVER, button -> {
            movePreset(-1);
        });

        this.moveBottom = new TexturedButton(x + 3, y + 12, 9, 9, Textures.ICON_SMALL_BOTTOM, Textures.ICON_SMALL_BOTTOM_HOVER, button -> {
            movePreset(1);
        });
        setWidth(width);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        newNameField.setWidth(width - 20);
        editBtn.setX(getX() + width - 12);
        saveBtn.setX(getX() + width - 12);
        deleteBtn.setX(getX() + width - 22);
        deleteConfBtn.setX(getX() + width - 32);
    }

    public void movePreset(int direction) {
        Storage.movePreset(presetId, direction);
        parent.generatePresetsList();
    }

    public void stopEditing() {
        isEditing = false;
        this.newNameField.setText(this.name);

        if (TextField.getFocusedField() == newNameField) {
            TextField.setFocusedField(null);
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
        String newName = getName.isBlank() ? TextUtils.translatable("nitsha.binds.default.newPreset").getString() : getName;
        PresetData pd = Storage.PRESET_REGISTRY.get(presetId);
        if (pd == null) {
            return;
        }
        pd.name = newName;
        Storage.savePreset(pd, pd.id);
        this.newNameField.setText(newName);
        this.name = newName;

        if (TextField.getFocusedField() == newNameField) {
            TextField.setFocusedField(null);
        }
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.onRender(ctx, mouseX, mouseY, delta);

        if (index > 0) RenderUtils.drawFill(ctx, getX() + 3, getY(), getX() + getWidth() - 3, getY() + 1, 0xFF555555);
        if (isEditing) {
            this.newNameField.onRender(ctx, mouseX, mouseY, delta);
            this.saveBtn.onRender(ctx, mouseX, mouseY, delta);
        } else {
            int nameX = 15;
            int maxLength = (isDeleteConfirmation()) ? 12 : 13;
            if (Storage.getSortedPresets().size() == 1) {
                nameX = 3;
                maxLength = (isDeleteConfirmation()) ? 13 : 14;
            } else {
                RenderUtils.drawTexture(ctx, Textures.ARROW_DISABLED, getX() + 3, getY() + 2, 0, 0, 9, 19, 9, 19);
            }
            int currentPreset = Storage.getSortedPresets().indexOf(BindsEditor.activePreset);
            if (this.isHovered && parent.isOpen() && index != currentPreset) RenderUtils.drawFill(ctx, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x0DFFFFFF);
            if (index == currentPreset) RenderUtils.drawFill(ctx, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x1A4AFF00);
            RenderUtils.drawText(ctx, TextUtils.literal(RenderUtils.truncateString(this.name, maxLength)), 0, getX() + nameX, getY() + 1 + (getHeight() / 2), "left", "center",0xFFFFFFFF, false);
            this.editBtn.onRender(ctx, mouseX, mouseY, delta);

            boolean canDelete = Storage.PRESET_REGISTRY.size() > 1;
            this.deleteBtn.visible = canDelete;
            this.deleteConfBtn.visible = canDelete;

            if (canDelete) {
                this.deleteBtn.onRender(ctx, mouseX, mouseY, delta);

                if (isDeleteConfirmation()) {
                    this.deleteConfBtn.onRender(ctx, mouseX, mouseY, delta);
                }
            }

            if (Storage.getSortedPresets().size() > 1) {
                if (index > 0) this.moveTop.onRender(ctx, mouseX, mouseY, delta);
                if (index < Storage.getSortedPresets().size() - 1) this.moveBottom.onRender(ctx, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || !parent.isOpen() || button != InputConstants.MOUSE_BUTTON_LEFT || !isMouseOver(mouseX, mouseY)) {
            return false;
        }

        if (isEditing) {
            return NitshaInput.mouseClicked(List.of(newNameField, saveBtn), mouseX, mouseY, button);
        }

        if (NitshaInput.mouseClicked(List.of(editBtn, deleteBtn, moveTop, moveBottom), mouseX, mouseY, button)) {
            return true;
        }

        if (isDeleteConfirmation() && NitshaInput.mouseClicked(List.of(deleteConfBtn), mouseX, mouseY, button)) {
            return true;
        }

        playDownSound(Minecraft.getInstance().getSoundManager());
        parent.screen.selectPreset(presetId);
        parent.openSelector(false);
        return true;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int nativeCode, int modifiers) {
        if (!parent.isOpen() || !isEditing) {
            return false;
        }

        return NitshaInput.keyPressed(List.of(newNameField), keyCode, nativeCode, modifiers);
    }

    @Override
    public boolean onCharTyped(int codePoint) {
        if (!parent.isOpen() || !isEditing) {
            return false;
        }

        return NitshaInput.charTyped(List.of(newNameField), codePoint);
    }
}
