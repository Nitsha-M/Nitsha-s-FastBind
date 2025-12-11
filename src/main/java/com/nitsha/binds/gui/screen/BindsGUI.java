package com.nitsha.binds.gui.screen;

import com.mojang.blaze3d.platform.Window;
import com.nitsha.binds.Main;
import com.nitsha.binds.configs.Bind;
import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.configs.Preset;
import com.nitsha.binds.configs.Page;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.gui.utils.AnimatedSprite;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.ItemButton;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.utils.BindExecutor;
//? if fabric {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?}

import net.minecraft.client.Minecraft;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//? } else {
/*import com.mojang.blaze3d.vertex.PoseStack;
 *///?}
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.KeyMapping;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

public class BindsGUI extends Screen {
    // Textures
    private static final ResourceLocation EDIT_BTN = Main.idSprite("edit");
    private static final ResourceLocation EDIT_BTN_HOVER = Main.idSprite("edit_hover");
    private static final ResourceLocation ITEMS_SELECTOR = Main.id("textures/gui/test/items_3.png");
    private static final ResourceLocation MENU_BG = Main.id("textures/gui/test/menu_bg.png");
    private static final ResourceLocation MENU_HEADER = Main.id("textures/gui/test/menu_header.png");

    private final List<ItemButton> itemButtons = new ArrayList<>();
    private String displayText = TextUtils.translatable("nitsha.binds.list").getString();
    private int centerX;
    private int centerY;
    private static int currentPage;
    private final int MENU_WIDTH = 125;

    private String textLEFT, textRIGHT;
    private Component leftArrow, rightArrow;

    public static int currentPreset;

    private static final ResourceLocation CAT = Main.id("textures/gui/cat_2.png");
    private static final ResourceLocation CAT_SPRITE = Main.id("textures/gui/cat_sprite_2.png");
    private static final ResourceLocation CAT_EYES = Main.id("textures/gui/cat_eyes.png");
    private AnimatedSprite catTail;
    private float eyeDx = 0;
    private float eyeDy = 0;

    public static boolean ignoreHoldToOpenOnce = false;

    public BindsGUI() {
        super(TextUtils.empty());
    }

    protected void init() {
        super.init();
        if (BindsStorage.getBooleanConfig("keepMovement", false)) {
            //? if >=1.21.9 {
            // Window handle = this.minecraft.getWindow();
            //? } else {
            long handle = this.minecraft.getWindow().getWindow();
            //? }
            for (KeyMapping key : /* ? if >=1.18.2 { */ this.minecraft.options.keyMappings /* ?} else { */ /*this.minecraft.options.keyMappings*/ /* ?} */ ) {
                InputConstants.Key bound = key.getDefaultKey();
                int code = bound.getValue();
                if (bound.getType() == InputConstants.Type.KEYSYM && code > 0) {
                    if (InputConstants.isKeyDown(handle, code)) {
                        KeyMapping.set(bound, true);
                    }
                }
            }
        }

        itemButtons.clear();
        currentPage = (BindsStorage.getBooleanConfig("openLastPage", true) ? BindsStorage.getIntConfig("lastPage", 0)
                : 0);
        currentPreset = (BindsStorage.getBooleanConfig("openLastPreset", true)
                ? BindsStorage.getIntConfig("lastPreset", 0)
                : 0);

        // Validate indices
        if (currentPreset >= BindsStorage.presets.size())
            currentPreset = 0;
        if (currentPage >= BindsStorage.presets.get(currentPreset).pages.size())
            currentPage = 0;

        this.textLEFT = "[ "
                + GUIUtils.truncateString(
                    TextUtils.translatable(KeyBinds.PREV_PAGE.getTranslatedKeyMessage().getString()).getString(), 4)
                + " ]";
        this.textRIGHT = "[ "
                + GUIUtils.truncateString(
                    TextUtils.translatable(KeyBinds.NEXT_PAGE.getTranslatedKeyMessage().getString()).getString(), 4)
                + " ]";
        this.leftArrow = TextUtils.literal(textLEFT).withStyle(ChatFormatting.AQUA);
        this.rightArrow = TextUtils.literal(textRIGHT).withStyle(ChatFormatting.AQUA);
        this.centerX = (this.width - MENU_WIDTH) / 2;
        this.centerY = (this.height - 106) / 2 - 21;

        AbstractWidget configBtn = GUIUtils.createTexturedBtn(centerX + 57, centerY + 93, 10, 10,
                new ResourceLocation[] { EDIT_BTN, EDIT_BTN_HOVER }, b -> {
                    minecraft.setScreen(new BindsEditor(null));
                });

        //? if >=1.17 {
        this.addRenderableWidget(configBtn);
        //?} else {
        /*this.addButton(configBtn);*/
        //?}

        this.catTail = new AnimatedSprite(14, 12, CAT_SPRITE, 0, false, 0, 0, 490, 14, 60, 504, 12);
        if (BindsStorage.getBooleanConfig("easterEgg", false)) {
            this.catTail.setLoop(true);
            this.catTail.setPosition(centerX + 101, centerY + 9);
            this.catTail.startAnimation(true);
        }

        generateButtons(centerX + 3, centerY + 27);
    }

    @Override
    public void render(
            //? if >=1.20 {
            GuiGraphics ctx
            //?} else {
            /*PoseStack ctx*/
            //?}
            , int mouseX, int mouseY, float delta) {
        if (!this.checkForClose()) {
            GUIUtils.drawResizableBox(ctx, MENU_BG, centerX, centerY, MENU_WIDTH, 106, 3, 7);
            GUIUtils.drawResizableBox(ctx, MENU_HEADER, centerX, centerY, MENU_WIDTH, 22, 4, 9);

            GUIUtils.addText(ctx, TextUtils.literal(displayText), 0, centerX + 5, centerY + 7);

            int totalPages = BindsStorage.presets.get(currentPreset).pages.size();
            GUIUtils.addText(ctx,
                    TextUtils.literal((currentPage + 1) + "/" + totalPages).withStyle(ChatFormatting.GRAY), MENU_WIDTH,
                    centerX + MENU_WIDTH - 5, centerY + 7, "right", "top");

            int lAC = 0xFFFFFFFF;
            int rAC = 0xFFFFFFFF;
            if (isInside(mouseX, mouseY, centerX + 3, centerY + 92, 51, 12))
                lAC = 0xFFE7BC1C;
            if (isInside(mouseX, mouseY, centerX + 70, centerY + 92, 51, 12))
                rAC = 0xFFE7BC1C;

            GUIUtils.addText(ctx, leftArrow, 125, centerX + 52, centerY + 94, "right", "top");
            GUIUtils.addText(ctx, rightArrow, 125, centerX + 72, centerY + 94, "left", "top");

            GUIUtils.addText(ctx, TextUtils.literal("⏴"), 125, centerX + 48 - font.width(textLEFT), centerY + 94,
                    "right", "top", lAC);
            GUIUtils.addText(ctx, TextUtils.literal("⏵"), 125, centerX + 76 + font.width(textRIGHT), centerY + 94,
                    "left", "top", rAC);

            // Пресеты
            int lC = 0xFFFFFFFF;
            int rC = 0xFFFFFFFF;
            if (isInside(mouseX, mouseY, centerX, centerY - 20, 22, 18))
                lC = 0xFFE7BC1C;
            if (isInside(mouseX, mouseY, centerX + MENU_WIDTH - 22, centerY - 20, 22, 18))
                rC = 0xFFE7BC1C;

            GUIUtils.drawResizableBox(ctx, MENU_BG, centerX, centerY - 20, MENU_WIDTH, 18, 3, 7);
            GUIUtils.addText(ctx, TextUtils.literal("⏴"), 0, centerX + 5, centerY - 15, lC);
            GUIUtils.addText(ctx, TextUtils.literal("⏵"), MENU_WIDTH, centerX + MENU_WIDTH - 5, centerY - 15, "right",
                    "top", rC);

            String full = BindsStorage.presets.get(currentPreset).name;

            if (full.length() < 15) {
                GUIUtils.addText(ctx, TextUtils.literal(full).withStyle(ChatFormatting.AQUA), MENU_WIDTH, centerX,
                        centerY - 15, "center", "top");
            } else {
                GUIUtils.addText(ctx,
                        TextUtils.literal(GUIUtils.truncateString(full, 15)).withStyle(ChatFormatting.AQUA), MENU_WIDTH,
                        centerX + 22, centerY - 15);
            }

            if (BindsStorage.getBooleanConfig("easterEgg", false)) {
                GUIUtils.matricesUtil(ctx, 0, 0, 5, () -> {
                    GUIUtils.adaptiveDrawTexture(ctx, CAT, centerX + 77, centerY, 0, 0, 35, 26, 35, 26);
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

                    GUIUtils.matricesUtil(ctx, eyeDx, eyeDy, 0, () -> {
                        GUIUtils.adaptiveDrawTexture(ctx, CAT_EYES,
                                eyeCenterX, eyeCenterY,
                                0, 0, 8, 6, 8, 6);
                    });

                    catTail.render(ctx);
                });
            }

            super.render(ctx, mouseX, mouseY, delta);
        }
    }

    //? if >=1.20 {
    //? if >=1.21 {
    @Override
    //? }
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
    }
    //?}

    public static int getCurrentPreset() {
        return currentPreset;
    }

    public void generateButtons(int startX, int startY) {
        for (ItemButton button : itemButtons) {
            //? if >=1.17 {
            this.removeWidget(button);
            //?} else {
            /*this.children().remove(button);
            this.buttons.remove(button);*/
            //?}
        }
        itemButtons.clear();
        int currentX = startX;
        int currentY = startY;

        if (currentPreset < BindsStorage.presets.size()) {
            Preset preset = BindsStorage.presets.get(currentPreset);
            if (currentPage < preset.pages.size()) {
                Page page = preset.pages.get(currentPage);
                for (int row = 0; row < page.binds.size() && row < 8; row++) {
                    Bind currentBind = page.binds.get(row);
                    ItemButton button = createBtn(GUIUtils.truncateString(currentBind.name, 12),
                            ItemsMapper.getItemStack(currentBind.icon), currentX, currentY, currentBind);
                    itemButtons.add(button);
                    //? if >=1.17 {
                    this.addRenderableWidget(button);
                    //?} else {
                    /*this.addButton(button);*/
                    //?}
                    currentX += 31;
                    if (row == 3) {
                        currentX = startX;
                        currentY = startY + 31;
                    }
                }
            }
        }
    }

    private ItemButton createBtn(String text, ItemStack icon, int x, int y, Bind bind) {
        return new ItemButton(x, y, icon, () -> {
            BindExecutor.startBind(bind);
            if (BindsStorage.getBooleanConfig("closeOnAction", false)) {
                BindsGUI.ignoreHoldToOpenOnce = true;
                minecraft.setScreen(null);
            }
        }, ITEMS_SELECTOR, "") {
            //? if >1.20.2 {
            @Override
            public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
                super.renderWidget(ctx, mouseX, mouseY, delta);
                if (this.isHovered) {
                    displayText = text;
                    if (text.isEmpty())
                        displayText = TextUtils.translatable("nitsha.binds.empty").getString();
                }
            }
            //?} else if >=1.20 {
            /*
             @Override
             public void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float
             delta) {
             super.renderWidget(ctx, mouseX, mouseY, delta);
             if (this.isHovered) {
             displayText = text;
             if (text.isEmpty()) displayText =
             TextUtils.translatable("nitsha.binds.empty").getString();
             }
             }
             */
            //?} else if >=1.19.4 {
            /*
             @Override
             public void renderWidget(PoseStack ctx, int mouseX, int mouseY, float delta)
             {
             super.renderWidget(ctx, mouseX, mouseY, delta);
             if (this.isHovered) {
             displayText = text;
             if (text.isEmpty()) displayText =
             TextUtils.translatable("nitsha.binds.empty").getString();
             }
             }
             *///?} else {
            /*
             @Override
             public void renderButton(PoseStack ctx, int mouseX, int mouseY, float delta)
             {
             super.renderButton(ctx, mouseX, mouseY, delta);
             if (this.isHovered) {
             displayText = text;
             if (text.isEmpty()) displayText =
             TextUtils.translatable("nitsha.binds.empty").getString();
             }
             }
             *///?}
        };
    }

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

    @Override
    public void onClose() {
        KeyMapping.set(KeyBinds.BINDS.getDefaultKey(), false);
    }

    private boolean checkForClose() {
        if (!BindsStorage.getBooleanConfig("holdToOpen", true))
            return false;
        if (ignoreHoldToOpenOnce) {
            ignoreHoldToOpenOnce = false;
            return false;
        }

        //? if >=1.21.9 {
        // Window handle = this.minecraft.getWindow();
        //? } else {
        long handle = this.minecraft.getWindow().getWindow();
        //? }

        //? if fabric {
        if (!InputConstants.isKeyDown(handle,
                KeyBindingHelper.getBoundKeyOf(KeyBinds.BINDS).getValue())) {
            //?} else {
            /*
            if (!InputConstants.isKeyDown(handle,
            KeyBinds.BINDS.getDefaultKey().getValue())) {
             *///?}
            minecraft.setScreen(null);
            return true;
        }
        return false;
    }

    private void updatePage(int dir) {
        int totalPages = BindsStorage.presets.get(currentPreset).pages.size();
        if (dir == -1 && currentPage == 0)
            currentPage = totalPages;
        currentPage += dir;
        if (currentPage == totalPages)
            currentPage = 0;
        BindsStorage.setConfig("lastPreset", currentPreset);
        BindsStorage.setConfig("lastPage", currentPage);
        generateButtons(centerX + 3, centerY + 27);
    }

    private void updatePreset(int dir) {
        int totalPresets = BindsStorage.presets.size();
        if (dir == -1 && currentPreset == 0)
            currentPreset = totalPresets;
        currentPreset += dir;
        if (currentPreset == totalPresets)
            currentPreset = 0;
        currentPage = 0;
        BindsStorage.setConfig("lastPreset", currentPreset);
        BindsStorage.setConfig("lastPage", currentPage);
        generateButtons(centerX + 3, centerY + 27);
    }

    private void setPreset(int index) {
        if (index >= 0 && index < BindsStorage.presets.size()) {
            currentPreset = index;
            currentPage = 0;
            BindsStorage.setConfig("lastPreset", currentPreset);
            BindsStorage.setConfig("lastPage", currentPage);
            generateButtons(centerX + 3, centerY + 27);
        }
    }

    public static boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private boolean handleClick(double mouseX, double mouseY, int x, int y, int width, int height, Runnable action) {
        if (isInside(mouseX, mouseY, x, y, width, height)) {
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            action.run();
            return true;
        }
        return false;
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (handleClick(mouseX, mouseY, centerX + 3, centerY + 92, 51, 12, () -> updatePage(-1)))
            return true;
        if (handleClick(mouseX, mouseY, centerX + 70, centerY + 92, 51, 12, () -> updatePage(1)))
            return true;
        if (handleClick(mouseX, mouseY, centerX, centerY - 20, 22, 18, () -> updatePreset(-1)))
            return true;
        if (handleClick(mouseX, mouseY, centerX + MENU_WIDTH - 22, centerY - 20, 22, 18, () -> updatePreset(1)))
            return true;

        return super.mouseClicked(event, bl);
    }*/
    //? } else {
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (handleClick(mouseX, mouseY, centerX + 3, centerY + 92, 51, 12, () -> updatePage(-1)))
                return true;
            if (handleClick(mouseX, mouseY, centerX + 70, centerY + 92, 51, 12, () -> updatePage(1)))
                return true;
            if (handleClick(mouseX, mouseY, centerX, centerY - 20, 22, 18, () -> updatePreset(-1)))
                return true;
            if (handleClick(mouseX, mouseY, centerX + MENU_WIDTH - 22, centerY - 20, 22, 18, () -> updatePreset(1)))
                return true;

            return super.mouseClicked(mouseX, mouseY, button);
        }
    //? }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isInside(mouseX, mouseY, centerX, centerY - 20, 125, 18)) {
            int direction = verticalAmount > 0 ? -1 : 1;
            updatePreset(direction);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }
        if (isInside(mouseX, mouseY, centerX + 3, centerY + 92, 119, 12)) {
            int direction = verticalAmount > 0 ? -1 : 1;
            updatePage(direction);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    //?} else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (isInside(mouseX, mouseY, centerX, centerY - 20, 125, 18)) {
            int direction = amount > 0 ? -1 : 1;
            updatePreset(direction);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }
        if (isInside(mouseX, mouseY, centerX + 3, centerY + 92, 119, 12)) {
            int direction = amount > 0 ? -1 : 1;
            updatePage(direction);
            Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.6F));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    *///?}

    @Override
    //? if >=1.21.9 {
    /*public boolean keyPressed(KeyEvent event) {
    int keyCode = event.key();
    int scanCode = event.scancode();*/
    //? } else {
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    //? }
        //? if fabric {
        int bindKey = KeyBindingHelper.getBoundKeyOf(KeyBinds.BINDS).getValue();
        //?} else {
        /*
         int bindKey = KeyBinds.BINDS.getDefaultKey().getValue();
         *///?}

        if ((keyCode == bindKey && !BindsStorage.getBooleanConfig("holdToOpen", true) && !ignoreHoldToOpenOnce) ||
                keyCode == GLFW.GLFW_KEY_ESCAPE) {
            ignoreHoldToOpenOnce = false;
            minecraft.setScreen(null);
            return true;
        }

        //? if fabric {
        if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.PREV_PAGE).getValue()) {
            //?} else {
            /*
             if (keyCode == KeyBinds.PREV_PAGE.getDefaultKey().getValue()) {
             *///?}
            updatePage(-1);
            return true;
        }

        //? if fabric {
        if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.NEXT_PAGE).getValue()) {
            //?} else {
            /*
             if (keyCode == KeyBinds.NEXT_PAGE.getDefaultKey().getValue()) {
             *///?}
            updatePage(1);
            return true;
        }

        if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_9) {
            int index = keyCode - GLFW.GLFW_KEY_1;
            setPreset(index);
            return true;
        }

        if (BindsStorage.getBooleanConfig("keepMovement", false)) {
            //? if >=1.21.9 {
            /*if (this.minecraft.options != null && this.minecraft.getWindow() != null) {
                if (this.minecraft.options.keyUp.matches(event)
                        || this.minecraft.options.keyDown.matches(event)
                        || this.minecraft.options.keyLeft.matches(event)
                        || this.minecraft.options.keyRight.matches(event)
                        || this.minecraft.options.keyJump.matches(event)
                        || this.minecraft.options.keySprint.matches(event)
                        || this.minecraft.options.keyShift.matches(event)) {
                    InputConstants.Key key = InputConstants.getKey(event);
                    KeyMapping.set(key, true);
                    return true;
                }
            }*/
            //? } else {
            if (this.minecraft.options != null && this.minecraft.getWindow() != null) {
                if (this.minecraft.options.keyUp.matches(keyCode, scanCode)
                        || this.minecraft.options.keyDown.matches(keyCode, scanCode)
                        || this.minecraft.options.keyLeft.matches(keyCode, scanCode)
                        || this.minecraft.options.keyRight.matches(keyCode, scanCode)
                        || this.minecraft.options.keyJump.matches(keyCode, scanCode)
                        || this.minecraft.options.keySprint.matches(keyCode, scanCode)
                        || this.minecraft.options.keyShift.matches(keyCode, scanCode)) {
                    InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
                    KeyMapping.set(key, true);
                    return true;
                }
            }
            //? }
        }
        //? if >=1.21.9 {
        // return super.keyPressed(event);
        //? } else {
        return super.keyPressed(keyCode, scanCode, modifiers);
        //? }
    }

    @Override
    //? if >=1.21.9 {
    /*public boolean keyReleased(KeyEvent event) {
        if (BindsStorage.getBooleanConfig("keepMovement", false)) {
            InputConstants.Key key = InputConstants.getKey(event);
            KeyMapping.set(key, false);
        }
        return super.keyReleased(event);
    }*/
    //? } else {
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            if (BindsStorage.getBooleanConfig("keepMovement", false)) {
                InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
                KeyMapping.set(key, false);
            }
            return super.keyReleased(keyCode, scanCode, modifiers);
        }
    //? }
}