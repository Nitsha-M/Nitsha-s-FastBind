package com.nitsha.binds.gui.screen;

import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.configs.BindEntry;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.gui.panels.*;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.gui.utils.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.font.TextRenderer;
import com.nitsha.binds.gui.utils.GUIUtils;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BindsEditor extends Screen {
    private static final Identifier BACKGROUND = MainClass.id("textures/gui/test/editor_bg.png");
    private static final Identifier BACKGROUND_FLAT = MainClass.id("textures/gui/test/editor_bg_flat.png");
    private static final Identifier BACKGROUND_DARK = MainClass.id("textures/gui/test/editor_bg_dark.png");
    private static final Identifier BACKGROUND_DARK_FLAT = MainClass.id("textures/gui/test/editor_bg_dark_flat.png");

    private final int TEXTURE_WIDTH = 141;
    private final int TEXTURE_HEIGHT = 190;

    private int centerX;
    private int centerY;

    private boolean selectorOpening = false;
    public static boolean isSelectorOpened = false;

    private static int currentPage = 0;
    private static int activeBind = 0;
    public static String editIconBtnString = "STRUCTURE_VOID";

    public BindEntry copied = new BindEntry(
            "",
            "STRUCTURE_VOID",
            0,
            new ArrayList<>()
    );

    private BasicOptionsWindow window_BasicOptions;
    private BindsList window_BindsList;
    private AdvancedOptions window_AdvancedOptions;
    private PresetSelector window_PresetSelector;

    private final int coeff = 200;
    public static int currentPreset = BindsGUI.getCurrentPreset();

    private final Screen parent;
    public BindsEditor(Screen parent) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        currentPage = 0;
        activeBind = 0;
    }

    protected void init() {
        super.init();
        //? if >=1.17 {
        this.clearChildren();
        //? } else {
        this.children().clear();
        //? }
        this.centerX = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.centerY = (this.height - TEXTURE_HEIGHT) / 2;

        isSelectorOpened = selectorOpening = false;

        // Basic options (bind name, single-command, icon)
        window_BasicOptions = new BasicOptionsWindow(this, centerX, centerY, TEXTURE_WIDTH, TEXTURE_HEIGHT, BACKGROUND, BACKGROUND_FLAT, 0);
        //? if >=1.17 {
        this.addDrawableChild(window_BasicOptions);
        //? } else {
//        this.addChild(window_BasicOptions);
        //? }

        // Binds list (gray window with all buttons)
        window_BindsList = new BindsList(this, centerX + 4, centerY - 1, 133, 100, BACKGROUND_DARK, BACKGROUND_DARK_FLAT, 140);
        //? if >=1.17 {
        this.addDrawableChild(window_BindsList);
        //? } else {
//        this.addChild(window_BindsList);
        //? }

        // Preset selector and editor
        window_PresetSelector = new PresetSelector(this, centerX + 7, centerY - 24, 127, 19, BACKGROUND_DARK, BACKGROUND_DARK_FLAT, 250);
        //? if >=1.17 {
        this.addDrawableChild(window_PresetSelector);
        //? } else {
//        this.addChild(window_PresetSelector);
        //? }

        // Advanced options (icon, actions, mod options)
        window_AdvancedOptions = new AdvancedOptions(this,centerX + 61, centerY, coeff - 20, TEXTURE_HEIGHT, BACKGROUND, BACKGROUND_FLAT, 0);
        //? if >=1.17 {
        this.addDrawableChild(window_AdvancedOptions);
        //? } else {
//        this.addChild(window_AdvancedOptions);
        //? }

        selectBind();
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
        List<String> presets = (List<String>) BindsConfig.configs.get("presets");
        return presets.get(currentPreset);
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

    public void setNewPage(int dir) {
        currentPage = (currentPage + dir + 5) % 5;
        window_BindsList.generateButtons(7, 31);
    }

    public void setNewPreset(int dir) {
        selectPreset((currentPreset + dir + 9) % 9);
    }

    // Functions
    public void selectPreset(int i) {
        currentPreset = i;
        currentPage = 0;
        setActiveBind(0);
        window_BindsList.generateButtons(7, 31);
        selectBind();
    }

    public static BindEntry getCBind() {
        return BindsConfig.getBind(getCurrentPreset(), getActiveBind());
    }

    public void openAdvancedOptions() {
        if (selectorOpening) return;

        selectorOpening = true;

        int xDelta = coeff / 2;
        int xDeltaN = -coeff / 2;

        if (!isSelectorOpened) {
            window_BasicOptions.updateX(xDelta);
            window_BindsList.updateX(xDelta);
            window_PresetSelector.updateX(xDelta);
            window_AdvancedOptions.open(() -> {
                isSelectorOpened = true;
                selectorOpening = false;
            });
        } else {
            window_AdvancedOptions.close(() -> {
                window_BasicOptions.updateX(xDeltaN);
                window_BindsList.updateX(xDeltaN);
                window_PresetSelector.updateX(xDeltaN);
                isSelectorOpened = false;
                selectorOpening = false;
            });
        }

        window_BindsList.updateSelected(ItemsMapper.getItemStack(getCBind().icon));
    }

    public static int getCurrentPreset() {
        return currentPreset;
    }

    public void saveBind() {
        if (!getCBind().actions.isEmpty())  {
            String bindName = window_BasicOptions.getBindName().getText().isEmpty() ? TextUtils.translatable("nitsha.binds.untitled").getString() : window_BasicOptions.getBindName().getText();
            if (editIconBtnString.equals("STRUCTURE_VOID")) editIconBtnString = "GRASS";
            int keyCode = window_AdvancedOptions.keybind.getKeyCode();

            BindsConfig.setBind(
                    getCurrentPreset(),
                    getActiveBind(),
                    new BindEntry(bindName, editIconBtnString, keyCode, window_AdvancedOptions.getAllActions())
            );
            BindsConfig.setBindKeyBind(getCurrentPreset(), getActiveBind(), keyCode);
            selectBind();
            window_BindsList.updateSelected(ItemsMapper.getItemStack(
                    getCBind().icon
            ));
        } else {
            deleteBind();
        }
    }

    public void deleteBind() {
        BindsConfig.setBind(getCurrentPreset(), getActiveBind(), new BindEntry(
                "",
                "STRUCTURE_VOID",
                0,
                new ArrayList<>()
        ));
        selectBind();
        window_BindsList.updateSelected(new ItemStack(Items.STRUCTURE_VOID));
    }

    public void copyBind() {
        BindEntry currentBind = getCBind();
        if(!currentBind.name.isEmpty()) {
            copied = currentBind;
            copied.name = currentBind.name;
            copied.icon = currentBind.icon;
            copied.keyCode = currentBind.keyCode;
            copied.actions = currentBind.actions;
            window_BasicOptions.getPasteIcon().setEnabled(true);
        }
    }

    public void pasteBind() {
        BindsConfig.setBind(currentPreset, activeBind, copied);
        selectBind();
        window_BindsList.updateSelected(ItemsMapper.getItemStack(getCBind().icon));
    }

    public void selectBind() {
        BindEntry currentBind = getCBind();
        window_BasicOptions.getBindName().setText(currentBind.name);
        window_BasicOptions.getEditIcon().setIcon(ItemsMapper.getItemStack(currentBind.icon));
        editIconBtnString = currentBind.icon;
        window_AdvancedOptions.keybind.setKeyCode(currentBind.keyCode);
        window_AdvancedOptions.generateActionList(currentBind.actions);
        window_AdvancedOptions.getFirstTab().updateButtons(currentBind.icon);
    }

    // Render
    @Override
    public void render(
            //? if >=1.20 {
            DrawContext ctx
            //? } else {
            /*MatrixStack ctx
            *///? }
            , int mouseX, int mouseY, float delta) {
        //? if >=1.20.2 {
        /*? if <1.21.6 { */this.renderBackground(ctx, mouseX, mouseY, delta); /*? }*/
        //? } else if >=1.19.4 {
        /*if (this.client.world == null) this.renderBackgroundTexture(ctx);*/
        //? } else {
        /*if (this.client.world == null) this.renderBackgroundTexture(0);*/
        //? }
        for (Element element : children()) {
            if (element instanceof Drawable dr) {
                if (element instanceof PresetSelector pS) {
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
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client.world == null) {
            //? if >=1.20.5 {
            this.renderPanoramaBackground(context, delta);
            this.applyBlur(/*? if >=1.21.6 {*/ /*context*/ /*? } else if <=1.21.1 { */ /*delta*/ /*? }*/);
            //? } else {
            /*this.renderBackgroundTexture(context);*/
            //? }
        }
    }
    //? }

    //? if >=1.18.2 {
    @Override
    public void close() {
        this.client.setScreen(parent);
    }
    //? } else if >=1.17.1 {
    /*@Override
    public void onClose() {
        this.client.setScreen(parent);
    }*/
    //? } else {
    /*@Override
    public void onClose() {
        this.client.openScreen(parent);
    }*/
    //? }

    @Override
    public boolean shouldCloseOnEsc() { return true; }

    //? if >=1.18.1 {
    @Override
    public boolean shouldPause() {
        return false;
    }
    //? } else {
    public boolean isPauseScreen() {
        return false;
    }
    //? }

    public void closeEditor() {
        TextField.setFocusedField(null);
        KeybindSelector.setFocusedField(null);
        this.saveBind();
        window_PresetSelector.saveAll();
    }

    // Click, scroll logic

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (window_PresetSelector.isMouseInside(mouseX, mouseY)) {
            window_PresetSelector.mouseClicked(mouseX, mouseY, button);
            return true;
        }

        boolean clicked = false;

        if (KeybindSelector.getFocusedField() != null) {
            KeybindSelector.getFocusedField().controlFocus(mouseX, mouseY);
        }
        if (TextField.getFocusedField() != null) {
            TextField.getFocusedField().controlFocus(mouseX, mouseY);
        }

        for (Element element : children()) {
            if (element != window_PresetSelector && element.mouseClicked(mouseX, mouseY, button)) {
                clicked = true;
            }
        }

        return clicked;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean released = false;
        for (Element child : children()) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                released = true;
            }
        }
        return released;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean dragged = false;
        for (Element child : children()) {
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                dragged = true;
            }
        }
        return dragged;
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (Element element : children()) {
            if (element.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    //? } else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (Element element : children()) {
            if (element.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    *///? }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Element element : children()) {
            if (element.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        TextField focusedField = TextField.getFocusedField();
        KeybindSelector focusedKeybind = KeybindSelector.getFocusedField();

        if (focusedField != null) {
            if (focusedField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                if (focusedField.escapeEvent != null) {
                    focusedField.escapeEvent.run();
                }
                TextField.setFocusedField(null);
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                if (focusedField.enterEvent != null) {
                    focusedField.enterEvent.run();
                }
                TextField.setFocusedField(null);
                return true;
            }
            return true;
        }

        if (focusedKeybind != null) {
            if (focusedKeybind.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }

            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                KeybindSelector.setFocusedField(null);
                return true;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            closeEditor();
        }

        if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.PREV_PAGE).getCode()) {
            setNewPage(-1);
            return true;
        }

        if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.NEXT_PAGE).getCode()) {
            setNewPage(1);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Environment(EnvType.CLIENT)
    public static class TextField extends TextFieldWidget {
        private static TextField focusedField = null;
        private final boolean isNumerical;
        private Runnable escapeEvent;
        private Runnable enterEvent;
        private String placeholderText;

        private int x, y;

        public TextField(TextRenderer textRenderer, int x, int y, int width, int height, int length, String text, String placeholder, boolean isNumerical) {
            //? if >1.20 {
            super(textRenderer, x, y, width, height, Text.of(text));
            //? } else {
            /*super(textRenderer, x + 1, y + 1, width - 2, height - 2, Text.of(text));*/
            //? }
            this.setMaxLength(length);
            this.setText(text);
            //? if >=1.19.3 {
            this.setPlaceholder(Text.of(placeholder));
            //? }
            this.setEditableColor((this.getText().isEmpty()) ? 0xFFAAAAAA : 0xFFFFFFFF);
            this.isNumerical = isNumerical;
            this.placeholderText = placeholder;
            this.x = x;
            this.y = y;
        }

        public TextField(TextRenderer textRenderer, int x, int y, int width, int height, int length, String text, String placeholder) {
            this(textRenderer, x, y, width, height, length, text, placeholder, false);
        }

        public void setEscapeEvent(Runnable event) {
            this.escapeEvent = event;
        }

        public void setEnterEvent(Runnable event) {
            this.enterEvent = event;
        }

        public static TextField getFocusedField() {
            return focusedField;
        }

        public static void setFocusedField(TextField ff) {
            if (focusedField != null) {
                focusedField.setFocused(false);
            }
            focusedField = ff;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            controlFocus(mouseX, mouseY);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        public void controlFocus(double mouseX, double mouseY) {
            if (isMouseOver(mouseX, mouseY)) {
                setFocusedField(this);
                this.setFocused(true);
            } else {
                setFocusedField(null);
            }
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            if (isNumerical) {
                if (chr >= '0' && chr <= '9') {
                    return super.charTyped(chr, modifiers);
                }
                return false;
            }
            return super.charTyped(chr, modifiers);
        }

        //? if >1.20.2 {
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderWidget(context, mouseX, mouseY, delta);
            if (this.getText().isEmpty() && !this.isFocused()) {
                this.setEditableColor(0xFFAAAAAA);
            } else {
                this.setEditableColor(0xFFFFFFFF);
            }
        }
        //? } else if >=1.20 {
        /*public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderButton(context, mouseX, mouseY, delta);
            if (this.getText().isEmpty() && !this.isFocused()) {
                this.setEditableColor(0xFFAAAAAA);
            } else {
                this.setEditableColor(0xFFFFFFFF);
            }
        }
        *///? } else if >=1.19.3 {
        /*public void renderButton(MatrixStack context, int mouseX, int mouseY, float delta) {
            super.renderButton(context, mouseX, mouseY, delta);
            if (this.getText().isEmpty() && !this.isFocused()) {
                this.setEditableColor(0xFFAAAAAA);
            } else {
                this.setEditableColor(0xFFFFFFFF);
            }
        }
        *///? } else {
        /*public void renderButton(MatrixStack context, int mouseX, int mouseY, float delta) {
            super.renderButton(context, mouseX, mouseY, delta);
            if (this.getText().isEmpty() && !this.isFocused()) {
                this.setEditableColor(0xFFAAAAAA);
                GUIUtils.addText(context, Text.of(this.placeholderText), 0, this.x + 5, (this.y + (this.height - 8) / 2) + 1, "left", "top", 0xFFAAAAAA, false);
            } else {
                this.setEditableColor(0xFFFFFFFF);
            }
        }
        */
        //? }
    }
}