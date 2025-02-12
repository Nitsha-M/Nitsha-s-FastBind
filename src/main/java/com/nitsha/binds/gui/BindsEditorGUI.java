package com.nitsha.binds.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.MainClass;
import com.nitsha.binds.configs.KeyBinds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;


public class BindsEditorGUI extends Screen {
    private static final Identifier BACKGROUND = MainClass.id("textures/gui/binds_gui.png");
    private final int TEXTURE_WIDTH;
    private final int TEXTURE_HEIGHT;
    private int centerX;
    private int centerY;
    public static boolean isSelectorOpened = false;
    private static int currentPage = 0;
    private List<IconSettingButton> buttons;
    private TextFieldWidget bindNameField;
    private TextFieldWidget commandField;
    public static IconSettingButton editIconBtn;
    public ActionButton pasteBtn;
    public ActionButton nodeController;
    public static String editIconBtnString = "STRUCTURE_VOID";
    private static int activeBind = 0;
    private static String[] copied = {"", "STRUCTURE_VOID", ""};

    // Textures
    private static final Identifier ARROW_L = MainClass.id("arrow_left");
    private static final Identifier ARROW_L_HOVER = MainClass.id("arrow_left_hover");
    private static final Identifier ARROW_R = MainClass.id("arrow_right");
    private static final Identifier ARROW_R_HOVER = MainClass.id("arrow_right_hover");

    public BindsEditorGUI() {
        super(NarratorManager.EMPTY);
        this.TEXTURE_WIDTH = 149;
        this.TEXTURE_HEIGHT = 211;
        this.buttons = new ArrayList<>();
        currentPage = 0;
        activeBind = 0;
        isSelectorOpened = false;
    }

    protected void init() {
        super.init();
        this.centerX = (this.width / 2) - (TEXTURE_WIDTH / 2);
        this.centerY = (this.height - TEXTURE_HEIGHT) / 2;
        generateButtons(centerX + 15, centerY + 34);

        // Кнопки навигации
        this.addDrawableChild(new TexturedButtonWidget(centerX - 7, centerY + 45, 7, 11, new ButtonTextures(ARROW_L, ARROW_L_HOVER), button -> updatePage(-1)));
        this.addDrawableChild(new TexturedButtonWidget(centerX + TEXTURE_WIDTH, centerY + 45, 7, 11, new ButtonTextures(ARROW_R, ARROW_R_HOVER), button -> updatePage(1)));

        // Поля ввода
        this.bindNameField = new TextField(this.textRenderer, centerX + 12, centerY + 115, 95, 20, 20, false,"", Text.translatable("nitsha.binds.name").getString());
        this.commandField = new TextField(this.textRenderer, centerX + 12, centerY + 154, 125, 20, Integer.MAX_VALUE, true,"", Text.translatable("nitsha.binds.command").getString());

        this.addDrawableChild(this.bindNameField);
        this.addDrawableChild(this.commandField);

        // Кнопки управления биндов
        this.editIconBtn = new IconSettingButton(centerX + 111, centerY + 112, ItemsMapper.getItemStack(BindsConfig.getBind(activeBind)[1]), this::openIconsSelector, 1);
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("nitsha.binds.save_bind"), button -> saveBind()).dimensions(centerX + 12, centerY + 179, 59, 20).build());
        this.addDrawableChild(new ActionButton(centerX + 73, centerY + 179, this::deleteBind, 0, 4, 20, 104)).setTooltip(Tooltip.of(Text.translatable("nitsha.binds.delete_bind")));
        this.addDrawableChild(new ActionButton(centerX + 95, centerY + 179, this::copyBind, 1, 3, 20, 104)).setTooltip(Tooltip.of(Text.translatable("nitsha.binds.copy_bind")));

        // Кнопка управления нодами
        this.nodeController = new ActionButton(centerX + 119, centerY + 156, () -> {}, -1, 4, 16, 164);
        this.nodeController.setTooltip(Tooltip.of(Text.translatable("nitsha.binds.wip")));
//        this.nodeController.setEnabledStatus(false);

        // Кнопка вставки бинда
        this.pasteBtn = new ActionButton(centerX + 117, centerY + 179, this::pasteBind, 2, 3, 20, 104);
        this.pasteBtn.setTooltip(Tooltip.of(Text.translatable("nitsha.binds.insert_bind")));
        if (copied[0].isEmpty()) this.pasteBtn.setEnabledStatus(false);

        this.addDrawableChild(editIconBtn);
        this.addDrawableChild(pasteBtn);
        this.addDrawableChild(nodeController);

        this.addDrawableChild(new BindsIconsSelector(centerX + 8, centerY + TEXTURE_HEIGHT + 15, 133, 26));
        selectBind();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        RenderSystem.enableBlend();
        ctx.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, centerX, centerY, 52, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, 320 , 320);
        RenderSystem.disableBlend();

        BindsGUI.addText(ctx, Text.translatable("nitsha.binds.configure"), 149, centerX + 12 + 4, centerY + 14,"left");
        BindsGUI.addText(ctx, Text.literal((currentPage + 1) + "/5").styled(style -> style.withColor(Formatting.GRAY)), 149, centerX - 12 - 4, centerY + 14,"right");
        BindsGUI.addText(ctx, Text.translatable("nitsha.binds.command"), 149, centerX + 12, centerY + 143,"left");

        if (isSelectorOpened) ctx.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, centerX, centerY + TEXTURE_HEIGHT + 5, 52, 211, TEXTURE_WIDTH, 46, 320 , 320);
        super.render(ctx, mouseX, mouseY, delta);
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    private void openIconsSelector() {
        isSelectorOpened = !isSelectorOpened;
        BindsIconsSelector.scrollOffset = 0;
        updateSelected(ItemsMapper.getItemStack(BindsConfig.getBind(activeBind)[1]));
    }

    private void saveBind() {
        String cmd = commandField.getText();
        if (!cmd.isEmpty()) {
            String bindName = (!cmd.isEmpty() && bindNameField.getText().isEmpty()) ? Text.translatable("nitsha.binds.untitled").getString() : bindNameField.getText();
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
            pasteBtn.setEnabledStatus(true);
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
        this.editIconBtn.setIcon(ItemsMapper.getItemStack(BindsConfig.getBind(activeBind)[1]));
        editIconBtnString = currentBind[1];
        BindsIconsSelector.updateButtons(ItemsMapper.getItemStackIndex(BindsConfig.getBind(activeBind)[1]));
    }

    private void updatePage(int dir) {
        currentPage = (currentPage + dir + 5) % 5;
        generateButtons(centerX + 15, centerY + 34);
    }

    private void updateSelected(ItemStack icon) {
        int aB = (int) Math.floor((double) activeBind / 8);
        if (aB == currentPage) buttons.get(activeBind - (8 * aB)).setIcon(icon);
    }

    public void generateButtons(int startX, int startY) {
        for (IconSettingButton button : buttons) {
            this.remove(button);
        }
        buttons.clear();
        int currentX = startX;
        int currentY = startY;
        for (int row = 0; row < 8; row++) {
            int bindIndex = row + (8 * currentPage);
            String[] currentBind = BindsConfig.getBind(bindIndex);
            IconSettingButton[] buttonHolder = new IconSettingButton[1];
            buttonHolder[0] = new IconSettingButton(currentX, currentY, ItemsMapper.getItemStack(currentBind[1]), () -> {
                for (IconSettingButton button : buttons) {
                    button.setSelected(false);
                }
                activeBind = bindIndex;
                buttonHolder[0].setSelected(true);
                BindsIconsSelector.scrollOffset = 0;
                this.selectBind();
            }, 3);
            IconSettingButton button = buttonHolder[0];
            buttons.add(button);
            this.addDrawableChild(button);
            currentX += 31;
            if (row == 3) {
                currentX = startX;
                currentY = startY + 31;
            }
        }
        int aB = (int) Math.floor((double) activeBind / 8);
        if (aB == currentPage) buttons.get(activeBind - (8 * aB)).setSelected(true);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean shouldPause() {
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
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Environment(EnvType.CLIENT)
    public static class IconSettingButton extends ClickableWidget {
        private ItemStack icon;
        private final Runnable onClick;
        private final int type;
        private boolean selected;

        public IconSettingButton(int x, int y, ItemStack icon, Runnable onClick, int type) {
            super(x, y, 26, 26, Text.empty());
            this.icon = icon;
            this.onClick = onClick;
            this.type = type;
            this.selected = false;
        }

        public boolean isSelected() {
            return super.isSelected();
        }

        public void setIcon(ItemStack icon) {
            this.icon = icon;
        }

        public void setSelected(boolean cond) {
            this.selected = cond;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.onClick.run();
        }

        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            this.drawBackground(context);
            context.drawItem(this.icon, this.getX() + 5, this.getY() + 5);
            RenderSystem.enableBlend();
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 200);
            if (this.isHovered()) {
                this.drawSelectionBox(context);
            }
            if (this.selected) context.drawTexture(RenderLayer::getGuiTextured, BindsEditorGUI.BACKGROUND, this.getX(), this.getY(), (type == 2) ? 26 : 0, (type == 2) ? 0 : 78, 26, 26, 320, 320);
            context.getMatrices().pop();
            RenderSystem.disableBlend();
        }

        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }


        private void drawBackground(DrawContext context) {
            context.drawTexture(RenderLayer::getGuiTextured, BindsEditorGUI.BACKGROUND, this.getX(), this.getY(), 0, (type == 3) ? 26 : 0, 26, 26, 320, 320);
        }

        private void drawSelectionBox(DrawContext context) {
            context.drawTexture(RenderLayer::getGuiTextured, BindsEditorGUI.BACKGROUND, this.getX(), this.getY(), 26, type * 26, 26, 26, 320, 320);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class ActionButton extends ClickableWidget {
        private final Runnable onClick;
        private final int type;
        private boolean enabled;
        private final int oT;
        private final int xO;
        private final int size;
        private int offset;

        public ActionButton(int x, int y, Runnable onClick, int type, int oT, int size, int xO) {
            super(x, y, size, size, Text.empty());
            this.onClick = onClick;
            this.type = type;
            this.enabled = true;
            this.oT = oT;
            this.xO = xO;
            this.size = size;
            this.offset = size;
        }

        public boolean isSelected() {
            return super.isSelected();
        }

        public void setEnabledStatus(boolean s) {
            this.enabled = s;
            this.offset = (this.enabled) ? size : 0;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (enabled) this.onClick.run();
        }

        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            this.drawBackground(context);
            RenderSystem.enableBlend();
            if (this.isHovered() && this.enabled) {
                context.drawTexture(RenderLayer::getGuiTextured, BindsEditorGUI.BACKGROUND, this.getX(), this.getY(), 0, xO + (size * 2), size, size, 320, 320);
            }
            if (type >= 0) context.drawTexture(RenderLayer::getGuiTextured, BindsEditorGUI.BACKGROUND, this.getX() + 4, this.getY() + oT, (this.enabled) ? 20 : 32, 104 + (13 * this.type), 12, 13, 320, 320);
            RenderSystem.disableBlend();
        }

        @Override
        public void playDownSound(SoundManager soundManager) {
            if (this.enabled) soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        private void drawBackground(DrawContext context) {
            context.drawTexture(RenderLayer::getGuiTextured, BindsEditorGUI.BACKGROUND, this.getX(), this.getY(), 0, xO + offset, size, size, 320, 320);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class TextField extends TextFieldWidget {
        private boolean drawsBackground = true;
        private boolean trim = false;

        public TextField(TextRenderer textRenderer, int x, int y, int width, int height, int length, boolean trim, String text, String placeholder) {
            super(textRenderer, x, y, width, height, Text.of(text));
            this.setMaxLength(length);
            this.setPlaceholder(Text.of(placeholder));
            this.trim = trim;
        }

        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            super.renderWidget(context, mouseX, mouseY, delta);
            if (this.getText().isEmpty() && !this.isFocused()) {
                this.setEditableColor(0xFFAAAAAA);
            } else {
                this.setEditableColor(0xFFFFFFFF);
            }
        }

        public int getInnerWidth() {
            if (this.trim) return this.drawsBackground() ? this.width - 28 : this.width;
            return this.drawsBackground() ? this.width - 8 : this.width;
        }

        public boolean drawsBackground() {
            return this.drawsBackground;
        }
//
//        private void drawSelectionHighlight(DrawContext context, int x1, int y1, int x2, int y2) {
//            if (x1 < x2) {
//                int i = x1;
//                x1 = x2;
//                x2 = i;
//            }
//
//            if (y1 < y2) {
//                int i = y1;
//                y1 = y2;
//                y2 = i;
//            }
//
//            if (x2 > this.getX() + this.width) {
//                x2 = this.getX() + this.width;
//            }
//
//            if (x1 > this.getX() + this.width) {
//                x1 = this.getX() + this.width;
//            }
//
//            context.fill(RenderLayer.getGuiTextHighlight(), x1, y1, (this.trim) ? x2 - 200 : x2, y2, -16776961);
//        }
    }
}
