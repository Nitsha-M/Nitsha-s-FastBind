package com.nitsha.binds.gui.panels;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.gui.widget.AnimatedWindow;
import com.nitsha.binds.gui.widget.ItemButton;
import com.nitsha.binds.gui.GUIUtils;
import com.nitsha.binds.gui.extend.AnimatedSprite;
import com.nitsha.binds.gui.widget.BedrockButton;
import com.nitsha.binds.gui.widget.BedrockIconButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BindsEditor extends Screen {
    private static final Identifier BACKGROUND = MainClass.id("textures/gui/test/editor_bg.png");
    private static final Identifier BACKGROUND_FLAT = MainClass.id("textures/gui/test/editor_bg_flat.png");

    private static final Identifier BACKGROUND_DARK = MainClass.id("textures/gui/test/editor_bg_dark.png");
    private static final Identifier BACKGROUND_DARK_FLAT = MainClass.id("textures/gui/test/editor_bg_dark_flat.png");

    private static final Identifier FRAME = MainClass.id("textures/gui/test/frame.png");
    private static final Identifier ARROW_SPRITE = MainClass.id("textures/gui/test/arrow_animation.png");
    private static final Identifier ARROW_SPRITE_LEFT = MainClass.id("textures/gui/test/arrow_animation_left.png");
    private static final Identifier ITEMS_SELECTOR = MainClass.id("textures/gui/test/items_2.png");
    private static final Identifier ITEMS_EDIT = MainClass.id("textures/gui/test/items_1.png");

    //? if >=1.20.2 {
    private static final Identifier ARROW_L = MainClass.id("arrow_left");
    private static final Identifier ARROW_L_HOVER = MainClass.id("arrow_left_hover");
    private static final Identifier ARROW_R = MainClass.id("arrow_right");
    private static final Identifier ARROW_R_HOVER = MainClass.id("arrow_right_hover");
    private static final Identifier RANDOM = MainClass.id("random");
    private static final Identifier RANDOM_HOVER = MainClass.id("random_hover");
    //? } else {
    /*private static final Identifier ARROW_L = MainClass.id("textures/gui/sprites/arrow_left.png");
    private static final Identifier ARROW_L_HOVER = MainClass.id("textures/gui/sprites/arrow_left_hover.png");
    private static final Identifier ARROW_R = MainClass.id("textures/gui/sprites/arrow_right.png");
    private static final Identifier ARROW_R_HOVER = MainClass.id("textures/gui/sprites/arrow_right_hover.png");
    private static final Identifier RANDOM = MainClass.id("textures/gui/sprites/random.png");
    private static final Identifier RANDOM_HOVER = MainClass.id("textures/gui/sprites/random_hover.png");
    *///? }

    private final int TEXTURE_WIDTH = 141;
    private final int TEXTURE_HEIGHT = 190;

    private TextFieldWidget bindNameField;
    private TextFieldWidget commandField;

    private BedrockButton saveBtn;
    private BedrockIconButton copyBtn;
    private BedrockIconButton pasteBtn;
    private BedrockIconButton deleteBtn;

    private int centerX;
    private int centerY;

    private boolean selectorOpening = false;
    public static boolean isSelectorOpened = false;
    private static int currentPage = 0;
    private static int activeBind = 0;
    public static ItemButton editIconBtn;
    public static String editIconBtnString = "STRUCTURE_VOID";
    private static String[] copied = {"", "STRUCTURE_VOID", ""};

    private List<ItemButton> buttons;
    private AnimatedSprite rightBtn;
    private AnimatedSprite leftBtn;
    private TexturedButtonWidget leftA;
    private TexturedButtonWidget rightA;
    private TexturedButtonWidget random;

    private AnimatedWindow mainW;
    private AnimatedWindow bindsW;
    private AnimatedWindow iconsH;
    private IconSelector selector;

    private final List<AnimatedWindow> animWindows = new ArrayList<>();

    private final int coeff = 200;

    public BindsEditor() {
        super(NarratorManager.EMPTY);
        this.buttons = new ArrayList<>();
        currentPage = 0;
        activeBind = 0;
    }

    protected void init() {
        super.init();
        animWindows.clear();
        this.centerX = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.centerY = (this.height - TEXTURE_HEIGHT) / 2;

        isSelectorOpened = selectorOpening = false;

        createMainW();
        createBindsW();
        createIconSelector();
        selectBind();
    }

    public void createMainW() {
        this.mainW = new AnimatedWindow(TEXTURE_WIDTH, TEXTURE_HEIGHT, centerX, centerY, BACKGROUND, BACKGROUND_FLAT, 0);
        this.mainW.addDrawElement(drawContext -> {
            Text comText = Text.translatable("nitsha.binds.command");
            int comWidth = (int) textRenderer.getWidth(comText);
            drawContext.drawText(textRenderer, comText, 5, 133, 0xFF212121, false);
            drawContext.drawHorizontalLine(6 + comWidth, 136, 137, 0xFF212121);
        });

        // Поля ввода
        this.bindNameField = new TextField(this.textRenderer, 4, 106, 105, 20, 20,"", Text.translatable("nitsha.binds.name").getString());
        this.commandField = new TextField(this.textRenderer, 4, 142, 133, 20, Integer.MAX_VALUE,"", Text.translatable("nitsha.binds.command_example").getString());

        this.editIconBtn = new ItemButton(111, 103, ItemsMapper.getItemStack(BindsConfig.getBind(activeBind)[1]), this::openIconsSelector, ITEMS_EDIT, "");
        this.saveBtn = new BedrockButton("Save", 4, 164, 67, 20, this::saveBind);
        this.copyBtn = new BedrockIconButton(73, 164, 20, 20, "copy", true, this::copyBind);
        this.pasteBtn = new BedrockIconButton(95, 164, 20, 20, "paste", false, this::pasteBind, 0xFF0569CE, 0xFF0776E6, 0xFFFFFFFF, 0xFFFFFFFF);
        this.deleteBtn = new BedrockIconButton(117, 164, 20, 20, "delete", true, this::deleteBind, 0xFFEF4747, 0xFFFF7272, 0xFFFFFFFF, 0xFFFFFFFF);
        if (copied[0].isEmpty()) this.pasteBtn.setEnabled(false);

        this.mainW.addElement(this.bindNameField);
        this.mainW.addElement(this.commandField);
        this.mainW.addElement(this.editIconBtn);
        this.mainW.addElement(this.saveBtn);
        this.mainW.addElement(this.copyBtn);
        this.mainW.addElement(this.pasteBtn);
        this.mainW.addElement(this.deleteBtn);

        animWindows.add(this.mainW);
        this.mainW.open(() -> {});
        this.addDrawableChild(this.mainW);
    }

    public void createBindsW() {
        this.bindsW = new AnimatedWindow(133, 100, centerX + 4, centerY - 1, BACKGROUND_DARK, BACKGROUND_DARK_FLAT, 40);
        // Кнопки
        this.leftA = GUIUtils.createTexturedBtn(-19, 43, 11, 11, new Identifier[]{ARROW_L, ARROW_L_HOVER}, button -> updatePage(-1));
        this.rightA = GUIUtils.createTexturedBtn(141, 43, 11, 11, new Identifier[]{ARROW_R, ARROW_R_HOVER}, button -> updatePage(1));

        this.rightBtn = new AnimatedSprite(22, 15, ARROW_SPRITE, 0, false, 0, 0, 220, 22, 2, 242, 15);
        this.leftBtn = new AnimatedSprite(22, 15, ARROW_SPRITE_LEFT, 0, false, 0, 0, 220, 22, 2, 242, 15);

        this.bindsW.addDrawElement(drawContext -> {
            GUIUtils.adaptiveDrawTexture(drawContext, FRAME, 4, 4, 0, 0, 125, 90, 125 , 90);
            GUIUtils.addText(drawContext, Text.translatable("nitsha.binds.configure"), 141, 8, 11);
            GUIUtils.addText(drawContext, Text.literal((currentPage + 1) + "/5").styled(style -> style.withColor(Formatting.GRAY)), 133, 125, 11,"right", "top");

            leftBtn.setPosition(-20, 42);
            rightBtn.setPosition(131, 42);
            rightBtn.tick();
            leftBtn.tick();
            rightBtn.render(drawContext);
            leftBtn.render(drawContext);
        });

        this.bindsW.open(() -> {
            this.rightBtn.startAnimation(true);
            this.leftBtn.startAnimation(true);
            this.bindsW.addElement(leftA);
            this.bindsW.addElement(rightA);
        });

        generateButtons(7, 31);

        this.addDrawableChild(this.bindsW);
        animWindows.add(this.bindsW);
    }

    private void createIconSelector() {
        this.iconsH = new AnimatedWindow(coeff - 24, 30, centerX + 61, centerY - 1, BACKGROUND_DARK, BACKGROUND_DARK_FLAT, 20);

        this.random = GUIUtils.createTexturedBtn(coeff - 48, 6, 16, 16, new Identifier[]{RANDOM, RANDOM_HOVER}, button -> {
            IconSelector.pickRandom();
        });
        this.iconsH.addElement(random);
        this.iconsH.addDrawElement(drawCtx -> {
            GUIUtils.addText(drawCtx, Text.of("Icon selector"), 140, 8, 10);
        });
        this.selector = new IconSelector(162, 126, centerX + 63, centerY + 36);
        animWindows.add(this.iconsH);
        this.addDrawableChild(this.iconsH);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        mainW.render(ctx, mouseX, mouseY, delta);
        bindsW.render(ctx, mouseX, mouseY, delta);
        iconsH.render(ctx, mouseX, mouseY, delta);
        this.selector.render(ctx, mouseX, mouseY, delta);
    }

    //? if >=1.20.2 {
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }
    //? }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AnimatedWindow window : animWindows) {
            if (window.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        if (selector.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (AnimatedWindow window : animWindows) {
            if (window.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                return true;
            }
        }

        if (selector.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    //? } else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amout) {
        for (AnimatedWindow window : animWindows) {
            if (window.mouseScrolled(mouseX, mouseY, amout)) {
                return true;
            }
        }

        if (selector.mouseScrolled(mouseX, mouseY, amout)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, amout);
    }
    *///? }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.selector != null && this.selector.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean handled = false;
        if (selector.mouseReleased(mouseX, mouseY, button)) {
            handled = true;
        }

        for (AnimatedWindow window : animWindows) {
            if (window.mouseReleased(mouseX, mouseY, button)) {
                handled = true;
            }
        }

        super.mouseReleased(mouseX, mouseY, button);

        return handled;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (AnimatedWindow window : animWindows) {
            if (window.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return false;
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
        for (AnimatedWindow window : animWindows) {
            if (window.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void openIconsSelector() {
        if (selectorOpening) return;

        selectorOpening = true;

        if (!isSelectorOpened) {
            mainW.updateWidth(coeff);
            bindsW.updateX(coeff / 2);
            this.selector.open(true);
            this.iconsH.open(() -> {
                isSelectorOpened = true;
                selectorOpening = false;
            });
        } else {
            this.selector.open(false);
            this.iconsH.close(() -> {
                mainW.updateWidth(-coeff);
                bindsW.updateX(-coeff / 2);
                isSelectorOpened = false;
                selectorOpening = false;
            });
        }

        updateSelected(ItemsMapper.getItemStack(BindsConfig.getBind(activeBind)[1]));
    }


    private void saveBind() {
        String cmd = commandField.getText();
        if (!cmd.isEmpty()) {
            String bindName = bindNameField.getText().isEmpty() ? Text.translatable("nitsha.binds.untitled").getString() : bindNameField.getText();
            if (editIconBtnString.equals("STRUCTURE_VOID")) editIconBtnString = "GRASS";
            BindsConfig.setBind(activeBind, new String[]{bindName, editIconBtnString, cmd});
            selectBind();
            updateSelected(ItemsMapper.getItemStack(BindsConfig.getBind(activeBind)[1]));
        }
    }

    private void deleteBind() {
        BindsConfig.setBind(activeBind, new String[]{"", "STRUCTURE_VOID", ""});
        selectBind();
        updateSelected(new ItemStack(Items.STRUCTURE_VOID));
    }

    private void copyBind() {
        String[] currentBind = BindsConfig.getBind(activeBind);
        if(!currentBind[0].isEmpty()) {
            copied = currentBind;
            pasteBtn.setEnabled(true);
        }
    }

    private void pasteBind() {
        BindsConfig.setBind(activeBind, copied);
        selectBind();
        updateSelected(ItemsMapper.getItemStack(BindsConfig.getBind(activeBind)[1]));
    }

    private void selectBind() {
        String[] currentBind = BindsConfig.getBind(activeBind);
        this.bindNameField.setText(currentBind[0]);
        this.commandField.setText(currentBind[2]);
        editIconBtn.setIcon(ItemsMapper.getItemStack(BindsConfig.getBind(activeBind)[1]));
        editIconBtnString = currentBind[1];
        IconSelector.updateButtons(currentBind[1]);
    }

    private void updatePage(int dir) {
        currentPage = (currentPage + dir + 5) % 5;
        generateButtons(7, 31);
    }

    private void updateSelected(ItemStack icon) {
        int aB = (int) Math.floor((double) activeBind / 8);
        if (aB == currentPage) buttons.get(activeBind - (8 * aB)).setIcon(icon);
    }

    public void generateButtons(int startX, int startY) {
        bindsW.removeElementsOfType(ItemButton.class);
        buttons.clear();

        int currentX = startX;
        int currentY = startY;

        for (int row = 0; row < 8; row++) {
            int bindIndex = row + (8 * currentPage);
            String[] currentBind = BindsConfig.getBind(bindIndex);

            ItemButton[] buttonHolder = new ItemButton[1];

            buttonHolder[0] = new ItemButton(currentX, currentY, ItemsMapper.getItemStack(currentBind[1]), () -> {
                for (ItemButton btn : buttons) {
                    btn.setSelected(false);
                }
                activeBind = bindIndex;
                buttonHolder[0].setSelected(true);
                this.selectBind();
            }, ITEMS_SELECTOR, "");

            ItemButton button = buttonHolder[0];

            bindsW.addElement(button);
            buttons.add(button);

            currentX += 31;
            if (row == 3) {
                currentX = startX;
                currentY = startY + 31;
            }
        }

        int aB = (int) Math.floor((double) activeBind / 8);
        if (aB == currentPage) {
            buttons.get(activeBind - (8 * aB)).setSelected(true);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class TextField extends TextFieldWidget {
        public TextField(TextRenderer textRenderer, int x, int y, int width, int height, int length, String text, String placeholder) {
            super(textRenderer, x, y, width, height, Text.of(text));
            this.setMaxLength(length);
            this.setPlaceholder(Text.of(placeholder));
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
        //? } else {
        /*public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderButton(context, mouseX, mouseY, delta);
            if (this.getText().isEmpty() && !this.isFocused()) {
                this.setEditableColor(0xFFAAAAAA);
            } else {
                this.setEditableColor(0xFFFFFFFF);
            }
        }
        *///? }
    }
}
