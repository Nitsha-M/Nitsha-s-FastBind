package com.nitsha.binds.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.panels.AdvancedOptions;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
 *///?}
import net.minecraft.client.gui.components.AbstractWidget;
//? if >=1.17 {
import net.minecraft.client.gui.narration.NarrationElementOutput;
//?}
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.List;

public class ActionItem extends AbstractButton {
    private final AbstractWidget topBtn;
    private final AbstractWidget bottomBtn;
    private final AbstractWidget leftBtn;
    private final AbstractWidget rightBtn;
    private final AbstractWidget resetBtn;
    private final AbstractWidget deleteBtn;
    private static final ResourceLocation TOP = Main.idSprite("action_top_normal");
    private static final ResourceLocation TOP_HOVER = Main.idSprite("action_top_hover");
    private static final ResourceLocation BOTTOM = Main.idSprite("action_bottom_normal");
    private static final ResourceLocation BOTTOM_HOVER = Main.idSprite("action_bottom_hover");
    private static final ResourceLocation LEFT = Main.idSprite("action_left_normal");
    private static final ResourceLocation LEFT_HOVER = Main.idSprite("action_left_hover");
    private static final ResourceLocation RIGHT = Main.idSprite("action_right_normal");
    private static final ResourceLocation RIGHT_HOVER = Main.idSprite("action_right_hover");
    private static final ResourceLocation RESET = Main.idSprite("action_reset_normal");
    private static final ResourceLocation RESET_HOVER = Main.idSprite("action_reset_hover");
    private static final ResourceLocation DELETE = Main.idSprite("action_delete_normal");
    private static final ResourceLocation DELETE_HOVER = Main.idSprite("action_delete_hover");
    private static final ResourceLocation ARROW_DISABLED = Main.id("textures/gui/test/arrow_disabled.png");

    private int type, index;

    private final TextField command;
    private final TextField delay;
    private final KeybindSelector keybind;

    private final TextField chatMessage;

    private final TextField titleMessage;
    private final TextField subtitleMessage;

    private final List<SmallTextButton> colorsItem = new ArrayList<>();
    private int colorOffset = 0;
    private static final int VISIBLE_COLORS = 13;

    private int colorButtonsX;
    private int colorButtonsY;
    private int colorButtonsWidth;
    private int colorButtonsHeight = 10;

    Object[][] colorsData = {
            { 0xFF000000, 0, TextUtils.literal("") }, // black
            { 0xFF0001a9, 1, TextUtils.literal("") }, // dark_blue
            { 0xFF04a702, 2, TextUtils.literal("") }, // dark_green
            { 0xFF01a7a9, 3, TextUtils.literal("") }, // dark_aqua
            { 0xFFaa0101, 4, TextUtils.literal("") }, // dark_red
            { 0xFFa501a7, 5, TextUtils.literal("") }, // dark_purple
            { 0xFFfca702, 6, TextUtils.literal("") }, // gold
            { 0xFFa8a8a8, 7, TextUtils.literal("") }, // gray
            { 0xFF545454, 8, TextUtils.literal("") }, // dark_gray
            { 0xFF5254fa, 9, TextUtils.literal("") }, // blue
            { 0xFF55fb53, 10, TextUtils.literal("") }, // green
            { 0xFF50fcf7, 11, TextUtils.literal("") }, // aqua
            { 0xFFfd5357, 12, TextUtils.literal("") }, // red
            { 0xFFf955fb, 13, TextUtils.literal("") }, // light_purple
            { 0xFFfbfd4f, 14, TextUtils.literal("") }, // yellow
            { 0xFFFFFFFF, 15, TextUtils.literal("") }, // white
            { 0xFF316d20, 20, TextUtils.literal("ʙ").setStyle(Style.EMPTY.withBold(true)) }, // bold
            { 0xFF316d20, 21, TextUtils.literal("ɪ").setStyle(Style.EMPTY.withItalic(true)) }, // italic
            { 0xFF316d20, 22, TextUtils.literal("ᴜ").setStyle(Style.EMPTY.withUnderlined(true)) }, // underline
            { 0xFF316d20, 23, TextUtils.literal("s").setStyle(Style.EMPTY.withStrikethrough(true)) }, // strikethrough
            { 0xFF316d20, 24, TextUtils.literal("ᴏ").setStyle(Style.EMPTY.withObfuscated(true)) }, // obfuscated
            { 0xFF316d20, 99, TextUtils.literal("ʀ") }, // reset
    };

    private int x, y;

    public ActionItem(AdvancedOptions parent, int type, int x, int y, int width, int height, Object value, int index) {
        super(x, y, width, height, Component.literal(""));
        this.index = index;
        this.type = type;
        this.x = x;
        this.y = y;

        this.command = new TextField(Minecraft.getInstance().font, x, y + 3, width - 26, 19, Integer.MAX_VALUE, String.valueOf(value),
                TextUtils.translatable("nitsha.binds.advances.actions.commandLine").getString());
        this.delay = new TextField(Minecraft.getInstance().font, x + 90, y + 3, width - 116, 19, 6, String.valueOf(value),
                TextUtils.translatable("nitsha.binds.advances.actions.delayLine").getString(), true);
        this.keybind = new KeybindSelector(x + width - 26 - 68, y + 3, 68, 19);

        this.chatMessage = new TextField(Minecraft.getInstance().font, x, y + 3, width - 26, 19,
                Integer.MAX_VALUE, "", TextUtils.translatable("nitsha.binds.advances.actions.chatMessage").getString());

        int fieldHeight = 19;
        int gap = 2;
        this.titleMessage = new TextField(Minecraft.getInstance().font, x, y + 3, width - 26, fieldHeight,
                Integer.MAX_VALUE, "", TextUtils.translatable("nitsha.binds.advances.actions.titleLine").getString());

        this.subtitleMessage = new TextField(Minecraft.getInstance().font, x, y + 3 + fieldHeight + gap, width, fieldHeight,
                Integer.MAX_VALUE, "", TextUtils.translatable("nitsha.binds.advances.actions.subtitleLine").getString());

        if (this.type == 4 && value instanceof Map) {
            loadFormattedText(this.chatMessage, (Map<String, Object>) value);
        } else if (this.type == 5 && value instanceof Map) {
            Map<String, Object> titleData = (Map<String, Object>) value;
            if (titleData.containsKey("title")) {
                loadFormattedText(this.titleMessage, (Map<String, Object>) titleData.get("title"));
            }
            if (titleData.containsKey("subtitle")) {
                loadFormattedText(this.subtitleMessage, (Map<String, Object>) titleData.get("subtitle"));
            }
        }

        if (this.type == 3)
            this.keybind.setKeyCode(Integer.parseInt(String.valueOf(value)));

        this.topBtn = GUIUtils.createTexturedBtn(x + width - 9, y + 3, 9, 9, new ResourceLocation[] { TOP, TOP_HOVER },
                button -> {
                    if (index > 0)
                        parent.moveAction(index, -1);
                });

        this.bottomBtn = GUIUtils.createTexturedBtn(x + width - 9, y + 13, 9, 9,
                new ResourceLocation[] { BOTTOM, BOTTOM_HOVER }, button -> {
                    if (BindsEditor.getCBind().actions.size() != index + 1)
                        parent.moveAction(index, 1);
                });

        int colorBtnY = (this.type == 5) ? y + 3 + fieldHeight + gap + fieldHeight + gap : y + 24;

        this.colorButtonsX = x + 10;
        this.colorButtonsY = colorBtnY;
        this.colorButtonsWidth = width - 20;
        this.colorButtonsHeight = 10;

        this.leftBtn = GUIUtils.createTexturedBtn(x, y + 24, 9, 9, new ResourceLocation[] { LEFT, LEFT_HOVER },
                button -> {
                    colorOffset = (colorOffset - 1 + colorsData.length) % colorsData.length;
                });

        this.rightBtn = GUIUtils.createTexturedBtn(x + width - 9, y + 24, 9, 9,
                new ResourceLocation[] { RIGHT, RIGHT_HOVER }, button -> {
                    colorOffset = (colorOffset + 1) % colorsData.length;
                });

        if (this.type == 4 || this.type == 5) {
            TextField[] fields = (this.type == 4)
                    ? new TextField[] { this.chatMessage }
                    : new TextField[] { this.titleMessage, this.subtitleMessage };

            for (int i = 0; i < 22; i++) {
                int color = (int) colorsData[i][0];
                int code = (int) colorsData[i][1];
                MutableComponent text = (MutableComponent) colorsData[i][2];
                SmallTextButton item = new SmallTextButton(text, x + 10, colorBtnY, color, 10, "left", () -> {
                    TextField focused = TextField.getFocusedField();
                    if (focused != null) {
                        focused.setStyle(code);
                    }
                });
                colorsItem.add(item);
            }
        }

        for (int i = 0; i < 22; i++) {
            int color = (int) colorsData[i][0];
            int code = (int) colorsData[i][1];
            MutableComponent text = (MutableComponent) colorsData[i][2];
            SmallTextButton item = new SmallTextButton(text, x + 10, y + 24, color, 10, "left", () -> {
                this.chatMessage.setStyle(code);
            });
            colorsItem.add(item);
        }

        this.resetBtn = GUIUtils.createTexturedBtn(x + width - 25, y + 3, 15, 9,
                new ResourceLocation[] { RESET, RESET_HOVER }, button -> {
                    switch (this.type) {
                        case 1:
                            this.command.setText("");
                        break;
                        case 2:
                            this.delay.setText("100");
                        break;
                        case 3:
                            this.keybind.setKeyCode(0);
                            this.keybind.setPressed(false);
                        break;
                        case 4:
                            this.chatMessage.setText("");
                        break;
                        case 5:
                            this.titleMessage.setText("");
                            this.subtitleMessage.setText("");
                        break;
                    }
                });

        this.deleteBtn = GUIUtils.createTexturedBtn(x + width - 25, y + 13, 15, 9,
                new ResourceLocation[] { DELETE, DELETE_HOVER }, button -> {
                    parent.removeAction(index);
                });
    }

    private void loadFormattedText(TextField field, Map<String, Object> data) {
        String text = (String) data.get("text");
        field.setText(text);
        if (data.containsKey("marks")) {
            List<Map<String, Object>> marks = (List<Map<String, Object>>) data.get("marks");
            field.setFormatMarksFromMap(marks);
        }
    }

    private Map<String, Object> saveFormattedText(TextField field) {
        Map<String, Object> data = new HashMap<>();
        data.put("text", field.getText());
        List<Map<String, Integer>> marks = field.getFormatMarksAsMap();
        data.put("marks", marks);
        return data;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Map<String, Object> getValue() {
        Map<String, Object> action = new HashMap<>();

        switch (this.type) {
            case 1:
                action.put("type", "command");
                action.put("value", this.command.getText());
                break;
            case 2:
                action.put("type", "delay");
                try {
                    action.put("value", Integer.parseInt(this.delay.getText()));
                } catch (NumberFormatException e) {
                    action.put("value", 100);
                }
                break;
            case 3:
                action.put("type", "keybind");
                action.put("value", this.keybind.getKeyCode());
                break;
            case 4:
                action.put("type", "chatMessage");
                action.put("value", saveFormattedText(this.chatMessage));
                break;
            case 5:
                action.put("type", "titleMessage");
                Map<String, Object> titleData = new HashMap<>();
                titleData.put("title", saveFormattedText(this.titleMessage));
                titleData.put("subtitle", saveFormattedText(this.subtitleMessage));
                action.put("value", titleData);
                break;
        }
        return action;
    }


    // ? if >1.20.2 {
    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        rndr(context, mouseX, mouseY, delta);
    }
    // ?} else if >=1.20 {
    /*
     @Override
     public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float
     delta) {
     rndr(context, mouseX, mouseY, delta);
     }
     */// ?} else {
    /*
     @Override
     public void renderWidget(PoseStack context, int mouseX, int mouseY, float
     delta) {
     rndr(context, mouseX, mouseY, delta);
     }
     */// ?}

    private void rndr(Object ctx, int mouseX, int mouseY, float delta) {
        // ? if >=1.20 {
        GuiGraphics c = (GuiGraphics) ctx;
        // ?} else {
        /*PoseStack c = (PoseStack) ctx;*/
        // ?}
        int lineColor = 0xFFFFFFFF;
//        GUIUtils.drawFill(c, getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(), 0x33FFFFFF);
        if(index % 2 == 1) GUIUtils.drawFill(c, getX() - 4, getY(), getX() + getWidth() + 4, getY() + getHeight(), 0x4DFFFFFF);
        this.resetBtn.render(c, mouseX, mouseY, delta);
        this.deleteBtn.render(c, mouseX, mouseY, delta);
        GUIUtils.adaptiveDrawTexture(ctx, ARROW_DISABLED, getX() + getWidth() - 9, getY() + 3, 0, 0, 9, 19, 9, 19);
        int aS = BindsEditor.getCBind().actions.size();
        if (aS > 0) {
            if (index > 0)
                this.topBtn.render(c, mouseX, mouseY, delta);
            if (index != (aS - 1))
                this.bottomBtn.render(c, mouseX, mouseY, delta);
        }
        switch (this.type) {
            case 1:
                lineColor = 0xFF4e8605;
                this.command.render(c, mouseX, mouseY, delta);
                break;
            case 2:
                lineColor = 0xFFc99212;
                this.delay.render(c, mouseX, mouseY, delta);
                GUIUtils.addText(c, TextUtils.translatable("nitsha.binds.advances.actions.delayDesс"), 0, getX() + 2,
                        getY() + 8, "top", "left", 0xFF212121, false);
                break;
            case 3:
                lineColor = 0xFF790e06;
                this.keybind.render(c, mouseX, mouseY, delta);
                GUIUtils.addText(c, TextUtils.translatable("nitsha.binds.advances.actions.pressKey"), 0, getX() + 2,
                        getY() + 8, "top", "left", 0xFF212121, false);
                break;
            case 4:
            case 5:
                lineColor = (this.type == 4) ? 0xFF174eff : 0xFF5facfa;

                if (this.type == 4) {
                    this.chatMessage.render(c, mouseX, mouseY, delta);
                } else {
                    this.titleMessage.render(c, mouseX, mouseY, delta);
                    this.subtitleMessage.render(c, mouseX, mouseY, delta);
                }

                int colorBtnY = (this.type == 5) ? getY() + 3 + 19 + 2 + 19 + 2 : getY() + 24;

                for (int i = 0; i < VISIBLE_COLORS; i++) {
                    int actualIndex = (colorOffset + i) % colorsData.length;
                    SmallTextButton item = colorsItem.get(actualIndex);
                    item.setX(this.x + 10 + (i * 11));
                    item.setY(colorBtnY);

                    int itemWidth = (item.isHovered()) ? 12 : 10;
                    int itemHeight = (item.isHovered()) ? 11 : 9;
                    int itemOffset = (item.isHovered()) ? -1 : 0;

                    item.setWidth(itemWidth);
                    item.setHeight(itemHeight);

                    GUIUtils.matricesUtil(c, itemOffset, itemOffset, 0, () -> {
                        item.render(c, mouseX, mouseY, delta);
                    });
                }

                this.leftBtn.setY(colorBtnY);
                this.rightBtn.setY(colorBtnY);
                this.leftBtn.render(c, mouseX, mouseY, delta);
                this.rightBtn.render(c, mouseX, mouseY, delta);
                break;
        }
        GUIUtils.drawFill(ctx, getX() - 3, getY() + 8, getX() - 1, getY() + getHeight() - 8, lineColor);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    public void onPress() {
    }

    public boolean isMouseOverColorButtons(double mouseX, double mouseY) {
        if (this.type != 4 && this.type != 5) return false;

        return mouseX >= colorButtonsX &&
                mouseX <= colorButtonsX + colorButtonsWidth &&
                mouseY >= colorButtonsY &&
                mouseY <= colorButtonsY + colorButtonsHeight;
    }

    private static int colorControlsClickCounter = 0;

    public static boolean wereColorControlsJustClicked() {
        return colorControlsClickCounter > 0;
    }

    public static void decrementColorControlsCounter() {
        if (colorControlsClickCounter > 0) {
            colorControlsClickCounter--;
        }
    }

    public static void setColorControlsClicked() {
        colorControlsClickCounter = 1;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isMouseOverColorButtons(mouseX, mouseY)) {
            long window = Minecraft.getInstance().getWindow().getWindow();
            boolean shift = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                    || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
            if (!shift) return false;
            if (verticalAmount > 0) {
                colorOffset = (colorOffset - 1 + colorsData.length) % colorsData.length;
            } else if (verticalAmount < 0) {
                colorOffset = (colorOffset + 1) % colorsData.length;
            }
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clicked = false;
        switch (this.type) {
            case 1:
                if (this.command.mouseClicked(mouseX, mouseY, button))
                    clicked = true;
                break;
            case 2:
                if (this.delay.mouseClicked(mouseX, mouseY, button))
                    clicked = true;
                break;
            case 3:
                if (this.keybind.mouseClicked(mouseX, mouseY, button))
                    clicked = true;
                break;
            case 4:
            case 5:
                TextField[] fields = (this.type == 4)
                        ? new TextField[] { this.chatMessage }
                        : new TextField[] { this.titleMessage, this.subtitleMessage };

                for (TextField field : fields) {
                    if (field.mouseClicked(mouseX, mouseY, button)) clicked = true;
                }

                if (this.leftBtn.mouseClicked(mouseX, mouseY, button) ||
                        this.rightBtn.mouseClicked(mouseX, mouseY, button)) {
                    ActionItem.setColorControlsClicked();
                    clicked = true;
                }

                for (int i = 0; i < VISIBLE_COLORS; i++) {
                    int actualIndex = (colorOffset + i) % colorsData.length;
                    SmallTextButton item = colorsItem.get(actualIndex);
                    if (item.mouseClicked(mouseX, mouseY, button)) {
                        ActionItem.setColorControlsClicked();
                        clicked = true;
                    }
                }
                break;
        }
        if (this.deleteBtn.mouseClicked(mouseX, mouseY, button) ||
                this.bottomBtn.mouseClicked(mouseX, mouseY, button) ||
                this.resetBtn.mouseClicked(mouseX, mouseY, button) ||
                this.topBtn.mouseClicked(mouseX, mouseY, button)) {
            clicked = true;
        }
        return clicked;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (this.type) {
            case 1:
                if (this.command.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
                break;
            case 2:
                if (this.delay.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
                break;
            case 3:
                if (keybind.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
                break;
            case 4:
                if (chatMessage.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
                break;
            case 5:
                if (titleMessage.keyPressed(keyCode, scanCode, modifiers) ||
                        subtitleMessage.keyPressed(keyCode, scanCode, modifiers)) return true;
                break;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        switch (this.type) {
            case 1:
                if (this.command.charTyped(codePoint, modifiers)) {
                    return true;
                }
                break;
            case 2:
                if (this.delay.charTyped(codePoint, modifiers)) {
                    return true;
                }
                break;
            case 3:
                if (keybind.charTyped(codePoint, modifiers)) {
                    return true;
                }
                break;
            case 4:
                if (chatMessage.charTyped(codePoint, modifiers)) {
                    return true;
                }
                break;
            case 5:
                if (titleMessage.charTyped(codePoint, modifiers) ||
                        subtitleMessage.charTyped(codePoint, modifiers)) return true;
                break;
        }
        return super.charTyped(codePoint, modifiers);
    }

    // ? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }
    // ?} else if >=1.17 {
    /*
     * @Override
     * public void updateNarration(NarrationElementOutput builder) {
     * }
     */
    // ?}
}