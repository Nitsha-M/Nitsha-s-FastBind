package com.nitsha.binds.gui.panels;

import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.preset.PresetData;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.widget.button.TexturedButton;
import com.nitsha.binds.gui.widget.list.PresetListItem;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import com.nitsha.binds.gui.widget.window.AnimatedWindow;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.button.SmallTextButton;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PresetSelector extends AnimatedWindow {
    private static final int HEADER_HEIGHT = 19;
    private static final int OPEN_HEIGHT = 160;
    private static final int ROW_HEIGHT = 22;
    private static final int BOTTOM_PADDING = 7;
    private static final int LIST_Y = 16;
    private static final int ADD_BUTTON_HEIGHT = 9;
    private static final int ADD_BUTTON_Y = OPEN_HEIGHT - BOTTOM_PADDING - ADD_BUTTON_HEIGHT;

    public final BindsEditor screen;

    private final List<PresetListItem> items = new ArrayList<>();
    private ScrollableWindow presetsList;
    private SmallTextButton addButton;
    private boolean isOpen;

    public PresetSelector(BindsEditor screen, int x, int y, float width, int height, Identifier bottomTexture, Identifier topTexture, int delay) {
        super(x, y, width, HEADER_HEIGHT, bottomTexture, topTexture, delay);
        this.screen = screen;
        initUI();
        open(() -> {});
    }

    private void initUI() {
        addDrawElement((ctx, mouseX, mouseY) -> {
            RenderUtils.drawText(ctx, TextUtils.small(TextUtils.literal(RenderUtils.truncateString(BindsEditor.getPresetName(), 13))), 0, 5, 4, "left", "top", 0xFFFFFFFF, false);
            if (isOpen) {
                RenderUtils.drawFill(ctx, 4, 15, getWidth() - 4, 16, 0xFF555555);
            }
        });

        addElement(new TexturedButton(getWidth() - 14, 4, 9, 9, Textures.SELECT_PRESET, Textures.SELECT_PRESET_HOVER, button -> openSelector(!isOpen)));
        TexturedButton previousButton = new TexturedButton(getWidth() - 36, 4, 9, 9, Textures.ICON_SMALL_TOP, Textures.ICON_SMALL_TOP_HOVER, button -> {
            saveAll();
            screen.setNewPreset(-1);
        });
        addElement(previousButton);
        TexturedButton nextButton = new TexturedButton(getWidth() - 25, 4, 9, 9, Textures.ICON_SMALL_BOTTOM, Textures.ICON_SMALL_BOTTOM_HOVER, button -> {
            saveAll();
            screen.setNewPreset(1);
        });
        addElement(nextButton);

        presetsList = new ScrollableWindow(2, LIST_Y, getWidth() - 4, ADD_BUTTON_Y - LIST_Y - 3, false);
        presetsList.setVisible(false);
        addElement(presetsList);
        generatePresetsList();

        addButton = new SmallTextButton(TextUtils.translatable("nitsha.binds.addNew"), 4, ADD_BUTTON_Y, 0xFF4d9109, getWidth() - 8, "left", Textures.ICON_ADD_NEW, () -> {
            saveAll();
            Storage.createNewPreset();
            generatePresetsList();
        });
        addButton.visible = false;
        addElement(addButton);

    }

    public void generatePresetsList() {
        saveAll();
        int previousScroll = presetsList.getScrollOffset();
        presetsList.clearChildren();
        items.clear();

        List<PresetData> presets = Storage.getSortedPresets();
        int rowY = 0;

        for (int i = 0; i < presets.size(); i++) {
            PresetListItem item = new PresetListItem(this, presets.get(i), 0, rowY, presetsList.getWidth(), ROW_HEIGHT, i);
            presetsList.addElement(item);
            items.add(item);
            rowY += ROW_HEIGHT;
        }

        presetsList.setScrollableArea(rowY);
        presetsList.setScrollOffset(previousScroll);
    }

    public List<PresetListItem> getItems() {
        return items;
    }

    public void saveAll() {
        for (PresetListItem item : items) {
            if (item.isEditing()) {
                item.savePreset();
            }
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isMouseInside(mouseX, mouseY);
    }

    public boolean isMouseInsideArea(double mouseX, double mouseY) {
        return isVisible() && mouseX >= getContentX() && mouseX < getContentX() + getWidth() && mouseY >= getContentY() && mouseY < getContentY() + Math.min(HEADER_HEIGHT, getHeight());
    }

    public void openSelector(boolean open) {
        if (isOpen == open) return;

        if (!open) {
            saveAll();
        }

        isOpen = open;
        setHeight(open ? OPEN_HEIGHT : HEADER_HEIGHT);
        presetsList.setVisible(open);
        addButton.visible = open;
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void renderWindow(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.customScissor(ctx, getContentX(), getContentY(), getWidth(), getHeight(), () -> {
            super.renderWindow(ctx, mouseX, mouseY, delta);
        });
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseInside(mouseX, mouseY)) return false;

        super.onMouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!canClick() || !isMouseInside(mouseX, mouseY)) return false;

        if (isMouseInsideArea(mouseX, mouseY)) {
            if (verticalAmount == 0.0) return false;

            List<PresetData> presets = Storage.getSortedPresets();

            if (presets.isEmpty()) return false;

            saveAll();
            int currentIndex = Math.max(0, presets.indexOf(BindsEditor.activePreset));
            int direction = verticalAmount > 0 ? -1 : 1;
            int nextIndex = Math.floorMod(currentIndex + direction, presets.size());
            screen.selectPreset(presets.get(nextIndex).id);
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }

        return isOpen && super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean onKeyPressed(int keyCode, int nativeCode, int modifiers) {
        return isOpen && super.onKeyPressed(keyCode, nativeCode, modifiers);
    }

    @Override
    public boolean onCharTyped(int codePoint) {
        return isOpen && super.onCharTyped(codePoint);
    }
}
