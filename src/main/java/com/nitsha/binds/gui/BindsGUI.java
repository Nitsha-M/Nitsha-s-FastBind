package com.nitsha.binds.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.gui.panels.BindsEditor;
import com.nitsha.binds.gui.widget.AnimatedWindow;
import com.nitsha.binds.gui.widget.ItemButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class BindsGUI extends Screen {
    private static final Identifier BUTTONS_TEXTURE = MainClass.id("textures/gui/binds_gui.png");

    // Textures
    //? if >=1.20.2 {
    private static final Identifier EDIT_BTN = MainClass.id("edit");
    private static final Identifier EDIT_BTN_HOVER = MainClass.id("edit_hover");
    //? } else {
    /*private static final Identifier EDIT_BTN = MainClass.id("textures/gui/sprites/edit.png");
    private static final Identifier EDIT_BTN_HOVER = MainClass.id("textures/gui/sprites/edit_hover.png");
    *///? }

    private static final Identifier ITEMS_SELECTOR = MainClass.id("textures/gui/test/items_3.png");
    private static final Identifier MENU_BG = MainClass.id("textures/gui/test/menu_bg.png");
    private static final Identifier MENU_HEADER = MainClass.id("textures/gui/test/menu_header.png");

    private String displayText = Text.translatable("nitsha.binds.list").getString();
    private int centerX;
    private int centerY;
    private static int currentPage = 0;
    private final List<ItemButton> buttons;
    private final int MENU_WIDTH = 125;

    private String textLEFT, textRIGHT;
    private Text leftArrow, rightArrow;

    public static int presetNum = 0;
    private final List<String> presetNames = (List<String>) BindsConfig.configs.get("presets");

    public BindsGUI() {
        super(NarratorManager.EMPTY);
        this.buttons = new ArrayList<>();
    }

    protected void init() {
        super.init();
        buttons.clear();
        this.textLEFT = "[ " +  GUIUtils.truncateString(Text.translatable(KeyBinds.PREV_PAGE.getBoundKeyTranslationKey()).getString(), 4) + " ]";
        this.textRIGHT = "[ " + GUIUtils.truncateString(Text.translatable(KeyBinds.NEXT_PAGE.getBoundKeyTranslationKey()).getString(), 4) + " ]";
        this.leftArrow = Text.literal(textLEFT).styled(style -> style.withColor(Formatting.AQUA));
        this.rightArrow = Text.literal(textRIGHT).styled(style -> style.withColor(Formatting.AQUA));
        this.centerX = (this.width - MENU_WIDTH) / 2;
        this.centerY = (this.height - 106) / 2 - 21;

        TexturedButtonWidget configBtn = GUIUtils.createTexturedBtn(centerX + 57, centerY + 93, 10, 10, new Identifier[]{EDIT_BTN, EDIT_BTN_HOVER}, b -> {
            assert client != null;
            client.setScreen(new BindsEditor());
        });

        this.addDrawableChild(configBtn);

        generateButtons(centerX + 3, centerY + 27);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (!this.checkForClose()) {
            GUIUtils.drawResizableBox(ctx, MENU_BG, centerX, centerY, MENU_WIDTH, 106, 3, 7);
            GUIUtils.drawResizableBox(ctx, MENU_HEADER, centerX, centerY, MENU_WIDTH, 22, 4, 9);

            GUIUtils.addText(ctx, Text.of(displayText), 0, centerX + 5, centerY + 7);
            GUIUtils.addText(ctx, Text.literal((currentPage + 1) + "/5").styled(style -> style.withColor(Formatting.GRAY)), MENU_WIDTH, centerX + MENU_WIDTH - 5, centerY + 7, "right", "top");

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

            String num = (presetNum + 1) + ". ";
            String full = num + presetNames.get(presetNum);

            if (full.length() < 15) {
                GUIUtils.addText(ctx, Text.literal(full).styled(style -> style.withColor(Formatting.AQUA)), MENU_WIDTH, centerX, centerY - 15, "center", "top");
            } else {
                GUIUtils.addText(ctx, Text.literal(GUIUtils.truncateString(full, 15)).styled(style -> style.withColor(Formatting.AQUA)), MENU_WIDTH, centerX + 22, centerY - 15);
            }
            super.render(ctx, mouseX, mouseY, delta);
        }
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    public void generateButtons(int startX, int startY) {
        for (ItemButton button : buttons) {
            this.remove(button);
        }
        buttons.clear();
        int currentX = startX;
        int currentY = startY;
        for (int row = 0; row < 8; row++) {
            String[] currentBind = BindsConfig.getBind(presetNum, row + (8 * currentPage));
            ItemButton button = createBtn(GUIUtils.truncateString(currentBind[0], 12), ItemsMapper.getItemStack(currentBind[1]), currentX, currentY, currentBind[2]);
            buttons.add(button);
            this.addDrawableChild(button);
            currentX += 31;
            if (row == 3) {
                currentX = startX;
                currentY = startY + 31;
            }
        }
    }

    private ItemButton createBtn(String text, ItemStack icon, int x, int y, String command) {
        return new ItemButton(x, y, icon, () -> {
            assert MinecraftClient.getInstance().player != null;
            //? if <1.21.6 {
            if (!command.isEmpty()) MinecraftClient.getInstance().player.networkHandler.sendCommand(command);
            //? } else {
            /*if (!command.isEmpty()) MinecraftClient.getInstance().player.networkHandler.sendChatCommand(command);
            *///? }
        }, ITEMS_SELECTOR, "")  {
            //? if >1.20.2 {
            @Override
            public void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
                super.renderWidget(ctx, mouseX, mouseY, delta);
                if (this.isHovered()) {
                    displayText = text;
                    if (text.isEmpty()) displayText = Text.translatable("nitsha.binds.empty").getString();
                }
            }
            //? } else {
            /*@Override
            public void renderButton(DrawContext ctx, int mouseX, int mouseY, float delta) {
                super.renderButton(ctx, mouseX, mouseY, delta);
                if (this.isHovered()) {
                    displayText = text;
                    if (text.isEmpty()) displayText = Text.translatable("nitsha.binds.empty").getString();
                }
            }
            *///? }
        };
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private boolean checkForClose() {
        if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), KeyBindingHelper.getBoundKeyOf(KeyBinds.BINDS).getCode())) {
            this.client.setScreen((Screen)null);
            return true;
        } else {
            return false;
        }
    }

    private void updatePage(int dir) {
        if (dir == -1 && currentPage == 0) currentPage = 5;
        currentPage += dir;
        if (currentPage == 5) currentPage = 0;
        generateButtons(centerX + 3, centerY + 27);
    }

    private void updatePreset(int dir) {
        if (dir == -1 && presetNum == 0) presetNum = 8;
        presetNum += dir;
        if (presetNum == 8) presetNum = 0;
        currentPage = 0;
        generateButtons(centerX + 3, centerY + 27);
    }

    private void setPreset(int index) {
        presetNum = index;
        currentPage = 0;
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
        if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.PREV_PAGE).getCode()) {
            updatePage(-1);
            return true;
        }
        if (keyCode == KeyBindingHelper.getBoundKeyOf(KeyBinds.NEXT_PAGE).getCode()) {
            updatePage(1);
            return true;
        }
        if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_9) {
            int index = keyCode - GLFW.GLFW_KEY_1; // 0..8
            setPreset(index);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Environment(EnvType.CLIENT)
    public class BindButton extends ClickableWidget {
        private final ItemStack icon;
        private final Runnable onClick;
        private final boolean status;

        public BindButton(int x, int y, ItemStack icon, Runnable onClick, boolean status) {
            super(x, y, 26, 26, Text.empty());
            this.icon = icon;
            this.onClick = onClick;
            this.status = status;
        }

        public boolean isSelected() {
            return super.isSelected();
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (status) this.onClick.run();
        }

        //? if >1.20.2 {
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            this.drawBackground(context);
            context.drawItem(this.icon, this.getX() + 5, this.getY() + 5);
            if (this.isHovered() && status) {
                this.drawSelectionBox(context);
            }
        }
        //? } else {
        /*@Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            this.drawBackground(context);
            context.drawItem(this.icon, this.getX() + 5, this.getY() + 5);
            if (this.isHovered() && status) {
                this.drawSelectionBox(context);
            }
        }
        *///? }

        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }


        private void drawBackground(DrawContext context) {
            GUIUtils.adaptiveDrawTexture(context, BindsGUI.BUTTONS_TEXTURE, this.getX(), this.getY(), 0, 0, 26, 26, 320);
        }

        private void drawSelectionBox(DrawContext context) {
            GUIUtils.adaptiveDrawTexture(context, BindsGUI.BUTTONS_TEXTURE, this.getX(), this.getY(), 26, 0, 26, 26, 320);
        }
    }
}