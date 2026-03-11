package com.nitsha.binds.action;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.SmallTextButton;
import com.nitsha.binds.gui.widget.TextField;
import com.nitsha.binds.gui.widget.TexturedButton;
import com.nitsha.binds.action.FormattedTextUtils;
import net.minecraft.client.Minecraft;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;*/
//?}
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

public class TitleMessageAction extends ActionType {

    private static final ResourceLocation LEFT        = Main.idSprite("action_left_normal");
    private static final ResourceLocation LEFT_HOVER  = Main.idSprite("action_left_hover");
    private static final ResourceLocation RIGHT       = Main.idSprite("action_right_normal");
    private static final ResourceLocation RIGHT_HOVER = Main.idSprite("action_right_hover");

    private static final int VISIBLE_COLORS = 13;
    private static final int FIELD_HEIGHT = 19;
    private static final int GAP = 2;

    private TextField titleField;
    private TextField subtitleField;
    private TexturedButton leftBtn;
    private TexturedButton rightBtn;
    private final List<SmallTextButton> colorsItem = new ArrayList<>();
    private int colorOffset = 0;

    private int x, y, width;
    private int colorButtonsY;

    @Override public String getId() { return "titleMessage"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.showTitle").getString();
    }

    @Override public String getDefaultValue() { return ""; }
    @Override public int getLineColor() { return 0xFF5facfa; }
    @Override public int getHeight() { return 57; }

    @Override
    public void buildTasks(Map<String, Object> data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        Object value = data.get("value");
        if (!(value instanceof Map)) return;

        Map<String, Object> titleData = (Map<String, Object>) value;

        MutableComponent titleComponent = TextUtils.empty();
        if (titleData.containsKey("title") && titleData.get("title") instanceof Map) {
            titleComponent = FormattedTextUtils.buildFormattedComponent(
                    (Map<String, Object>) titleData.get("title"));
        }

        MutableComponent subtitleComponent = TextUtils.empty();
        if (titleData.containsKey("subtitle") && titleData.get("subtitle") instanceof Map) {
            subtitleComponent = FormattedTextUtils.buildFormattedComponent(
                    (Map<String, Object>) titleData.get("subtitle"));
        }

        if (titleComponent.getString().isEmpty() && subtitleComponent.getString().isEmpty()) return;

        final MutableComponent finalTitle    = titleComponent;
        final MutableComponent finalSubtitle = subtitleComponent;

        //? if >=1.17 {
        actions.add(() -> {
            if (client.gui != null) {
                //? if >=1.21.4 {
                // client.gui.clearTitles();
                //?} else {
                client.gui.clear();
                //?}
                client.gui.setTitle(finalTitle);
                client.gui.setSubtitle(finalSubtitle);
                client.gui.setTimes(10, 70, 20);
            }
        });
        //? } else {
        /*actions.add(() -> {
            if (client.gui != null) {
                client.gui.setTitles(null, null, 10, 70, 20);
                client.gui.setTitles(finalTitle, null, -1, -1, -1);
                client.gui.setTitles(null, finalSubtitle, -1, -1, -1);
            }
        });*/
        //? }
    }

    @Override
    public void init(int x, int y, int width, Object value) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.colorButtonsY = y + 3 + FIELD_HEIGHT + GAP + FIELD_HEIGHT + GAP;

        this.titleField = new TextField(
                Minecraft.getInstance().font,
                x, y + 3, width - 26, FIELD_HEIGHT,
                Integer.MAX_VALUE, "",
                TextUtils.translatable("nitsha.binds.advances.actions.titleLine").getString()
        );

        this.subtitleField = new TextField(
                Minecraft.getInstance().font,
                x, y + 3 + FIELD_HEIGHT + GAP, width, FIELD_HEIGHT,
                Integer.MAX_VALUE, "",
                TextUtils.translatable("nitsha.binds.advances.actions.subtitleLine").getString()
        );

        if (value instanceof Map) {
            Map<String, Object> titleData = (Map<String, Object>) value;
            if (titleData.containsKey("title")) {
                loadFormattedText(this.titleField, (Map<String, Object>) titleData.get("title"));
            }
            if (titleData.containsKey("subtitle")) {
                loadFormattedText(this.subtitleField, (Map<String, Object>) titleData.get("subtitle"));
            }
        }

        this.leftBtn = GUIUtils.createTexturedBtn(x, colorButtonsY, 9, 9,
                new ResourceLocation[]{ LEFT, LEFT_HOVER },
                button -> colorOffset = (colorOffset - 1 + ChatMessageAction.COLORS_DATA.length) % ChatMessageAction.COLORS_DATA.length);

        this.rightBtn = GUIUtils.createTexturedBtn(x + width - 9, colorButtonsY, 9, 9,
                new ResourceLocation[]{ RIGHT, RIGHT_HOVER },
                button -> colorOffset = (colorOffset + 1) % ChatMessageAction.COLORS_DATA.length);

        for (int i = 0; i < ChatMessageAction.COLORS_DATA.length; i++) {
            int color = (int) ChatMessageAction.COLORS_DATA[i][0];
            int code  = (int) ChatMessageAction.COLORS_DATA[i][1];
            MutableComponent text = (MutableComponent) ChatMessageAction.COLORS_DATA[i][2];
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
    public void render(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        GuiGraphics c = (GuiGraphics) ctx;
        //?} else {
        /*PoseStack c = (PoseStack) ctx;*/
        //?}
        titleField.render(c, mouseX, mouseY, delta);
        subtitleField.render(c, mouseX, mouseY, delta);

        for (int i = 0; i < VISIBLE_COLORS; i++) {
            int actualIndex = (colorOffset + i) % ChatMessageAction.COLORS_DATA.length;
            SmallTextButton item = colorsItem.get(actualIndex);
            item.setX(x + 10 + (i * 11));
            item.setY(colorButtonsY);

            int itemWidth  = item.isHovered() ? 12 : 10;
            int itemHeight = item.isHovered() ? 11 : 9;
            int itemOffset = item.isHovered() ? -1 : 0;
            item.setWidth(itemWidth);
            item.setHeight(itemHeight);

            GUIUtils.matricesUtil(c, itemOffset, itemOffset, 0, () -> item.render(c, mouseX, mouseY, delta));
        }

        leftBtn.setY(colorButtonsY);
        rightBtn.setY(colorButtonsY);
        leftBtn.render(c, mouseX, mouseY, delta);
        rightBtn.render(c, mouseX, mouseY, delta);
    }

    @Override
    public Map<String, Object> getValue() {
        Map<String, Object> titleData = new HashMap<>();
        titleData.put("title", saveFormattedText(titleField));
        titleData.put("subtitle", saveFormattedText(subtitleField));

        Map<String, Object> result = new HashMap<>();
        result.put("type", "titleMessage");
        result.put("value", titleData);
        return result;
    }

    @Override
    public void reset() {
        titleField.setText("");
        subtitleField.setText("");
    }

    @Override
    public boolean isMouseOverColorButtons(double mouseX, double mouseY) {
        return mouseX >= x + 10 && mouseX <= x + 10 + width - 20
                && mouseY >= colorButtonsY && mouseY <= colorButtonsY + 10;
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        boolean clicked = titleField.mouseClicked(event, bl) || subtitleField.mouseClicked(event, bl);
        if (leftBtn.mouseClicked(event, bl) || rightBtn.mouseClicked(event, bl)) clicked = true;
        for (int i = 0; i < VISIBLE_COLORS; i++) {
            int actualIndex = (colorOffset + i) % ChatMessageAction.COLORS_DATA.length;
            if (colorsItem.get(actualIndex).mouseClicked(event, bl)) clicked = true;
        }
        return clicked;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return titleField.keyPressed(event) || subtitleField.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        return titleField.charTyped(event) || subtitleField.charTyped(event);
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        boolean clicked = titleField.mouseClicked(mx, my, btn)
                || subtitleField.mouseClicked(mx, my, btn);
        if (leftBtn.mouseClicked(mx, my, btn) || rightBtn.mouseClicked(mx, my, btn)) clicked = true;
        for (int i = 0; i < VISIBLE_COLORS; i++) {
            int actualIndex = (colorOffset + i) % ChatMessageAction.COLORS_DATA.length;
            if (colorsItem.get(actualIndex).mouseClicked(mx, my, btn)) clicked = true;
        }
        return clicked;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        return titleField.keyPressed(key, scan, mods)
                || subtitleField.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return titleField.charTyped(c, mods)
                || subtitleField.charTyped(c, mods);
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
            colorOffset = (colorOffset - 1 + ChatMessageAction.COLORS_DATA.length) % ChatMessageAction.COLORS_DATA.length;
        } else if (amount < 0) {
            colorOffset = (colorOffset + 1) % ChatMessageAction.COLORS_DATA.length;
        }
        return true;
    }
}