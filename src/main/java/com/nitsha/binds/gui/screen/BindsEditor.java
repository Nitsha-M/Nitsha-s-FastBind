package com.nitsha.binds.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.configs.dto.preset.BindData;
import com.nitsha.binds.configs.dto.preset.PresetData;
import com.nitsha.binds.configs.dto.preset.PageData;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.*;
import com.nitsha.binds.gui.modals.SelectIcon;
import com.nitsha.binds.gui.modals.SelectKeyEvent;
import com.nitsha.binds.gui.modals.SelectSound;
import com.nitsha.binds.gui.panels.*;
import com.nitsha.binds.gui.utils.Textures;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.gui.widget.window.ModalWindow;
import com.nitsha.binds.gui.panels.AdvancedOptions;
import com.nitsha.binds.gui.widget.button.MainKeybindSelector;
import com.nitsha.binds.gui.widget.window.TextFormattingWindow;
import com.nitsha.binds.multiversions.*;
import com.nitsha.binds.utils.AudioPlayer;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import java.util.ArrayList;
import java.util.List;
import com.nitsha.binds.configs.dto.preset.ActionData;

public class BindsEditor extends NitshaScreen {
    
    private final int TEXTURE_WIDTH = 141;
    private final int TEXTURE_HEIGHT = 190;

    private int centerX;
    private int centerY;

    public static String activePresetId;
    public static PresetData activePreset;
    public static int currentPage = 0;
    public static int activeBind = 0;
    private static int activeBindPage;
    public static String editIconBtnString = "minecraft:structure_void";

    public BindData copied = new BindData();

    private BasicOptionsWindow window_BasicOptions;
    private BindsList window_BindsList;
    private AdvancedOptions window_AdvancedOptions;
    private PresetSelector window_PresetSelector;
    private PresetOptions window_PresetOptions;
    private TextFormattingWindow window_TextFormatting;
    private GuiEventListener mouseCapture;
    private int mouseCaptureButton = -1;

    private SelectKeyEvent modal_SelectKeyEvent;
    private SelectSound modal_SelectSound;
    private SelectIcon modal_SelectIcon;

    private final Screen parent;

    private final List<ModalWindow> modalWindows = new ArrayList<>();

    public BindsEditor(Screen parent) {
        super();
        this.parent = parent;
    }

    private void addModal(ModalWindow modal) {
        addNWidget(modal);
        modalWindows.add(modal);
    }

    protected void init() {
        super.init();
        //? if >=1.17 {
        this.clearWidgets();
        //?} else {
        /*this.children().clear();*/
        //? }
        modalWindows.clear();
        mouseCapture = null;
        mouseCaptureButton = -1;
        this.centerX = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.centerY = (this.height - TEXTURE_HEIGHT) / 2;

        List<PresetData> allPresets = Storage.getSortedPresets();
        if (allPresets != null && !allPresets.isEmpty()) {
            String savedId = Storage.options.lastPresetId;
            if (Storage.options.openLastPreset && savedId != null && !savedId.isEmpty() && Storage.PRESET_REGISTRY.containsKey(savedId)) {
                activePreset = Storage.PRESET_REGISTRY.get(savedId);
            } else {
                activePreset = allPresets.get(0);
            }
            activePresetId = activePreset.id;

            if (Storage.options.openLastPage && activePreset.pages.size() > 0) {
                currentPage = Math.max(0, Math.min(Storage.options.lastPageIndex, activePreset.pages.size() - 1));
            } else {
                currentPage = 0;
            }
        }

        setActiveBind(getFirstBindOfPage(currentPage));

        // Basic options (bind name, icon)
        window_BasicOptions = new BasicOptionsWindow(this, centerX - 100, centerY, TEXTURE_WIDTH, TEXTURE_HEIGHT, Textures.BACKGROUND, Textures.BACKGROUND_FLAT, 0);
        this.addNWidget(window_BasicOptions);

        // Binds list (gray window with all buttons)
        window_BindsList = new BindsList(this, centerX + 4 - 100, centerY - 20, 133, 100, Textures.BACKGROUND_DARK, Textures.BACKGROUND_DARK_FLAT, 140);
        this.addNWidget(window_BindsList);

        // Preset options
        window_PresetOptions = new PresetOptions(this, centerX + 4 - 100, centerY + 103, 133, 30, Textures.BACKGROUND_DARK, Textures.BACKGROUND_DARK_FLAT, 250);
        this.addNWidget(window_PresetOptions);

        // Preset selector and editor
        window_PresetSelector = new PresetSelector(this, centerX + 4 - 100, centerY + 82, 133, 19, Textures.BACKGROUND_DARK, Textures.BACKGROUND_DARK_FLAT, 350);
        this.addNWidget(window_PresetSelector);

        // Advanced options (icon, actions, mod options)
        window_AdvancedOptions = new AdvancedOptions(this, centerX + 61, (Storage.options.fullHeightEditor) ? 26 : centerY, 180, (Storage.options.fullHeightEditor) ? this.height - 36 : TEXTURE_HEIGHT,
                Textures.BACKGROUND, Textures.BACKGROUND_FLAT, 140);
        this.addNWidget(window_AdvancedOptions);

        // Select key event
        modal_SelectKeyEvent = new SelectKeyEvent(this, centerX, centerY, 180, TEXTURE_HEIGHT,
                Textures.BACKGROUND, Textures.BACKGROUND_FLAT);
        addModal(modal_SelectKeyEvent);

        // Select icon
        modal_SelectIcon = new SelectIcon(this, centerX, centerY, 180, TEXTURE_HEIGHT,
                Textures.BACKGROUND, Textures.BACKGROUND_FLAT);
        addModal(modal_SelectIcon);

        // Select icon
        modal_SelectSound = new SelectSound(this, centerX, centerY, 180, TEXTURE_HEIGHT,
                Textures.BACKGROUND, Textures.BACKGROUND_FLAT);
        addModal(modal_SelectSound);

        selectBind();
        window_BindsList.updateSelected(ItemsMapper.getItemStack(getCBind().icon));

        EventBus.on("selectKeyEvent.open", (Object[] data) -> {
            modal_SelectKeyEvent.open(() -> {});
        });

        EventBus.on("selectIcon.open", (Object[] data) -> {
            modal_SelectIcon.open(() -> {});
        });

        EventBus.on("selectSound.open", (Object[] data) -> {
            modal_SelectSound.open(() -> {});
        });


        window_TextFormatting = new TextFormattingWindow(0, 0);
        addNWidget(window_TextFormatting);
    }

    public BasicOptionsWindow getBasicOptionsWindow() {
        return this.window_BasicOptions;
    }

    public BindsList getBindsListWindow() {
        return this.window_BindsList;
    }

    public AdvancedOptions getAdvancedOptionsWindow() {
        return this.window_AdvancedOptions;
    }

    public PresetSelector getPresetSelectorWindow() {
        return this.window_PresetSelector;
    }

    public PresetOptions getPresetOptionsWindow() {
        return this.window_PresetOptions;
    }

    public static String getPresetName() {
        return activePreset != null ? activePreset.name : "";
    }

    public static int getCurrentPage() {
        return currentPage;
    }

    public static int getActiveBind() {
        return activeBind;
    }

    public static void setActiveBind(int aB) {
        activeBind = aB;
        activeBindPage = currentPage;
    }

    public static int getPageOfActiveBind() {
        return activeBindPage;
    }

    public static boolean isActiveBindOnPage(int page) {
        return getPageOfActiveBind() == page;
    }

    public static int getFirstBindOfPage(int page) {
        // The stored page offset stays fixed when the visible grid changes.
        return page * 8;
    }

    public void setPresetGridSize(int columns, int rows) {
        columns = Math.max(PresetData.MIN_COLUMNS, Math.min(PresetData.MAX_COLUMNS, columns));
        rows = Math.max(PresetData.MIN_ROWS, Math.min(PresetData.MAX_ROWS, rows));
        if (activePreset == null || (activePreset.columns == columns && activePreset.rows == rows)) return;

        saveBind();
        int selectedSlot = isActiveBindOnPage(currentPage) ? activeBind - getFirstBindOfPage(currentPage) : 0;
        activePreset.columns = columns;
        activePreset.rows = rows;
        Storage.savePreset(activePreset, activePresetId);

        if (selectedSlot >= columns * rows || selectedSlot < 0) {
            setActiveBind(getFirstBindOfPage(currentPage));
            selectBind();
        }

        window_BindsList.generateButtons(7, 31);
    }

    public void setNewPage(int dir) {
        if (activePreset == null) return;
        int totalPages = activePreset.pages.size();
        if (totalPages == 0) return;
        saveBind();
        if (dir == -1 && currentPage == 0)
            currentPage = totalPages;
        currentPage += dir;
        if (currentPage >= totalPages)
            currentPage = 0;
            
        Storage.options.lastPresetId = activePresetId;
        Storage.options.lastPageIndex = currentPage;
        Storage.saveModOptions();
        setActiveBind(getFirstBindOfPage(currentPage));
        window_BindsList.generateButtons(7, 31);
        selectBind();
    }

    public void selectPage(int i) {
        currentPage = i;
        Storage.options.lastPageIndex = currentPage;
        Storage.saveModOptions();
    }

    public void setNewPreset(int dir) {
        List<PresetData> sortedPresets = Storage.getSortedPresets();
        if (sortedPresets.isEmpty()) return;
        
        int currentIndex = sortedPresets.indexOf(activePreset);
        if (currentIndex == -1) currentIndex = 0;
        
        int totalPresets = sortedPresets.size();
        if (dir == -1 && currentIndex == 0)
            currentIndex = totalPresets;
        currentIndex += dir;
        if (currentIndex >= totalPresets)
            currentIndex = 0;
            
        selectPreset(currentIndex);
    }

    // Functions
    public void selectPreset(String id) {
        List<PresetData> presets = Storage.getSortedPresets();

        for (int i = 0; i < presets.size(); i++) {
            if (presets.get(i).id.equals(id)) {
                selectPreset(i);
                return;
            }
        }

        if (!presets.isEmpty()) {
            selectPreset(0);
        }
    }

    public void selectPreset(int i) {
        List<PresetData> sortedPresets = Storage.getSortedPresets();
        if (i >= 0 && i < sortedPresets.size()) {
            activePreset = sortedPresets.get(i);
            activePresetId = activePreset.id;
            currentPage = 0;
            setActiveBind(0);

            Storage.options.lastPresetId = activePresetId;
            Storage.options.lastPageIndex = currentPage;
            Storage.saveModOptions();

            window_BindsList.generateButtons(7, 31);
            window_PresetOptions.syncPreset();
            selectBind();
        }
    }

    public static BindData getCBind() {
        if (activePreset != null && currentPage >= 0 && currentPage < activePreset.pages.size()) {
            PageData page = activePreset.pages.get(currentPage);
            if (page.binds != null) {
                for (BindData b : page.binds) {
                    if (b.index == activeBind) return b;
                }
            }
        }
        BindData dummy = new BindData();
        dummy.index = activeBind;
        return dummy;
    }

    public static String getCurrentPresetId() {
        return activePresetId;
    }

    public void saveBind() {
        List<ActionData> actions = window_AdvancedOptions.getActionsTab().getAllActions();
        boolean hasActions = actions != null && !actions.isEmpty();
        String currentName = window_BasicOptions.getBindName().getText();
        boolean hasName = currentName != null && !currentName.trim().isEmpty();
        int keyCode = window_AdvancedOptions.getActionsTab().keybind.getKeyCode();
        boolean hasKey = keyCode != 0 && keyCode != -1;

        String localizedUntitled = TextUtils.translatable("nitsha.binds.untitled").getString();
        if (hasName && (currentName.equals(localizedUntitled) || currentName.equals("Untitled") || currentName.equals("Без названия")) && !hasActions && !hasKey) {
            hasName = false;
        }

        if (hasActions || hasName || hasKey) {
            String bindName = hasName ? currentName : TextUtils.translatable("nitsha.binds.untitled").getString();

            if ("minecraft:structure_void".equals(editIconBtnString)) {
                ItemsMapper.IconEntry randomItem = ItemsMapper.getRandomItem();
                editIconBtnString = randomItem != null ? randomItem.key() : "minecraft:grass_block";
            }

            BindData newBind = new BindData();
            newBind.index = getActiveBind();
            newBind.name = bindName;
            newBind.icon = editIconBtnString;
            newBind.keyCode = keyCode;
            newBind.keyMode = window_AdvancedOptions.getActionsTab().getTriggerMode();
            newBind.holdMs = window_AdvancedOptions.getActionsTab().getHoldMs();
            newBind.actions = new ArrayList<>(actions);

            if (activePreset != null && currentPage < activePreset.pages.size()) {
                 PageData page = activePreset.pages.get(currentPage);
                 
                 boolean found = false;
                 if (page.binds == null) page.binds = new ArrayList<>();
                 for (int i = 0; i < page.binds.size(); i++) {
                     if (page.binds.get(i).index == activeBind) {
                         page.binds.set(i, newBind);
                         found = true;
                         break;
                     }
                 }
                 if (!found) {
                     page.binds.add(newBind);
                 }
                 Storage.savePreset(activePreset, activePresetId);
            }

            selectBind();
            window_BindsList.updateSelected(ItemsMapper.getItemStack(getCBind().icon));
        } else {
            deleteBind();
        }
    }

    public void deleteBind() {
        if (activePreset != null && currentPage < activePreset.pages.size()) {
             PageData page = activePreset.pages.get(currentPage);
             boolean removed = false;
             if (page.binds != null) {
                 removed = page.binds.removeIf(b -> b.index == activeBind);
             }
             if (removed) {
                 Storage.savePreset(activePreset, activePresetId);
             }
        }
        selectBind();
        window_BindsList.updateSelected(ItemsMapper.getItemStack("minecraft:structure_void"));
    }

    public void copyBind() {
        BindData currentBind = getCBind();
        if (currentBind != null && currentBind.name != null && !currentBind.name.isEmpty()) {
            copied = new BindData();
            copied.index = currentBind.index;
            copied.name = currentBind.name;
            copied.icon = currentBind.icon;
            copied.keyCode = currentBind.keyCode;
            copied.keyMode = currentBind.keyMode;
            copied.holdMs = currentBind.holdMs;
            if (currentBind.actions != null) {
                copied.actions = new ArrayList<>(currentBind.actions);
            } else {
                copied.actions = new ArrayList<>();
            }
            window_BasicOptions.getPasteBtn().setEnabled(true);
        }
    }

    public void pasteBind() {
        if (activePreset != null && currentPage < activePreset.pages.size()) {
             PageData page = activePreset.pages.get(currentPage);
             if (page.binds == null) page.binds = new ArrayList<>();
             
             BindData newPastedBind = new BindData();
             newPastedBind.index = activeBind;
             newPastedBind.name = copied.name;
             newPastedBind.icon = copied.icon;
             newPastedBind.keyCode = copied.keyCode;
             newPastedBind.keyMode = copied.keyMode;
             newPastedBind.holdMs = copied.holdMs;
             newPastedBind.actions = new ArrayList<>(copied.actions);
             
             boolean found = false;
             for (int i = 0; i < page.binds.size(); i++) {
                 if (page.binds.get(i).index == activeBind) {
                     page.binds.set(i, newPastedBind);
                     found = true;
                     break;
                 }
             }
             if (!found) {
                 page.binds.add(newPastedBind);
             }
             Storage.savePreset(activePreset, activePresetId);
        }

        selectBind();
        window_BindsList.updateSelected(ItemsMapper.getItemStack(getCBind().icon));
    }

    public void selectBind() {
        BindData currentBind = getCBind();
        
        window_BasicOptions.getBindName().setText(currentBind.name == null ? "" : currentBind.name);
        window_BasicOptions.getEditIcon().setItemStack(ItemsMapper.getItemStack(currentBind.icon));
        editIconBtnString = currentBind.icon;
        window_AdvancedOptions.getActionsTab().keybind.setKeyCode(currentBind.keyCode);
        
        if (currentBind.actions != null) {
            window_AdvancedOptions.getActionsTab().generateActionList(currentBind.actions);
        } else {
            window_AdvancedOptions.getActionsTab().generateActionList(new ArrayList<>());
        }
        
        window_AdvancedOptions.getIconTab().getIconSelectorWindow().updateButtons(currentBind.icon);
        window_AdvancedOptions.getActionsTab().loadTriggerMode(currentBind.keyMode, currentBind.holdMs);
        window_BasicOptions.confirm(false);
        
        if(currentBind.name == null || currentBind.name.isEmpty()) {
            window_BasicOptions.getDeleteBtn().setEnabled(false);
        } else {
            window_BasicOptions.getDeleteBtn().setEnabled(true);
        }
    }

    @Override
    public void onRender(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        if (activePreset != null) {
            float gridExpansion = Math.max(0, activePreset.columns - 4) * 31 / 2.0F;
            window_AdvancedOptions.setX(centerX + 61 + gridExpansion);
        }

        boolean anyModalOpen = false;

        for (ModalWindow modal : modalWindows) {
            if (modal.isVisible()) {
                anyModalOpen = true;
                break;
            }
        }

        boolean selectorHovered = window_PresetSelector.isOpen() && window_PresetSelector.isMouseInside(mouseX, mouseY);
        boolean optionsHovered = window_PresetOptions.isOpen() && window_PresetOptions.isMouseInside(mouseX, mouseY);
        boolean formattingHovered = window_TextFormatting.hasTarget() && window_TextFormatting.isMouseInside(mouseX, mouseY);

        for (GuiEventListener element : children()) {
            if (element instanceof ModalWindow || element == window_PresetSelector || element == window_PresetOptions || element == window_TextFormatting) continue;

            boolean blockHover = anyModalOpen || selectorHovered || optionsHovered || formattingHovered;
            int elementMouseX = blockHover ? -10000 : mouseX;
            int elementMouseY = blockHover ? -10000 : mouseY;

            RenderUtils.renderElement(ctx, element, elementMouseX, elementMouseY, delta);
        }

        boolean blockOptionsHover = anyModalOpen || selectorHovered || formattingHovered;
        RenderUtils.renderElement(ctx, window_PresetOptions, blockOptionsHover ? -10000 : mouseX, blockOptionsHover ? -10000 : mouseY, delta);
        RenderUtils.renderElement(ctx, window_PresetSelector, anyModalOpen || formattingHovered ? -10000 : mouseX, anyModalOpen || formattingHovered ? -10000 : mouseY, delta);

        for (GuiEventListener element : children()) {
            if (element instanceof ModalWindow modal && modal.isVisible()) {
                RenderUtils.renderElement(ctx, modal, mouseX, mouseY, delta);
            }
        }

        TextField field = TextField.getFocusedField();
        RenderUtils.WidgetBounds bounds = field != null ? field.getFormattingBounds() : null;
        boolean showFormatting = !anyModalOpen && !window_PresetSelector.isOpen() && !window_PresetOptions.isOpen() && !window_AdvancedOptions.getActionsTab().getAddNewAction().isOpen();

        if (showFormatting && field != null && field.isFormattingAllowed() && field.isFocused() && bounds != null && bounds.fullyVisible() && bounds.y() >= 0 && bounds.y() + bounds.height() <= height) {
            window_TextFormatting.showFor(field, bounds, width, height);
        } else {
            window_TextFormatting.hide();
            if (!anyModalOpen && field != null && field.isFormattingAllowed() && bounds == null) {
                TextField.setFocusedField(null);
            }
        }

        if (anyModalOpen) {
            window_TextFormatting.tick();
        } else if (window_TextFormatting.isVisible()) {
            RenderUtils.renderOverlay(ctx, window_TextFormatting, window_TextFormatting.hasTarget() ? mouseX : -10000, window_TextFormatting.hasTarget() ? mouseY : -10000, delta);
        }
    }

    @Override
    protected void onBackground(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        if (this.minecraft.level != null) {
            return;
        }

        //? if >=1.20.5 {
        drawNitshaPanorama(ctx, delta);
        drawNitshaBlur(ctx, delta);
        //? } else {
        /*super.onBackground(ctx, mouseX, mouseY, delta);*/
        //? }
    }

    @Override
    public void onClose() {
        NitshaCraft.setScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void closeEditor() {
        window_TextFormatting.hide();
        TextField.setFocusedField(null);
        KeybindSelector.setFocusedField(null);
        MainKeybindSelector.setFocusedField(null);
        this.saveBind();
        window_PresetSelector.saveAll();
        AudioPlayer.stopChannel(9);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        KeybindSelector keybind = KeybindSelector.getFocusedField();
        MainKeybindSelector mainKeybind = MainKeybindSelector.getFocusedField();

        if (keybind != null && keybind.captureMouseButton(button)) {
            mouseCapture = keybind;
            mouseCaptureButton = button;
            return true;
        }

        if (mainKeybind != null && mainKeybind.captureMouseButton(button)) {
            mouseCapture = mainKeybind;
            mouseCaptureButton = button;
            return true;
        }

        TextField.setLastClickedWidget(null);
        KeybindSelector.setLastClickedWidget(null);
        MainKeybindSelector.setLastClickedWidget(null);

        for (ModalWindow modal : modalWindows) {
            if (modal.isVisible()) {
                mouseCapture = modal;
                mouseCaptureButton = button;
                NitshaInput.mouseClicked(List.of(modal), mouseX, mouseY, button);

                if (!modal.isMouseInside(mouseX, mouseY)) {
                    modal.close(() -> {});
                }

                controlInputFocus();
                return true;
            }
        }

        if (window_TextFormatting.hasTarget() && window_TextFormatting.isMouseInside(mouseX, mouseY)) {
            mouseCapture = window_TextFormatting;
            mouseCaptureButton = button;
            NitshaInput.mouseClicked(List.of(window_TextFormatting), mouseX, mouseY, button);
            return true;
        }

        if (window_PresetSelector.isMouseInside(mouseX, mouseY)) {
            window_PresetOptions.openSelector(false);
            mouseCapture = window_PresetSelector;
            mouseCaptureButton = button;
            NitshaInput.mouseClicked(List.of(window_PresetSelector), mouseX, mouseY, button);
            controlInputFocus();
            return true;
        } else if (window_PresetSelector.isOpen()) {
            window_PresetSelector.openSelector(false);
        }

        if (window_PresetOptions.isMouseInside(mouseX, mouseY)) {
            mouseCapture = window_PresetOptions;
            mouseCaptureButton = button;
            NitshaInput.mouseClicked(List.of(window_PresetOptions), mouseX, mouseY, button);
            controlInputFocus();
            return true;
        } else if (window_PresetOptions.isOpen()) {
            window_PresetOptions.openSelector(false);
        }

        boolean clicked = false;

        for (GuiEventListener element : children()) {
            if (element != window_PresetSelector && element != window_PresetOptions && element != window_TextFormatting && NitshaInput.mouseClicked(List.of(element), mouseX, mouseY, button)) {
                clicked = true;
            }
        }

        controlInputFocus();

        return clicked;
    }

    private void controlInputFocus() {
        TextField.controlFocus();
        KeybindSelector.controlFocus();
        MainKeybindSelector.controlFocus();
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        if (mouseCapture != null) {
            if (button == mouseCaptureButton) {
                GuiEventListener captured = mouseCapture;
                mouseCapture = null;
                mouseCaptureButton = -1;
                NitshaInput.mouseReleased(List.of(captured), mouseX, mouseY, button);
            }
            return true;
        }

        return NitshaInput.mouseReleased(children(), mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (mouseCapture != null) {
            if (button == mouseCaptureButton) {
                NitshaInput.mouseDragged(List.of(mouseCapture), mouseX, mouseY, button, deltaX, deltaY);
            }
            return true;
        }

        return NitshaInput.mouseDragged(children(), mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (ModalWindow modal : modalWindows) {
            if (modal.isVisible()) {
                NitshaInput.mouseScrolled(List.of(modal), mouseX, mouseY, horizontalAmount, verticalAmount);
                return true;
            }
        }

        if (window_TextFormatting.hasTarget() && window_TextFormatting.isMouseInside(mouseX, mouseY)) {
            return true;
        }

        if (window_PresetSelector.isMouseInside(mouseX, mouseY)) {
            NitshaInput.mouseScrolled(List.of(window_PresetSelector), mouseX, mouseY, horizontalAmount, verticalAmount);
            return true;
        }

        if (window_PresetOptions.isMouseInside(mouseX, mouseY)) {
            NitshaInput.mouseScrolled(List.of(window_PresetOptions), mouseX, mouseY, horizontalAmount, verticalAmount);
            return true;
        }

        return NitshaInput.mouseScrolled(children(), mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean onCharTyped(int codePoint) {
        return NitshaInput.charTyped(children(), codePoint);
    }

    @Override
    public boolean onKeyPressed(int keyCode, int nativeCode, int modifiers) {
        TextField focusedField = TextField.getFocusedField();
        KeybindSelector focusedKeybind = KeybindSelector.getFocusedField();
        MainKeybindSelector focusedMainKeybind = MainKeybindSelector.getFocusedField();

        boolean escape = keyCode == InputConstants.KEY_ESCAPE;
        boolean enter = keyCode == InputConstants.KEY_RETURN;

        if (focusedField != null) {
            if (NitshaInput.keyPressed(List.of(focusedField), keyCode, nativeCode, modifiers)) {
                return true;
            }

            if (escape || enter) {
                Runnable action = escape ? focusedField.getEscapeEvent() : focusedField.getEnterEvent();

                if (action != null) {
                    action.run();
                }

                TextField.setFocusedField(null);
            }

            return true;
        }

        if (focusedKeybind != null) {
            if (NitshaInput.keyPressed(List.of(focusedKeybind), keyCode, nativeCode, modifiers)) {
                return true;
            }

            if (escape) {
                KeybindSelector.setFocusedField(null);
                return true;
            }
        }

        if (focusedMainKeybind != null) {
            if (NitshaInput.keyPressed(List.of(focusedMainKeybind), keyCode, nativeCode, modifiers)) {
                return true;
            }

            if (escape) {
                MainKeybindSelector.setFocusedField(null);
                return true;
            }
        }

        if (escape) {
            for (ModalWindow modal : modalWindows) {
                if (modal.isVisible()) {
                    modal.close(() -> {});
                    return true;
                }
            }

            if (window_PresetSelector.isOpen()) {
                window_PresetSelector.openSelector(false);
                return true;
            }

            if (window_PresetOptions.isOpen()) {
                window_PresetOptions.openSelector(false);
                return true;
            }

            closeEditor();
            onClose();
            return true;
        }

        if (NitshaInput.matchesKey(KeyBinds.PREV_PAGE, keyCode, nativeCode)) {
            setNewPage(-1);
            return true;
        }

        if (NitshaInput.matchesKey(KeyBinds.NEXT_PAGE, keyCode, nativeCode)) {
            setNewPage(1);
            return true;
        }

        return super.onKeyPressed(keyCode, nativeCode, modifiers);
    }
}
