package com.nitsha.binds.gui.screen;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.bind.BindExecutor;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.dto.preset.BindData;
import com.nitsha.binds.configs.dto.preset.PageData;
import com.nitsha.binds.configs.dto.preset.PresetData;
import com.nitsha.binds.gui.utils.AnimatedSprite;
import com.nitsha.binds.gui.widget.button.TexturedButton;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.button.ItemButton;
import com.nitsha.binds.multiversions.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.KeyMapping;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.ArrayList;
import java.util.List;

public class BindsGUI extends NitshaScreen {

    private final List<ItemButton> itemButtons = new ArrayList<>();
    private String displayText = TextUtils.translatable("nitsha.binds.list").getString();
    private int centerX;
    private int centerY;
    private final int MENU_WIDTH = 125;

    private String textLEFT, textRIGHT;
    private Component leftArrow, rightArrow;

    private AnimatedSprite catTail;
    private float eyeDx = 0;
    private float eyeDy = 0;

    public static boolean ignoreHoldToOpenOnce = false;

    public String activePresetId;
    public PresetData activePreset;
    public PageData activePage;
    public int activePresetIndex = 0;
    public int currentPageIndex = 0;

    public BindsGUI() {
        super();
    }

    protected void init() {
        super.init();
        itemButtons.clear();

        List<PresetData> allPresets = Storage.getSortedPresets();
        if (allPresets.isEmpty()) return;

        String savedId = Storage.options.lastPresetId;
        if (Storage.options.openLastPreset && !savedId.isEmpty() && Storage.PRESET_REGISTRY.containsKey(savedId)) {
            this.activePreset = Storage.PRESET_REGISTRY.get(savedId);
            this.activePresetIndex = allPresets.indexOf(this.activePreset);
        } else {
            this.activePreset = allPresets.get(0);
        }
        this.activePresetId = this.activePreset.id;

        if (Storage.options.openLastPage) {
            this.currentPageIndex = Math.min(Storage.options.lastPageIndex, this.activePreset.pages.size() - 1);
        }

        this.activePage = this.activePreset.pages.get(this.currentPageIndex);

        // Validate indices
        if (this.activePresetIndex >= Storage.PRESET_REGISTRY.size())
            this.activePresetIndex = 0;
        if (this.currentPageIndex >= this.activePreset.pages.size())
            this.currentPageIndex = 0;

        this.textLEFT = "[ "
                + RenderUtils.truncateString(
                        TextUtils.translatable(KeyBinds.PREV_PAGE.getTranslatedKeyMessage().getString()).getString(), 4)
                + " ]";
        this.textRIGHT = "[ "
                + RenderUtils.truncateString(
                        TextUtils.translatable(KeyBinds.NEXT_PAGE.getTranslatedKeyMessage().getString()).getString(), 4)
                + " ]";
        this.leftArrow = TextUtils.literal(textLEFT).withStyle(ChatFormatting.AQUA);
        this.rightArrow = TextUtils.literal(textRIGHT).withStyle(ChatFormatting.AQUA);
        this.centerX = (this.width - MENU_WIDTH) / 2;
        this.centerY = (this.height - 106) / 2 - 21;

        AbstractWidget configBtn = new TexturedButton(centerX + 57, centerY + 93, 10, 10, Textures.EDIT_BTN, Textures.EDIT_BTN_HOVER, b -> {
            NitshaCraft.setScreen(new BindsEditor(null));
        });

        this.addNWidget(configBtn);

        this.catTail = new AnimatedSprite(14, 12, Textures.CAT_SPRITE, 0, false, 0, 0, 490, 14, 60, 504, 12);
        if (Storage.options.easterEgg) {
            this.catTail.setLoop(true);
            this.catTail.setPosition(centerX + 101, centerY + 9);
            this.catTail.startAnimation(true);
        }

        generateButtons(centerX + 3, centerY + 27);
    }

    @Override
     public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        if (!this.checkForClose()) {
            RenderUtils.drawResizableBox(ctx, Textures.MENU_BG, centerX, centerY, MENU_WIDTH, 106, 3, 7);
            RenderUtils.drawResizableBox(ctx, Textures.MENU_HEADER, centerX, centerY, MENU_WIDTH, 22, 4, 9);

            RenderUtils.drawText(ctx, TextUtils.literal(displayText), 0, centerX + 5, centerY + 7);

            int totalPages = this.activePreset.pages.size();
            RenderUtils.drawText(ctx,
                    TextUtils.literal((this.currentPageIndex + 1) + "/" + totalPages).withStyle(ChatFormatting.GRAY), MENU_WIDTH,
                    centerX + MENU_WIDTH - 5, centerY + 7, "right", "top");

            int lAC = 0xFFFFFFFF;
            int rAC = 0xFFFFFFFF;
            if (isInside(mouseX, mouseY, centerX + 3, centerY + 92, 51, 12))
                lAC = 0xFFE7BC1C;
            if (isInside(mouseX, mouseY, centerX + 70, centerY + 92, 51, 12))
                rAC = 0xFFE7BC1C;

            RenderUtils.drawText(ctx, leftArrow, 125, centerX + 52, centerY + 94, "right", "top");
            RenderUtils.drawText(ctx, rightArrow, 125, centerX + 72, centerY + 94, "left", "top");

            RenderUtils.drawText(ctx, TextUtils.literal("⏴"), 125, centerX + 48 - font.width(textLEFT), centerY + 94,
                    "right", "top", lAC);
            RenderUtils.drawText(ctx, TextUtils.literal("⏵"), 125, centerX + 76 + font.width(textRIGHT), centerY + 94,
                    "left", "top", rAC);

            // Пресеты
            int lC = 0xFFFFFFFF;
            int rC = 0xFFFFFFFF;
            if (isInside(mouseX, mouseY, centerX, centerY - 20, 22, 18))
                lC = 0xFFE7BC1C;
            if (isInside(mouseX, mouseY, centerX + MENU_WIDTH - 22, centerY - 20, 22, 18))
                rC = 0xFFE7BC1C;

            RenderUtils.drawResizableBox(ctx, Textures.MENU_BG, centerX, centerY - 20, MENU_WIDTH, 18, 3, 7);
            RenderUtils.drawText(ctx, TextUtils.literal("⏴"), 0, centerX + 5, centerY - 15, lC);
            RenderUtils.drawText(ctx, TextUtils.literal("⏵"), MENU_WIDTH, centerX + MENU_WIDTH - 5, centerY - 15, "right",
                    "top", rC);

            String full = this.activePreset.name;

            if (full.length() < 15) {
                RenderUtils.drawText(ctx, TextUtils.literal(full).withStyle(ChatFormatting.AQUA), MENU_WIDTH, centerX,
                        centerY - 15, "center", "top");
            } else {
                RenderUtils.drawText(ctx,
                        TextUtils.literal(RenderUtils.truncateString(full, 15)).withStyle(ChatFormatting.AQUA), MENU_WIDTH,
                        centerX + 22, centerY - 15);
            }

            if (Storage.options.easterEgg) {
                RenderUtils.matricesUtil(ctx, 0, 0, 5, () -> {
                    RenderUtils.drawTexture(ctx, Textures.CAT, centerX + 77, centerY, 0, 0, 35, 26, 35, 26);
                    int eyeCenterX = centerX + 84;
                    int eyeCenterY = centerY + 10;
                    float maxOffsetX = 1.4f;
                    float maxOffsetY = 1.1f;

                    float targetDx = (mouseX - eyeCenterX) / 50f;
                    float targetDy = (mouseY - eyeCenterY) / 50f;

                    targetDx = Math.max(-maxOffsetX, Math.min(maxOffsetX, targetDx));
                    targetDy = Math.max(-maxOffsetY, Math.min(maxOffsetY, targetDy));

                    eyeDx += (targetDx - eyeDx) * 0.2f;
                    eyeDy += (targetDy - eyeDy) * 0.2f;

                    RenderUtils.matricesUtil(ctx, eyeDx, eyeDy, 0, () -> {
                        RenderUtils.drawTexture(ctx, Textures.CAT_EYES,
                                eyeCenterX, eyeCenterY,
                                0, 0, 8, 6, 8, 6);
                    });

                    catTail.render(ctx);
                });
            }

             super.onRender(ctx, mouseX, mouseY, delta);
        }
    }

    public void generateButtons(int startX, int startY) {
        for (ItemButton button : itemButtons) {
            removeNWidget(button);
        }

        itemButtons.clear();

        if (activePreset == null || currentPageIndex < 0 || currentPageIndex >= activePreset.pages.size()) {
            return;
        }

        PageData page = activePreset.pages.get(currentPageIndex);
        int slotCount = activePreset.columns * activePreset.rows;

        for (int slot = 0; slot < slotCount; slot++) {
            int currentX = startX + (slot % activePreset.columns) * 31;
            int currentY = startY + (slot / activePreset.columns) * 31;

            BindData foundBind = null;

            if (page.binds != null) {
                for (BindData bind : page.binds) {
                    if (bind.index == slot) {
                        foundBind = bind;
                        break;
                    }
                }
            }

            ItemButton button;

            if (foundBind != null) {
                BindData bind = foundBind;

                Runnable runBind = () -> {
                    BindExecutor.startBind(bind);

                    if (Storage.options.closeOnAction) {
                        BindsGUI.ignoreHoldToOpenOnce = true;
                        NitshaCraft.setScreen(null);
                    }
                };

                button = createBtn(RenderUtils.truncateString(bind.name, 12), ItemsMapper.getItemStack(bind.icon), currentX, currentY, runBind);
            } else {
                button = createBtn(TextUtils.translatable("nitsha.binds.empty").getString(), ItemsMapper.getItemStack("minecraft:structure_void"), currentX, currentY, () -> {});
            }

            itemButtons.add(button);
            addNWidget(button);
        }
    }

    private ItemButton createBtn(String text, ItemStack icon, int x, int y, Runnable onClick) {
        return new ItemButton(x, y, icon, Textures.ITEMS_SELECTOR, "", onClick) {
            @Override
            public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
                super.onRender(ctx, mouseX, mouseY, delta);

                if (isHovered()) {
                    displayText = text;
                }
            }
        };
    }
    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        KeyMapping.set(KeyBinds.BINDS.getDefaultKey(), false);
    }

    private boolean checkForClose() {
        if (!Storage.options.holdToOpen)
            return false;
        if (ignoreHoldToOpenOnce) {
            ignoreHoldToOpenOnce = false;
            return false;
        }

        if (!NitshaInput.isKeyDown(KeyBinds.BINDS)) {
            NitshaCraft.setScreen(null);
            return true;
        }
        return false;
    }

    private void updatePage(int dir) {
        if (this.activePreset == null) return;

        int totalPages = this.activePreset.pages.size();
        if (totalPages == 0) return;

        if (dir == -1 && this.currentPageIndex == 0) {
            this.currentPageIndex = totalPages - 1;
        } else {
            this.currentPageIndex += dir;
            if (this.currentPageIndex >= totalPages) {
                this.currentPageIndex = 0;
            }
        }

        Storage.options.lastPresetId = this.activePresetId;
        Storage.options.lastPageIndex = this.currentPageIndex;
        Storage.saveModOptions();

        generateButtons(centerX + 3, centerY + 27);
    }

    private void updatePreset(int dir) {
        List<PresetData> sortedPresets = Storage.getSortedPresets();
        int totalPresets = sortedPresets.size();
        if (totalPresets == 0) return;

        int currentIndex = sortedPresets.indexOf(this.activePreset);
        if (currentIndex == -1) currentIndex = 0;

        if (dir == -1 && currentIndex == 0) {
            currentIndex = totalPresets - 1;
        } else {
            currentIndex += dir;
            if (currentIndex >= totalPresets) {
                currentIndex = 0;
            }
        }

        this.activePreset = sortedPresets.get(currentIndex);
        this.activePresetId = this.activePreset.id;
        this.activePresetIndex = currentIndex;

        this.currentPageIndex = 0;

        Storage.options.lastPresetId = this.activePresetId;
        Storage.options.lastPageIndex = this.currentPageIndex;
        Storage.saveModOptions();

        generateButtons(centerX + 3, centerY + 27);
    }

    private void setPreset(int index) {
        List<PresetData> sortedPresets = Storage.getSortedPresets();

        if (index >= 0 && index < sortedPresets.size()) {
            this.activePreset = sortedPresets.get(index);
            this.activePresetId = this.activePreset.id;

            this.activePresetIndex = index;
            this.currentPageIndex = 0;

            Storage.options.lastPresetId = this.activePresetId;
            Storage.options.lastPageIndex = this.currentPageIndex;
            Storage.saveModOptions();
            generateButtons(centerX + 3, centerY + 27);
        }
    }

    public static boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private boolean handleClick(double mouseX, double mouseY, int x, int y, int width, int height, Runnable action) {
        if (isInside(mouseX, mouseY, x, y, width, height)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            action.run();
            return true;
        }
        return false;
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (handleClick(mouseX, mouseY, centerX + 3, centerY + 92, 51, 12, () -> updatePage(-1))) return true;
        if (handleClick(mouseX, mouseY, centerX + 70, centerY + 92, 51, 12, () -> updatePage(1))) return true;
        if (handleClick(mouseX, mouseY, centerX, centerY - 20, 22, 18, () -> updatePreset(-1))) return true;
        if (handleClick(mouseX, mouseY, centerX + MENU_WIDTH - 22, centerY - 20, 22, 18, () -> updatePreset(1))) return true;

        if (NitshaInput.matchesMouse(KeyBinds.PREV_PAGE, button)) {
            updatePage(-1);
            return true;
        }

        if (NitshaInput.matchesMouse(KeyBinds.NEXT_PAGE, button)) {
            updatePage(1);
            return true;
        }

        return super.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount == 0.0) {
            return super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        int direction = verticalAmount > 0 ? -1 : 1;

        if (isInside(mouseX, mouseY, centerX, centerY - 20, 125, 18)) {
            updatePreset(direction);
        } else if (isInside(mouseX, mouseY, centerX, centerY, MENU_WIDTH, 106)) {
            updatePage(direction);
        } else {
            return super.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));

        return true;
    }

    @Override
    protected boolean onKeyPressed(int keyCode, int nativeCode, int modifiers) {
        if ((NitshaInput.matchesKey(KeyBinds.BINDS, keyCode, nativeCode) && !Storage.options.holdToOpen && !ignoreHoldToOpenOnce) || keyCode == InputConstants.KEY_ESCAPE) {
            ignoreHoldToOpenOnce = false;
            NitshaCraft.setScreen(null);
            return true;
        }

        if (NitshaInput.matchesKey(KeyBinds.PREV_PAGE, keyCode, nativeCode)) {
            updatePage(-1);
            return true;
        }

        if (NitshaInput.matchesKey(KeyBinds.NEXT_PAGE, keyCode, nativeCode)) {
            updatePage(1);
            return true;
        }

        if (keyCode >= InputConstants.KEY_1 && keyCode <= InputConstants.KEY_9) {
            int index = keyCode - InputConstants.KEY_1;
            setPreset(index);
            return true;
        }

        return super.onKeyPressed(keyCode, nativeCode, modifiers);
    }

    @Override
    protected boolean onKeyReleased(InputConstants.Key key, int modifiers) {
        if (Storage.options.keepMovement) {
            KeyMapping.set(key, false);
        }

        return super.onKeyReleased(key, modifiers);
    }
}