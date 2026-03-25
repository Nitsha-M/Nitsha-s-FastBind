package com.nitsha.binds.action;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.SmallTextButton;
import com.nitsha.binds.gui.widget.TextField;
import com.nitsha.binds.gui.widget.TexturedButton;
import com.nitsha.binds.action.FormattedTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

public class ChatMessageAction extends ActionType {

    private static final ResourceLocation LEFT        = Main.idSprite("action_left_normal");
    private static final ResourceLocation LEFT_HOVER  = Main.idSprite("action_left_hover");
    private static final ResourceLocation RIGHT       = Main.idSprite("action_right_normal");
    private static final ResourceLocation RIGHT_HOVER = Main.idSprite("action_right_hover");

    private static final int VISIBLE_COLORS = 13;

    private TextField field;
    private TexturedButton leftBtn;
    private TexturedButton rightBtn;
    private final List<SmallTextButton> colorsItem = new ArrayList<>();
    private int colorOffset = 0;

    private int x, y, width;
    private int colorButtonsY;

    static final Object[][] COLORS_DATA = {
            { 0xFF000000, 0,  TextUtils.literal("") },
            { 0xFF0001a9, 1,  TextUtils.literal("") },
            { 0xFF04a702, 2,  TextUtils.literal("") },
            { 0xFF01a7a9, 3,  TextUtils.literal("") },
            { 0xFFaa0101, 4,  TextUtils.literal("") },
            { 0xFFa501a7, 5,  TextUtils.literal("") },
            { 0xFFfca702, 6,  TextUtils.literal("") },
            { 0xFFa8a8a8, 7,  TextUtils.literal("") },
            { 0xFF545454, 8,  TextUtils.literal("") },
            { 0xFF5254fa, 9,  TextUtils.literal("") },
            { 0xFF55fb53, 10, TextUtils.literal("") },
            { 0xFF50fcf7, 11, TextUtils.literal("") },
            { 0xFFfd5357, 12, TextUtils.literal("") },
            { 0xFFf955fb, 13, TextUtils.literal("") },
            { 0xFFfbfd4f, 14, TextUtils.literal("") },
            { 0xFFFFFFFF, 15, TextUtils.literal("") },
            { 0xFF316d20, 20, TextUtils.literal("ʙ").setStyle(Style.EMPTY.withBold(true)) },
            { 0xFF316d20, 21, TextUtils.literal("ɪ").setStyle(Style.EMPTY.withItalic(true)) },
            { 0xFF316d20, 22, TextUtils.literal("ᴜ").setStyle(Style.EMPTY.withUnderlined(true)) },
            //? if >=1.20 {
            { 0xFF316d20, 23, TextUtils.literal("s").setStyle(Style.EMPTY.withStrikethrough(true)) },
            { 0xFF316d20, 24, TextUtils.literal("ᴏ").setStyle(Style.EMPTY.withObfuscated(true)) },
            //?} else {
            /*{ 0xFF316d20, 23, TextUtils.literal("s").withStyle(s -> s.applyFormat(ChatFormatting.STRIKETHROUGH)) },
            { 0xFF316d20, 24, TextUtils.literal("ᴏ").withStyle(s -> s.applyFormat(ChatFormatting.OBFUSCATED)) },*/
            //?}
            { 0xFF316d20, 99, TextUtils.literal("ʀ") },
    };

    @Override public String getId() { return "chatMessage"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.showMessage").getString();
    }

    @Override public String getDefaultValue() { return ""; }
    @Override public int getLineColor() { return 0xFF174eff; }
    @Override public int getHeight() { return 36; }

    @Override
    public void buildTasks(Map<String, Object> data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        Object value = data.get("value");
        MutableComponent message;

        if (value instanceof Map) {
            Map<String, Object> formattedText = (Map<String, Object>) value;
            String text = (String) formattedText.get("text");
            message = TextUtils.empty();

            if (formattedText.containsKey("marks")) {
                List<Map<String, Object>> marks = (List<Map<String, Object>>) formattedText.get("marks");
                Style currentStyle = Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));
                int lastPos = 0;

                for (Map<String, Object> markData : marks) {
                    int pos = ((Number) markData.get("pos")).intValue();
                    int styleCode = ((Number) markData.get("style")).intValue();

                    if (pos > lastPos) {
                        message = message.append(
                                TextUtils.literal(text.substring(lastPos, pos)).setStyle(currentStyle));
                    }
                    currentStyle = FormattedTextUtils.applyStyleCode(styleCode, currentStyle);
                    lastPos = pos;
                }

                if (lastPos < text.length()) {
                    message = message.append(TextUtils.literal(text.substring(lastPos)).setStyle(currentStyle));
                }
            } else {
                message = TextUtils.literal(text);
            }
        } else {
            message = TextUtils.literal(String.valueOf(value));
        }

        final MutableComponent finalMessage = message;
        actions.add(() -> {
            if (client.player != null) {
                //? if <26.1 {
                client.player.displayClientMessage(finalMessage, false);
                //? } else {
                // client.player.sendSystemMessage(finalMessage);
                //? }
            }
        });
    }

    @Override
    public void init(int x, int y, int width, Object value) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.colorButtonsY = y + 24;

        this.field = new TextField(
                Minecraft.getInstance().font,
                x, y + 3, width - 26, 19,
                Integer.MAX_VALUE, "",
                TextUtils.translatable("nitsha.binds.advances.actions.chatMessage").getString()
        );

        if (value instanceof Map) {
            loadFormattedText(this.field, (Map<String, Object>) value);
        }

        this.leftBtn = GUIUtils.createTexturedBtn(x, colorButtonsY, 9, 9,
                new ResourceLocation[]{ LEFT, LEFT_HOVER },
                button -> colorOffset = (colorOffset - 1 + COLORS_DATA.length) % COLORS_DATA.length);

        this.rightBtn = GUIUtils.createTexturedBtn(x + width - 9, colorButtonsY, 9, 9,
                new ResourceLocation[]{ RIGHT, RIGHT_HOVER },
                button -> colorOffset = (colorOffset + 1) % COLORS_DATA.length);

        for (int i = 0; i < COLORS_DATA.length; i++) {
            int color = (int) COLORS_DATA[i][0];
            int code  = (int) COLORS_DATA[i][1];
            MutableComponent text = (MutableComponent) COLORS_DATA[i][2];
            colorsItem.add(new SmallTextButton(text, x + 10, colorButtonsY, color, 10, "left", () -> {
                TextField focused = TextField.getFocusedField();
                if (focused != null) focused.setStyle(code);
            }));
        }
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
        data.put("marks", field.getFormatMarksAsMap());
        return data;
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        field.renderWidget(ctx, mouseX, mouseY, delta);

        for (int i = 0; i < VISIBLE_COLORS; i++) {
            int actualIndex = (colorOffset + i) % COLORS_DATA.length;
            SmallTextButton item = colorsItem.get(actualIndex);
            item.setX(x + 10 + (i * 11));
            item.setY(colorButtonsY);

            int itemWidth  = item.isHovered() ? 12 : 10;
            int itemHeight = item.isHovered() ? 11 : 9;
            int itemOffset = item.isHovered() ? -1 : 0;
            item.setWidth(itemWidth);
            item.setHeight(itemHeight);

            GUIUtils.matricesUtil(ctx, itemOffset, itemOffset, 0, () -> item.renderWidget(ctx, mouseX, mouseY, delta));
        }

        leftBtn.setY(colorButtonsY);
        rightBtn.setY(colorButtonsY);
        leftBtn.renderWidget(ctx, mouseX, mouseY, delta);
        rightBtn.renderWidget(ctx, mouseX, mouseY, delta);
    }

    @Override
    public Map<String, Object> getValue() {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "chatMessage");
        result.put("value", saveFormattedText(field));
        return result;
    }

    @Override public void reset() { field.setText(""); }

    @Override
    public boolean isMouseOverColorButtons(double mouseX, double mouseY) {
        return mouseX >= x + 10 && mouseX <= x + 10 + width - 20
                && mouseY >= colorButtonsY && mouseY <= colorButtonsY + 10;
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        boolean clicked = field.mouseClicked(event, bl);
        if (leftBtn.mouseClicked(event, bl) || rightBtn.mouseClicked(event, bl)) clicked = true;
        for (int i = 0; i < VISIBLE_COLORS; i++) {
            int actualIndex = (colorOffset + i) % COLORS_DATA.length;
            if (colorsItem.get(actualIndex).mouseClicked(event, bl)) clicked = true;
        }
        return clicked;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return field.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        return field.charTyped(event);
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        boolean clicked = field.mouseClicked(mx, my, btn);
        if (leftBtn.mouseClicked(mx, my, btn) || rightBtn.mouseClicked(mx, my, btn)) clicked = true;
        for (int i = 0; i < VISIBLE_COLORS; i++) {
            int actualIndex = (colorOffset + i) % COLORS_DATA.length;
            if (colorsItem.get(actualIndex).mouseClicked(mx, my, btn)) clicked = true;
        }
        return clicked;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        return field.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return field.charTyped(c, mods);
    }
    //? }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        if (!isMouseOverColorButtons(mx, my)) return false;
        //? if >=1.21.9 {
        // com.mojang.blaze3d.platform.Window window = Minecraft.getInstance().getWindow();
        //? } else {
        long window = Minecraft.getInstance().getWindow().getWindow();
        //? }
        boolean shift = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
        if (!shift) return false;
        if (amount > 0) {
            colorOffset = (colorOffset - 1 + COLORS_DATA.length) % COLORS_DATA.length;
        } else if (amount < 0) {
            colorOffset = (colorOffset + 1) % COLORS_DATA.length;
        }
        return true;
    }
}