package com.nitsha.binds.gui.panels;

import com.nitsha.binds.Main;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.preset.PresetData;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.widget.button.TexturedButton;
import com.nitsha.binds.gui.widget.list.PresetListItem;
import com.nitsha.binds.gui.widget.window.ScrollableWindow;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.button.SmallTextButton;
import com.nitsha.binds.multiversions.NitshaContainer;
import com.nitsha.binds.multiversions.RenderUtils;
import com.nitsha.binds.multiversions.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class PresetSelector extends NitshaContainer {
    private static final int HEADER_HEIGHT = 17;
    private static final int OPEN_HEIGHT = 160;
    private static final int ROW_HEIGHT = 22;

    public final BindsEditor screen;

    private final List<PresetListItem> items = new ArrayList<>();
    private final float speed = Main.GLOBAL_ANIMATION_SPEED - 0.1F;
    private ScrollableWindow presetsList;
    private SmallTextButton addButton;
    private boolean isOpen;
    private float targetWidth;
    private float targetHeight;
    private float yOffset = 80.0F;
    private long delayMs;
    private long lastUpdateTime;

    public PresetSelector(BindsEditor screen, int x, int y, float width, int height, int delay) {
        super(x, y, width, height);
        this.screen = screen;
        targetWidth = width;
        targetHeight = height;
        delayMs = Math.max(0, delay);
        initUI();
        open();
    }

    private void initUI() {
        addDrawElement((ctx, mouseX, mouseY) -> {
            RenderUtils.drawText(ctx, TextUtils.literal(RenderUtils.truncateString(BindsEditor.getPresetName(), 13)), 0, 5, 5, "left", "top", 0xFFFFFFFF, false);
        });

        addElement(new TexturedButton(113, 4, 9, 9, Textures.SELECT_PRESET, Textures.SELECT_PRESET_HOVER, button -> {
            openSelector(!isOpen);
        }));
        addElement(new TexturedButton(91, 4, 9, 9, Textures.ICON_SMALL_TOP, Textures.ICON_SMALL_TOP_HOVER, button -> {
            saveAll();
            screen.setNewPreset(-1);
        }));
        addElement(new TexturedButton(102, 4, 9, 9, Textures.ICON_SMALL_BOTTOM, Textures.ICON_SMALL_BOTTOM_HOVER, button -> {
            saveAll();
            screen.setNewPreset(1);
        }));

        presetsList = new ScrollableWindow(2, 15, getWidth() - 4, 130, false);
        presetsList.setVisible(false);
        addElement(presetsList);
        generatePresetsList();

        addButton = new SmallTextButton(TextUtils.translatable("nitsha.binds.addNew"), 4, 147, 0xFF4d9109, getWidth() - 8, "left", Textures.ICON_ADD_NEW, () -> {
            saveAll();
            Storage.createNewPreset();
            generatePresetsList();
        });
        addButton.visible = false;
        addElement(addButton);

        addDrawElement((ctx, mouseX, mouseY) -> {
            if (isOpen) {
                RenderUtils.drawFill(ctx, 5, 15, getWidth() - 5, 16, 0xFF555555);
            }
        }, 1);
    }

    public void generatePresetsList() {
        saveAll();
        int previousScroll = presetsList.getScrollOffset();
        presetsList.clearChildren();
        items.clear();

        List<PresetData> presets = Storage.getSortedPresets();
        int rowY = 0;

        for (int i = 0; i < presets.size(); i++) {
            PresetListItem item = new PresetListItem(this, presets.get(i), 0, rowY, getWidth() - 4, ROW_HEIGHT, i);
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
    public void setWidth(float width) {
        targetWidth = width;
    }

    @Override
    public void setHeight(float height) {
        targetHeight = height;
    }

    @Override
    protected int getContentY() {
        return getY() - Math.round(yOffset);
    }

    @Override
    protected boolean canClick() {
        return isVisible() && delayMs == 0;
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

    public void open() {
        setVisible(true);
        lastUpdateTime = System.currentTimeMillis();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void tick() {
        long now = System.currentTimeMillis();

        if (lastUpdateTime == 0) {
            lastUpdateTime = now;
            return;
        }

        long elapsed = Math.max(0, now - lastUpdateTime);
        lastUpdateTime = now;

        if (delayMs > 0) {
            long delayed = Math.min(delayMs, elapsed);
            delayMs -= delayed;
            elapsed -= delayed;
        }

        if (elapsed == 0) return;

        float factor = 1.0F - (float) Math.pow(1.0F - speed, elapsed / 16.666F);
        float sizeFactor = 1.0F - (1.0F - factor) * (1.0F - factor);
        width = Mth.lerp(sizeFactor, width, targetWidth);
        height = Mth.lerp(sizeFactor, height, targetHeight);
        yOffset = Mth.lerp(factor, yOffset, 0.0F);

        if (Math.abs(width - targetWidth) < 0.1F) {
            width = targetWidth;
        }

        if (Math.abs(height - targetHeight) < 0.1F) {
            height = targetHeight;
        }

        if (Math.abs(yOffset) < 0.001F) {
            yOffset = 0.0F;
        }
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        tick();

        if (!isVisible() || delayMs > 0) return;

        RenderUtils.customScissor(ctx, getContentX(), getContentY(), getWidth(), getHeight(), () -> {
            RenderUtils.matricesUtil(ctx, 0, 0, 499, () -> {
                super.onRender(ctx, mouseX, mouseY, delta);
            });
        });
    }

    @Override
    protected void renderBackground(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        RenderUtils.drawResizableBox(ctx, Textures.DARK_MENU_BG, 0, 0, getWidth(), getHeight(), 3, 7);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseInside(mouseX, mouseY)) return false;

        return super.onMouseClicked(mouseX, mouseY, button);
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
