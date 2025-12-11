package com.nitsha.binds.gui.screen;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.Main;
import com.nitsha.binds.configs.*;
import com.nitsha.binds.gui.panels.*;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.utils.RenderUtils;
//? if fabric {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?}
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.vertex.PoseStack;*/
//? }
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

    private static int currentPage = 0;
    private static int activeBind = 0;
    public static String editIconBtnString = "minecraft:structure_void";

    public Bind copied = new Bind(
            "",
            "minecraft:structure_void",
            0,
            new ArrayList<>());

    private BasicOptionsWindow window_BasicOptions;
    private BindsList window_BindsList;
    private AdvancedOptions window_AdvancedOptions;
    private PresetSelector window_PresetSelector;

    public static int currentPreset = BindsGUI.getCurrentPreset();

    private final Screen parent;

    public BindsEditor(Screen parent) {
        super(TextUtils.empty());
        this.parent = parent;
        currentPage = 0;
        activeBind = 0;
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
        window_AdvancedOptions = new AdvancedOptions(this, centerX + 61, centerY, 180, TEXTURE_HEIGHT,
                BACKGROUND, BACKGROUND_FLAT, 140);
        //? if >=1.17 {
        this.addRenderableWidget(window_AdvancedOptions);
        //?} else {
        // this.addWidget(window_AdvancedOptions);
        //?}

        selectBind();
        window_BindsList.updateSelected(ItemsMapper.getItemStack(getCBind().icon));
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
        return BindsStorage.presets.get(currentPreset).name;
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
        int totalPages = BindsStorage.presets.get(currentPreset).pages.size();
        if (dir == -1 && currentPage == 0)
            currentPage = totalPages;
        currentPage += dir;
        if (currentPage == totalPages)
            currentPage = 0;
        window_BindsList.generateButtons(7, 31);
    }

    public void selectPage(int i) {
        currentPage = i;
    }

    public void setNewPreset(int dir) {
        int totalPresets = BindsStorage.presets.size();
        if (dir == -1 && currentPreset == 0)
            currentPreset = totalPresets;
        currentPreset += dir;
        if (currentPreset == totalPresets)
            currentPreset = 0;
        selectPreset(currentPreset);
    }

    // Functions
    public void selectPreset(int i) {
        if (i >= 0 && i < BindsStorage.presets.size()) {
            currentPreset = i;
            currentPage = 0;
            setActiveBind(0);
            window_BindsList.generateButtons(7, 31);
            selectBind();
        }
    }

    public static Bind getCBind() {
        return BindsStorage.getBind(getCurrentPreset(), getActiveBind());
    }

    public static int getCurrentPreset() {
        return currentPreset;
    }

    public void saveBind() {
        if (!getCBind().actions.isEmpty()) {
            Map.Entry<String, ItemStack> randomItem = ItemsMapper.getRandomItem();
            String newIcon = (randomItem != null ) ? randomItem.getKey() : "minecraft:grass_block";
            String bindName = window_BasicOptions.getBindName().getText().isEmpty()
                    ? TextUtils.translatable("nitsha.binds.untitled").getString()
                    : window_BasicOptions.getBindName().getText();
            if (editIconBtnString.equals("minecraft:structure_void"))
                editIconBtnString =  newIcon;
            int keyCode = window_AdvancedOptions.keybind.getKeyCode();

            BindsStorage.setBind(
                    getCurrentPreset(),
                    getActiveBind(),
                    new Bind(bindName, editIconBtnString, keyCode, window_AdvancedOptions.getAllActions()));
            BindsStorage.setBindKeyBind(getCurrentPreset(), getActiveBind(), keyCode);
            selectBind();
            window_BindsList.updateSelected(ItemsMapper.getItemStack(
                    getCBind().icon));
        } else {
            deleteBind();
        }
    }

    public void deleteBind() {
        BindsStorage.setBind(getCurrentPreset(), getActiveBind(), new Bind(
                "",
                "minecraft:structure_void",
                0,
                new ArrayList<>()));
        selectBind();
        window_BindsList.updateSelected(new ItemStack(Items.STRUCTURE_VOID));
    }

    public void copyBind() {
        Bind currentBind = getCBind();
        if (!currentBind.name.isEmpty()) {
            copied = currentBind;
            copied.name = currentBind.name;
            copied.icon = currentBind.icon;
            copied.keyCode = currentBind.keyCode;
            copied.actions = currentBind.actions;
            window_BasicOptions.getPasteIcon().setEnabled(true);
        }
    }

    public void pasteBind() {
        BindsStorage.setBind(currentPreset, activeBind, copied);
        selectBind();
        window_BindsList.updateSelected(ItemsMapper.getItemStack(getCBind().icon));
    }

    public void selectBind() {
        Bind currentBind = getCBind();
        window_BasicOptions.getBindName().setText(currentBind.name);
        window_BasicOptions.getEditIcon().setIcon(ItemsMapper.getItemStack(currentBind.icon));
        editIconBtnString = currentBind.icon;
        window_AdvancedOptions.keybind.setKeyCode(currentBind.keyCode);
        window_AdvancedOptions.generateActionList(currentBind.actions);
        window_AdvancedOptions.getSecondTab().updateButtons(currentBind.icon);
    }

    // Render
    @Override
    public void render(
            //? if >=1.20 {
            GuiGraphics ctx
            //?} else {
            /*PoseStack ctx*/
            //?}
            , int mouseX, int mouseY, float delta) {
        //? if >=1.20.2 {
        /* ? if <1.21.6 { */this.renderBackground(ctx, mouseX, mouseY, delta); /* ?} */
        //?} else if >=1.19.4 {
        /* if (this.minecraft.level == null) this.renderBackground(ctx); */
        //?} else {
        /* if (this.minecraft.level == null) this.renderBackground(ctx); */
        //?}
        for (GuiEventListener element : children()) {
            Renderable dr = RenderUtils.wrapRenderable(element);
            if (dr != null) {
                if (element instanceof PresetSelector) {
                    PresetSelector pS = (PresetSelector) element;
                    pS.render(ctx, mouseX, mouseY, delta);
                } else {
                    if (window_PresetSelector.isMouseInside(mouseX, mouseY) && window_PresetSelector.isOpen()) {
                        dr.render(ctx, -10000, -10000, delta);
                    } else {
                        dr.render(ctx, mouseX, mouseY, delta);
                    }
                }
            }
        }
    }

    //? if >=1.20.2 {
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
            //?}
        }
    }
    //?}

    //? if >=1.18.2 {
    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
    //?} else if >=1.17.1 {
    /*@Override
     public void onClose() {
     this.minecraft.setScreen(parent);
     }*/
    //?} else {
    /*
     @Override
     public void onClose() {
     this.minecraft.setScreen(parent);
     }
     */
    //?}

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    //? if >=1.18.1 {
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    //?} else {
    /*
    public boolean isPauseScreen() {
    return false;
    }
     *///?}

    public void closeEditor() {
        TextField.setFocusedField(null);
        KeybindSelector.setFocusedField(null);
        this.saveBind();
        window_PresetSelector.saveAll();
    }

    // Click, scroll logic

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double mouseX = event.x();
        double mouseY = event.y();
        TextField.setLastClickedWidget(null);
        KeybindSelector.setLastClickedWidget(null);
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
        return clicked;
    }*/
    //? } else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        TextField.setLastClickedWidget(null);
        KeybindSelector.setLastClickedWidget(null);
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