package com.nitsha.binds.gui.screen;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.Main;
import com.nitsha.binds.configs.dto.preset.BindData;
import com.nitsha.binds.configs.dto.preset.PresetData;
import com.nitsha.binds.configs.dto.preset.PageData;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.configs.*;
import com.nitsha.binds.gui.modals.SelectIcon;
import com.nitsha.binds.gui.modals.SelectKeyEvent;
import com.nitsha.binds.gui.modals.SelectSound;
import com.nitsha.binds.gui.panels.*;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.utils.AudioPlayer;
import com.nitsha.binds.utils.EventBus;
import com.nitsha.binds.utils.RenderUtils;
import net.minecraft.world.level.block.Blocks;
//? if fabric {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?}
import net.minecraft.client.gui.GuiGraphics;
import com.nitsha.binds.utils.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.nitsha.binds.configs.dto.preset.ActionData;

//? if >=26.1 {
// import net.minecraft.world.item.ItemStackTemplate;
//? }

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class BindsEditor extends Screen {
    private static final ResourceLocation BACKGROUND = Main.id("textures/gui/test/editor_bg.png");
    private static final ResourceLocation BACKGROUND_FLAT = Main.id("textures/gui/test/editor_bg_flat.png");
    private static final ResourceLocation BACKGROUND_DARK = Main.id("textures/gui/test/editor_bg_dark.png");
    private static final ResourceLocation BACKGROUND_DARK_FLAT = Main.id("textures/gui/test/editor_bg_dark_flat.png");

    private final int TEXTURE_WIDTH = 141;
    private final int TEXTURE_HEIGHT = 190;

    private int centerX;
    private int centerY;

    public static String activePresetId;
    public static PresetData activePreset;
    public static int currentPage = 0;
    public static int activeBind = 0;
    public static String editIconBtnString = "minecraft:structure_void";

    public BindData copied = new BindData();

    private BasicOptionsWindow window_BasicOptions;
    private BindsList window_BindsList;
    private AdvancedOptions window_AdvancedOptions;
    private PresetSelector window_PresetSelector;

    private SelectKeyEvent modal_SelectKeyEvent;
    private SelectSound modal_SelectSound;
    private SelectIcon modal_SelectIcon;

    private final Screen parent;

    private List<ModalWindow> modalWindows = new ArrayList<>();

    public BindsEditor(Screen parent) {
        super(TextUtils.empty());
        this.parent = parent;
    }

    protected void init() {
        super.init();
        //? if >=1.17 {
        this.clearWidgets();
        //?} else {
        /*this.children().clear();*/
        //? }
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

            if (Storage.options.openLastPage) {
                currentPage = Math.min(Storage.options.lastPageIndex, activePreset.pages.size() - 1);
            } else {
                currentPage = 0;
            }
        }

        // Basic options (bind name, single-command, icon)
        window_BasicOptions = new BasicOptionsWindow(this, centerX - 100, centerY, TEXTURE_WIDTH, TEXTURE_HEIGHT,
                BACKGROUND, BACKGROUND_FLAT, 0);
        //? if >=1.17 {
        this.addRenderableWidget(window_BasicOptions);
        //?} else {
        // this.addWidget(window_BasicOptions);
        //?}

        // Binds list (gray window with all buttons)
        window_BindsList = new BindsList(this, centerX + 4 - 100, centerY - 1, 133, 122, BACKGROUND_DARK,
                BACKGROUND_DARK_FLAT, 140);
        //? if >=1.17 {
        this.addRenderableWidget(window_BindsList);
        //?} else {
        // this.addWidget(window_BindsList);
        //?}

        // Preset selector and editor
        window_PresetSelector = new PresetSelector(this, centerX + 7 - 100, centerY - 19, 127, 17,250);
        //? if >=1.17 {
        this.addRenderableWidget(window_PresetSelector);
        //? } else {
        // this.addWidget(window_PresetSelector);
        //? }

        // Advanced options (icon, actions, mod options)
        window_AdvancedOptions = new AdvancedOptions(this, centerX + 61, (Storage.options.fullHeightEditor) ? 16 : centerY, 180, (Storage.options.fullHeightEditor) ? this.height - 16 : TEXTURE_HEIGHT,
                BACKGROUND, BACKGROUND_FLAT, 140);
        //? if >=1.17 {
        this.addRenderableWidget(window_AdvancedOptions);
        //?} else {
        // this.addWidget(window_AdvancedOptions);
        //?}

        // Select key event
        modal_SelectKeyEvent = new SelectKeyEvent(this, centerX, centerY, 180, TEXTURE_HEIGHT,
                BACKGROUND, BACKGROUND_FLAT);
        //? if >=1.17 {
        this.addRenderableWidget(modal_SelectKeyEvent);
        //?} else {
        // this.addWidget(modal_SelectKeyEvent);
        //?}
        modalWindows.add(modal_SelectKeyEvent);

        // Select icon
        modal_SelectIcon = new SelectIcon(this, centerX, centerY, 180, TEXTURE_HEIGHT,
                BACKGROUND, BACKGROUND_FLAT);
        //? if >=1.17 {
        this.addRenderableWidget(modal_SelectIcon);
        //?} else {
        // this.addWidget(modal_SelectIcon);
        //?}
        modalWindows.add(modal_SelectIcon);

        // Select icon
        modal_SelectSound = new SelectSound(this, centerX, centerY, 180, TEXTURE_HEIGHT,
                BACKGROUND, BACKGROUND_FLAT);
        //? if >=1.17 {
        this.addRenderableWidget(modal_SelectSound);
        //?} else {
        // this.addWidget(modal_SelectSound);
        //?}
        modalWindows.add(modal_SelectSound);

        selectBind();
        window_BindsList.updateSelected(ItemsMapper.getItemStack(getCBind().icon));

        EventBus.on("selectKeyEvent.open", (Void v) -> {
            modal_SelectKeyEvent.open(() -> {});
        });

        EventBus.on("selectIcon.open", (Void v) -> {
            modal_SelectIcon.open(() -> {});
        });

        EventBus.on("selectSound.open", (Void v) -> {
            modal_SelectSound.open(() -> {});
        });
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
    }

    public static int getPageOfActiveBind() {
        return activeBind / 8;
    }

    public static boolean isActiveBindOnPage(int page) {
        return getPageOfActiveBind() == page;
    }

    public static int getFirstBindOfPage(int page) {
        return page * 8;
    }

    public void setNewPage(int dir) {
        if (activePreset == null) return;
        int totalPages = activePreset.pages.size();
        if (totalPages == 0) return;
        if (dir == -1 && currentPage == 0)
            currentPage = totalPages;
        currentPage += dir;
        if (currentPage >= totalPages)
            currentPage = 0;
            
        Storage.options.lastPresetId = activePresetId;
        Storage.options.lastPageIndex = currentPage;
        Storage.saveModOptions();
        window_BindsList.generateButtons(7, 31);
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
            selectBind();
        }
    }

    public static BindData getCBind() {
        if (activePreset != null && currentPage < activePreset.pages.size()) {
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
        List<ActionData> actions = window_AdvancedOptions.getAllActions();
        boolean hasActions = actions != null && !actions.isEmpty();
        String currentName = window_BasicOptions.getBindName().getText();
        boolean hasName = currentName != null && !currentName.trim().isEmpty();
        int keyCode = window_AdvancedOptions.keybind.getKeyCode();
        boolean hasKey = keyCode != 0 && keyCode != -1;

        String localizedUntitled = TextUtils.translatable("nitsha.binds.untitled").getString();
        if (hasName && (currentName.equals(localizedUntitled) || currentName.equals("Untitled") || currentName.equals("Без названия")) && !hasActions && !hasKey) {
            hasName = false;
        }

        if (hasActions || hasName || hasKey) {
            //? if >=26.1 {
            // Map.Entry<String, ItemStackTemplate> randomItem = ItemsMapper.getRandomItem();
            //? } else {
            Map.Entry<String, ItemStack> randomItem = ItemsMapper.getRandomItem();
            //? }
            String newIcon = (randomItem != null ) ? randomItem.getKey() : "minecraft:grass_block";
            String bindName = !hasName
                    ? TextUtils.translatable("nitsha.binds.untitled").getString()
                    : currentName;
            if (editIconBtnString.equals("minecraft:structure_void"))
                editIconBtnString =  newIcon;

            BindData newBind = new BindData();
            newBind.index = getActiveBind();
            newBind.name = bindName;
            newBind.icon = editIconBtnString;
            newBind.keyCode = keyCode;
            newBind.keyMode = window_AdvancedOptions.getTriggerMode();
            newBind.holdMs = window_AdvancedOptions.getHoldMs();
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
        //? if >=26.1 {
        // window_BindsList.updateSelected(ItemStackTemplate.fromNonEmptyStack(new ItemStack(Blocks.STRUCTURE_VOID)));
        //? } else {
        window_BindsList.updateSelected(new ItemStack(Items.STRUCTURE_VOID));
        //? }
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
        window_BasicOptions.getEditIcon().setIcon(ItemsMapper.getItemStack(currentBind.icon));
        editIconBtnString = currentBind.icon;
        window_AdvancedOptions.keybind.setKeyCode(currentBind.keyCode);
        
        if (currentBind.actions != null) {
            window_AdvancedOptions.generateActionList(currentBind.actions);
        } else {
            window_AdvancedOptions.generateActionList(new ArrayList<>());
        }
        
        window_AdvancedOptions.getSecondTab().updateButtons(currentBind.icon);
        window_AdvancedOptions.loadTriggerMode(currentBind.keyMode, currentBind.holdMs);
        window_BasicOptions.confirm(false);
        
        if(currentBind.name == null || currentBind.name.isEmpty()) {
            window_BasicOptions.getDeleteBtn().setEnabled(false);
        } else {
            window_BasicOptions.getDeleteBtn().setEnabled(true);
        }
    }

    // Render
    @Override
    //? if >=26.1 {
    // public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
    //? } else {
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
    //? }
        //? if >=1.20.2 {
        /*? if <1.21.6 { */this.renderBackground(ctx, mouseX, mouseY, delta); /*? } */
        //? } else if >=1.19.4 {
        /* if (this.minecraft.level == null) this.renderBackground(ctx); */
        //? } else {
        /* if (this.minecraft.level == null) this.renderBackground(ctx); */
        //? }
        //? if >=26.1 {
        /*
        boolean anyModalOpen = false;
        for (ModalWindow m : modalWindows) { if (m.isVisible()) { anyModalOpen = true; break; } }
        
        for (GuiEventListener element : children()) {
            Renderable dr = RenderUtils.wrapRenderable(element);
            if (dr != null) {
                if (element instanceof PresetSelector) {
                    PresetSelector pS = (PresetSelector) element;
                    pS.extractRenderState(ctx, mouseX, mouseY, delta);
                } else if (element instanceof ModalWindow) {
                    ModalWindow modal = (ModalWindow) element;
                    if (modal.isVisible()) {
                        modal.extractRenderState(ctx, mouseX, mouseY, delta);
                    }
                } else {
                    if (window_PresetSelector.isMouseInside(mouseX, mouseY) && window_PresetSelector.isOpen() || anyModalOpen) {
                        dr.extractRenderState(ctx, -10000, -10000, delta);
                    } else {
                        dr.extractRenderState(ctx, mouseX, mouseY, delta);
                    }
                }
            }
        }*/
        //? } else {
        boolean anyModalOpen = false;
        for (ModalWindow m : modalWindows) { if (m.isVisible()) { anyModalOpen = true; break; } }
        
        for (GuiEventListener element : children()) {
            Renderable dr = RenderUtils.wrapRenderable(element);
            if (dr != null) {
                if (element instanceof PresetSelector) {
                    PresetSelector pS = (PresetSelector) element;
                    pS.render(ctx, mouseX, mouseY, delta);
                } else if (element instanceof ModalWindow) {
                    ModalWindow modal = (ModalWindow) element;
                    if (modal.isVisible()) {
                        modal.render(ctx, mouseX, mouseY, delta);
                    }
                } else {
                    if (window_PresetSelector.isMouseInside(mouseX, mouseY) && window_PresetSelector.isOpen() || anyModalOpen) {
                        dr.render(ctx, -10000, -10000, delta);
                    } else {
                        dr.render(ctx, mouseX, mouseY, delta);
                    }
                }
            }
        }
        //? }
    }

    //? if >=26.1 {
    /*@Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (this.minecraft.level == null) {
            //? if >=1.20.5 {
            this.extractPanorama(context, delta);
            this.extractBlurredBackground(
                    //? if >=1.21.6 {
                    context
                    //? } else if <=1.21.1 {
                    // delta
                    //? }
            );
            //?} else {
            *//* super.renderBackground(context, mouseX, mouseY, delta); *//*
            //? }
        }
    }*/
    //? } else >=1.20.2 {
    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.minecraft.level == null) {
            //? if >=1.20.5 {
            this.renderPanorama(context, delta);
            this.renderBlurredBackground(
                    //? if >=1.21.6 {
                    // context
                    //? } else if <=1.21.1 {
                    // delta
                    //? }
            );
            //?} else {
            /* super.renderBackground(context, mouseX, mouseY, delta); */
            //? }
        }
    }
    //? }

    //? if >=1.18.2 {
    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
    //? } else if >=1.17.1 {
    /*@Override
     public void onClose() {
     this.minecraft.setScreen(parent);
     }*/
    //? } else {
    /*
     @Override
     public void onClose() {
     this.minecraft.setScreen(parent);
     }
     */
    //? }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    //? if >=1.18.1 {
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    //? } else {
    /*
    public boolean isPauseScreen() {
    return false;
    }
     *///? }

    public void closeEditor() {
        TextField.setFocusedField(null);
        KeybindSelector.setFocusedField(null);
        MainKeybindSelector.setFocusedField(null);
        this.saveBind();
        window_PresetSelector.saveAll();
        AudioPlayer.stopChannel(9);
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double mouseX = event.x();
        double mouseY = event.y();
        TextField.setLastClickedWidget(null);
        KeybindSelector.setLastClickedWidget(null);
        MainKeybindSelector.setLastClickedWidget(null);
        
        for (ModalWindow modal : modalWindows) {
            if (modal.isVisible()) {
                modal.mouseClicked(event, bl);
                if (!modal.isMouseInside(mouseX, mouseY)) {
                    modal.close(() -> {});
                }
                return true;
            }
        }
        
        if (window_PresetSelector.isMouseInside(mouseX, mouseY)) {
            window_PresetSelector.mouseClicked(event, bl);
            return true;
        } else if (window_PresetSelector.isOpen()) {
            window_PresetSelector.openSelector(false);
        }

        boolean clicked = false;
        for (GuiEventListener element : children()) {
            if (element != window_PresetSelector && element.mouseClicked(event, bl)) {
                clicked = true;
            }
        }
        TextField.controlFocus();
        KeybindSelector.controlFocus();
        MainKeybindSelector.controlFocus();
        return clicked;
    }*/
    //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        TextField.setLastClickedWidget(null);
        KeybindSelector.setLastClickedWidget(null);
        MainKeybindSelector.setLastClickedWidget(null);
        
        for (ModalWindow modal : modalWindows) {
            if (modal.isVisible()) {
                modal.mouseClicked(mouseX, mouseY, button);
                if (!modal.isMouseInside(mouseX, mouseY)) {
                    modal.close(() -> {});
                }
                return true;
            }
        }
        
        if (window_PresetSelector.isMouseInside(mouseX, mouseY)) {
            window_PresetSelector.mouseClicked(mouseX, mouseY, button);
            return true;
        } else if (window_PresetSelector.isOpen()) {
            window_PresetSelector.openSelector(false);
        }
        boolean clicked = false;
        for (GuiEventListener element : children()) {
            if (element != window_PresetSelector && element.mouseClicked(mouseX, mouseY, button)) {
                clicked = true;
            }
        }
        TextField.controlFocus();
        KeybindSelector.controlFocus();
        MainKeybindSelector.controlFocus();
        return clicked;
    }
    //? }
    @Override
    //? if >=1.21.9 {
    /*public boolean mouseReleased(MouseButtonEvent event) {
        boolean released = false;
        for (GuiEventListener child : children()) {
            if (child.mouseReleased(event)) {
                released = true;
            }
        }
        return released;
    }*/
    //? } else {
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean released = false;
        for (GuiEventListener child : children()) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                released = true;
            }
        }
        return released;
    }
    //? }
    @Override
    //? if >=1.21.9 {
    /*public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        boolean dragged = false;
        for (GuiEventListener child : children()) {
            if (child.mouseDragged(event, deltaX, deltaY)) {
                dragged = true;
            }
        }
        return dragged;
    }*/
    //? } else {
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean dragged = false;
        for (GuiEventListener child : children()) {
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                dragged = true;
            }
        }
        return dragged;
    }
    //? }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (ModalWindow modal : modalWindows) {
            if (modal.isVisible()) {
                modal.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
                return true;
            }
        }
        for (GuiEventListener element : children()) {
            if (element.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    //?} else {
    /*
     @Override
     public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (ModalWindow modal : modalWindows) {
            if (modal.isVisible()) {
                modal.mouseScrolled(mouseX, mouseY, amount);
                return true;
            }
        }
         for (GuiEventListener element : children()) {
             if (element.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
             }
         }
        return super.mouseScrolled(mouseX, mouseY, amount);
     }
     *///?}

    @Override
    //? if >=1.21.9 {
    /*public boolean charTyped(CharacterEvent event) {
        for (GuiEventListener element : children()) {
            if (element.charTyped(event)) {
                return true;
            }
        }
        return super.charTyped(event);
    }*/
    //? } else {
    public boolean charTyped(char codePoint, int modifiers) {
        for (GuiEventListener element : children()) {
            if (element.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }
    //? }

    @Override
    //? if >=1.21.9 {
    /*public boolean keyPressed(KeyEvent event) {
    int keyCode = event.key();
    int scanCode = event.scancode();*/
    //? } else {
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    //? }
        TextField focusedField = TextField.getFocusedField();
        KeybindSelector focusedKeybind = KeybindSelector.getFocusedField();
        MainKeybindSelector focusedMainKeybind = MainKeybindSelector.getFocusedField();

        if (focusedField != null) {
            //? if >=1.21.9 {
            // if (focusedField.keyPressed(event)) return true;
            //? } else {
            if (focusedField.keyPressed(keyCode, scanCode, modifiers)) return true;
            //? }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                if (focusedField.getEscapeEvent() != null) {
                    focusedField.getEscapeEvent().run();
                }
                TextField.setFocusedField(null);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                if (focusedField.getEnterEvent() != null) {
                    focusedField.getEnterEvent().run();
                }
                TextField.setFocusedField(null);
                return true;
            }
            return true;
        }

        if (focusedKeybind != null) {
            //? if >=1.21.9 {
            // if (focusedKeybind.keyPressed(event)) return true;
            //? } else {
            if (focusedKeybind.keyPressed(keyCode, scanCode, modifiers)) return true;
            //? }

            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                KeybindSelector.setFocusedField(null);
                return true;
            }
        }

        if (focusedMainKeybind != null) {
            //? if >=1.21.9 {
            // if (focusedMainKeybind.keyPressed(event)) return true;
            //? } else {
            if (focusedMainKeybind.keyPressed(keyCode, scanCode, modifiers)) return true;
            //? }

            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                MainKeybindSelector.setFocusedField(null);
                return true;
            }
        }

        for (ModalWindow modal : modalWindows) {
            if (modal.isVisible()) {
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    modal.close(() -> {});
                    return true;
                }
            }
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            closeEditor();
        }

        //? if fabric {
        if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.PREV_PAGE).getValue()) {
            //?} else {
            /*
             if (keyCode == KeyBinds.PREV_PAGE.getKey().getValue()) {
             *///?}
            setNewPage(-1);
            return true;
        }

        //? if fabric {
        if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.NEXT_PAGE).getValue()) {
            //?} else {
            /*
             if (keyCode == KeyBinds.NEXT_PAGE.getKey().getValue()) {
             *///?}
            setNewPage(1);
            return true;
        }

        //? if >=1.21.9 {
        // return super.keyPressed(event);
        //? } else {
        return super.keyPressed(keyCode, scanCode, modifiers);
        //? }
    }
}