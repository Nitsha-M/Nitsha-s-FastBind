package com.nitsha.binds.gui.screen;

import com.nitsha.binds.MainClass;
import com.nitsha.binds.configs.BindEntry;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.gui.utils.AnimatedSprite;
import com.nitsha.binds.utils.BindExecutor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.widget.ItemButton;
import com.nitsha.binds.gui.utils.TextUtils;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
//? if >=1.20 {
import net.minecraft.client.gui.DrawContext;
//? } else {
/*import net.minecraft.client.gui.DrawableHelper;
 *///? }
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BindsGUI extends Screen {
    // Textures
    private static final Identifier EDIT_BTN = MainClass.idSprite("edit");
    private static final Identifier EDIT_BTN_HOVER = MainClass.idSprite("edit_hover");
    private static final Identifier ITEMS_SELECTOR = MainClass.id("textures/gui/test/items_3.png");
    private static final Identifier MENU_BG = MainClass.id("textures/gui/test/menu_bg.png");
    private static final Identifier MENU_HEADER = MainClass.id("textures/gui/test/menu_header.png");

    private final List<ItemButton> buttons = new ArrayList<>();
    private String displayText = TextUtils.translatable("nitsha.binds.list").getString();
    private int centerX;
    private int centerY;
    private static int currentPage;
    private final int MENU_WIDTH = 125;

    private String textLEFT, textRIGHT;
    private Text leftArrow, rightArrow;

    public static int currentPreset;
    private final List<String> presetNames = (List<String>) BindsConfig.configs.get("presets");

    private static final Identifier CAT = MainClass.id("textures/gui/cat_2.png");
    private static final Identifier CAT_SPRITE = MainClass.id("textures/gui/cat_sprite_2.png");
    private static final Identifier CAT_EYES = MainClass.id("textures/gui/cat_eyes.png");
    private AnimatedSprite catTail;
    private float eyeDx = 0;
    private float eyeDy = 0;

    public static boolean ignoreHoldToOpenOnce = false;

    public BindsGUI() {
        super(NarratorManager.EMPTY);
    }

    protected void init() {
        super.init();
        if (BindsConfig.getBooleanConfig("keepMovement", false)) {
            long handle = this.client.getWindow().getHandle();
            for (KeyBinding key : /*? if >=1.18.2 {*/ this.client.options.allKeys /*? } else {*/ /*this.client.options.keysAll*/ /*? }*/ ) {
                InputUtil.Key bound = key.getDefaultKey();
                int code = bound.getCode();
                if (bound.getCategory() == InputUtil.Type.KEYSYM && code > 0) {
                    if (InputUtil.isKeyPressed(handle, code)) {
                        KeyBinding.setKeyPressed(bound, true);
                    }
                }
            }
        }

        buttons.clear();
        currentPage = (BindsConfig.getBooleanConfig("openLastPage", true) ? BindsConfig.getIntConfig("lastPage", 0) : 0);
        currentPreset = (BindsConfig.getBooleanConfig("openLastPreset", true) ? BindsConfig.getIntConfig("lastPreset", 0) : 0);
        this.textLEFT = "[ " +  GUIUtils.truncateString(TextUtils.translatable(KeyBinds.PREV_PAGE.getBoundKeyTranslationKey()).getString(), 4) + " ]";
        this.textRIGHT = "[ " + GUIUtils.truncateString(TextUtils.translatable(KeyBinds.NEXT_PAGE.getBoundKeyTranslationKey()).getString(), 4) + " ]";
        this.leftArrow = TextUtils.literal(textLEFT).styled(style -> style.withColor(Formatting.AQUA));
        this.rightArrow = TextUtils.literal(textRIGHT).styled(style -> style.withColor(Formatting.AQUA));
        this.centerX = (this.width - MENU_WIDTH) / 2;
        this.centerY = (this.height - 106) / 2 - 21;

        PressableWidget configBtn = GUIUtils.createTexturedBtn(centerX + 57, centerY + 93, 10, 10, new Identifier[]{EDIT_BTN, EDIT_BTN_HOVER}, b -> {
            assert client != null;
            //? if >=1.17.1 {
            client.setScreen(new BindsEditor(null));
            //? } else {
            /*client.openScreen(new BindsEditor(null));*/
            //? }
        });

        //? if >=1.17 {
        this.addDrawableChild(configBtn);
        //? } else {
//        this.addButton(configBtn);
        //? }

        this.catTail = new AnimatedSprite(14, 12, CAT_SPRITE, 0, false, 0, 0, 490, 14, 60, 504, 12);
        if (BindsConfig.getBooleanConfig("easterEgg", false)) {
            this.catTail.setLoop(true);
            this.catTail.setPosition(centerX + 101, centerY + 9);
            this.catTail.startAnimation(true);
        }

        generateButtons(centerX + 3, centerY + 27);
    }

    @Override
    public void render(
            //? if >=1.20 {
            DrawContext ctx
            //? } else {
            /*MatrixStack ctx
             *///? }
            , int mouseX, int mouseY, float delta) {
        if (!this.checkForClose()) {
            GUIUtils.drawResizableBox(ctx, MENU_BG, centerX, centerY, MENU_WIDTH, 106, 3, 7);
            GUIUtils.drawResizableBox(ctx, MENU_HEADER, centerX, centerY, MENU_WIDTH, 22, 4, 9);

            GUIUtils.addText(ctx, Text.of(displayText), 0, centerX + 5, centerY + 7);
            GUIUtils.addText(ctx, TextUtils.literal((currentPage + 1) + "/5").styled(style -> style.withColor(Formatting.GRAY)), MENU_WIDTH, centerX + MENU_WIDTH - 5, centerY + 7, "right", "top");

            int lAC = 0xFFFFFFFF;
            int rAC = 0xFFFFFFFF;
            if (isInside(mouseX, mouseY, centerX + 3, centerY + 92, 51, 12)) lAC = 0xFFE7BC1C;
            if (isInside(mouseX, mouseY, centerX + 70, centerY + 92, 51, 12)) rAC = 0xFFE7BC1C;

            GUIUtils.addText(ctx, leftArrow, 125, centerX + 52, centerY + 94, "right", "top");
            GUIUtils.addText(ctx, rightArrow, 125, centerX + 72, centerY + 94, "left", "top");

            GUIUtils.addText(ctx, Text.of("⏴"), 125, centerX + 48 - textRenderer.getWidth(textLEFT), centerY + 94, "right", "top", lAC);
            GUIUtils.addText(ctx, Text.of("⏵"), 125, centerX + 76 + textRenderer.getWidth(textRIGHT), centerY + 94, "left", "top", rAC);

            // Пресеты
            int lC = 0xFFFFFFFF;
            int rC = 0xFFFFFFFF;
            if (isInside(mouseX, mouseY, centerX, centerY - 20, 22, 18)) lC = 0xFFE7BC1C;
            if (isInside(mouseX, mouseY, centerX + MENU_WIDTH - 22, centerY - 20, 22, 18)) rC = 0xFFE7BC1C;

            GUIUtils.drawResizableBox(ctx, MENU_BG, centerX, centerY - 20, MENU_WIDTH, 18, 3, 7);
            GUIUtils.addText(ctx, Text.of("⏴"), 0, centerX + 5, centerY - 15, lC);
            GUIUtils.addText(ctx, Text.of("⏵"), MENU_WIDTH, centerX + MENU_WIDTH - 5, centerY - 15, "right", "top", rC);

            String full = presetNames.get(currentPreset);

            if (full.length() < 15) {
                GUIUtils.addText(ctx, TextUtils.literal(full).styled(style -> style.withColor(Formatting.AQUA)), MENU_WIDTH, centerX, centerY - 15, "center", "top");
            } else {
                GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.truncateString(full, 15)).styled(style -> style.withColor(Formatting.AQUA)), MENU_WIDTH, centerX + 22, centerY - 15);
            }

            if (BindsConfig.getBooleanConfig("easterEgg", false)) {
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
                                0, 0, 8, 6, 8, 6
                        );
                    });

                    catTail.render(ctx);
                });
            }

            super.render(ctx, mouseX, mouseY, delta);
        }
    }

    //? if >=1.20 {
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) { }
    //? }
    public static int getCurrentPreset() {
        return currentPreset;
    }

    public void generateButtons(int startX, int startY) {
        for (ItemButton button : buttons) {
            //? if >=1.17 {
            this.remove(button);
            //? } else {
            //this.children().remove(button);
            //? }
        }
        buttons.clear();
        int currentX = startX;
        int currentY = startY;
        for (int row = 0; row < 8; row++) {
            BindEntry currentBind = BindsConfig.getBind(currentPreset, row + (8 * currentPage));
            ItemButton button = createBtn(GUIUtils.truncateString(currentBind.name, 12), ItemsMapper.getItemStack(currentBind.icon), currentX, currentY, currentBind);
            buttons.add(button);
            //? if >=1.17 {
            this.addDrawableChild(button);
            //? } else {
            //this.addButton(button);
            //? }
            currentX += 31;
            if (row == 3) {
                currentX = startX;
                currentY = startY + 31;
            }
        }
    }

    private ItemButton createBtn(String text, ItemStack icon, int x, int y, BindEntry bind) {
        return new ItemButton(x, y, icon, () -> {
            BindExecutor.startBind(bind);
            if (BindsConfig.getBooleanConfig("closeOnAction", false)) {
                BindsGUI.ignoreHoldToOpenOnce = true;
                //? if >=1.17.1 {
                client.setScreen(null);
                //? } else {
                /*client.openScreen(null);*/
                //? }
            }
        }, ITEMS_SELECTOR, "")  {
            //? if >1.20.2 {
            @Override
            public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
                super.renderWidget(ctx, mouseX, mouseY, delta);
                if (this.isHovered()) {
                    displayText = text;
                    if (text.isEmpty()) displayText = TextUtils.translatable("nitsha.binds.empty").getString();
                }
            }
            //? } else if >=1.20 {
            /*@Override
            public void renderButton(DrawContext ctx, int mouseX, int mouseY, float delta) {
                super.renderButton(ctx, mouseX, mouseY, delta);
                if (this.isHovered()) {
                    displayText = text;
                    if (text.isEmpty()) displayText = TextUtils.translatable("nitsha.binds.empty").getString();
                }
            }
            *///? } else {
            /*@Override
            public void renderButton(MatrixStack ctx, int mouseX, int mouseY, float delta) {
                super.renderButton(ctx, mouseX, mouseY, delta);
                if (this.isHovered()) {
                    displayText = text;
                    if (text.isEmpty()) displayText = TextUtils.translatable("nitsha.binds.empty").getString();
                }
            }
            *///? }
        };
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

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

    //? if >=1.18.2 {
    @Override
    public void close() {
        KeyBinding.setKeyPressed(KeyBinds.BINDS.getDefaultKey(), false);
    }
    //? } else {
    /*@Override
    public void onClose() {
        KeyBinding.setKeyPressed(KeyBinds.BINDS.getDefaultKey(), false);
    }*/
    //? }

    private boolean checkForClose() {
        if (!BindsConfig.getBooleanConfig("holdToOpen", true)) return false;
        if (ignoreHoldToOpenOnce) {
            ignoreHoldToOpenOnce = false;
            return false;
        }
        if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), KeyBindingHelper.getBoundKeyOf(KeyBinds.BINDS).getCode())) {
            //? if >=1.17.1 {
            client.setScreen((Screen)null);
            //? } else {
            /*client.openScreen((Screen)null);*/
            //? }
            return true;
        }
        return false;
    }

    private void updatePage(int dir) {
        if (dir == -1 && currentPage == 0) currentPage = 5;
        currentPage += dir;
        if (currentPage == 5) currentPage = 0;
        BindsConfig.setConfig("lastPreset", currentPreset);
        BindsConfig.setConfig("lastPage", currentPage);
        generateButtons(centerX + 3, centerY + 27);
    }

    private void updatePreset(int dir) {
        if (dir == -1 && currentPreset == 0) currentPreset = 8;
        currentPreset += dir;
        if (currentPreset == 8) currentPreset = 0;
        currentPage = 0;
        BindsConfig.setConfig("lastPreset", currentPreset);
        BindsConfig.setConfig("lastPage", currentPage);
        generateButtons(centerX + 3, centerY + 27);
    }

    private void setPreset(int index) {
        currentPreset = index;
        currentPage = 0;
        BindsConfig.setConfig("lastPreset", currentPreset);
        BindsConfig.setConfig("lastPage", currentPage);
        generateButtons(centerX + 3, centerY + 27);
    }

    public static boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private boolean handleClick(double mouseX, double mouseY, int x, int y, int width, int height, Runnable action) {
        if (isInside(mouseX, mouseY, x, y, width, height)) {
            MinecraftClient.getInstance().getSoundManager().play(
                    PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F)
            );
            action.run();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (handleClick(mouseX, mouseY, centerX + 3, centerY + 92, 51, 12, () -> updatePage(-1))) return true;
        if (handleClick(mouseX, mouseY, centerX + 70, centerY + 92, 51, 12, () -> updatePage(1))) return true;
        if (handleClick(mouseX, mouseY, centerX, centerY - 20, 22, 18, () -> updatePreset(-1))) return true;
        if (handleClick(mouseX, mouseY, centerX + MENU_WIDTH - 22, centerY - 20, 22, 18, () -> updatePreset(1))) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.BINDS).getCode() && !BindsConfig.getBooleanConfig("holdToOpen", true) && !ignoreHoldToOpenOnce) ||
             keyCode == GLFW.GLFW_KEY_ESCAPE) {
            ignoreHoldToOpenOnce = false;
            //? if >=1.17.1 {
            client.setScreen((Screen)null);
            //? } else {
            /*client.openScreen((Screen)null);*/
            //? }
            return true;
        }
        if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.PREV_PAGE).getCode()) {
            updatePage(-1);
            return true;
        }
        if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.NEXT_PAGE).getCode()) {
            updatePage(1);
            return true;
        }
        if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_9) {
            int index = keyCode - GLFW.GLFW_KEY_1;
            setPreset(index);
            return true;
        }
        if (BindsConfig.getBooleanConfig("keepMovement", false)) {
            //? if >=1.18.2 {
            if (this.client.options != null && this.client.getWindow() != null) {
                if (this.client.options.forwardKey.matchesKey(keyCode, scanCode)
                        || this.client.options.backKey.matchesKey(keyCode, scanCode)
                        || this.client.options.leftKey.matchesKey(keyCode, scanCode)
                        || this.client.options.rightKey.matchesKey(keyCode, scanCode)
                        || this.client.options.jumpKey.matchesKey(keyCode, scanCode)
                        || this.client.options.sprintKey.matchesKey(keyCode, scanCode)
                        || this.client.options.sneakKey.matchesKey(keyCode, scanCode)) {
                    InputUtil.Key key = InputUtil.fromKeyCode(keyCode, scanCode);
                    KeyBinding.setKeyPressed(key, true);
                    return true;
                }
            }
            //? } else {
            /*if (this.client.options != null && this.client.getWindow() != null) {
                if (this.client.options.keyForward.matchesKey(keyCode, scanCode)
                        || this.client.options.keyBack.matchesKey(keyCode, scanCode)
                        || this.client.options.keyLeft.matchesKey(keyCode, scanCode)
                        || this.client.options.keyRight.matchesKey(keyCode, scanCode)
                        || this.client.options.keyJump.matchesKey(keyCode, scanCode)
                        || this.client.options.keySprint.matchesKey(keyCode, scanCode)
                        || this.client.options.keySneak.matchesKey(keyCode, scanCode)) {
                    InputUtil.Key key = InputUtil.fromKeyCode(keyCode, scanCode);
                    KeyBinding.setKeyPressed(key, true);
                    return true;
                }
            }*/
            //? }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (BindsConfig.getBooleanConfig("keepMovement", false)) {
            InputUtil.Key key = InputUtil.fromKeyCode(keyCode, scanCode);
            KeyBinding.setKeyPressed(key, false);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
}