package com.nitsha.binds.action;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.ItemsMapper;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.SmallTextButton;
import com.nitsha.binds.gui.widget.TextField;
import com.nitsha.binds.gui.widget.TexturedButton;
import com.nitsha.binds.utils.FormattedTextUtils;
import com.nitsha.binds.utils.ToastUtils;
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
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;

import com.nitsha.binds.configs.dto.actions.AllActionsData.ToastActionData;
import com.nitsha.binds.configs.dto.actions.AllActionsData.ToastInnerData;
import com.nitsha.binds.configs.dto.actions.AllActionsData.TextFormatData;

public class ToastAction extends ActionType<ToastActionData> {

    private static final ResourceLocation LEFT        = Main.idSprite("action_left_normal");
    private static final ResourceLocation LEFT_HOVER  = Main.idSprite("action_left_hover");
    private static final ResourceLocation RIGHT       = Main.idSprite("action_right_normal");
    private static final ResourceLocation RIGHT_HOVER = Main.idSprite("action_right_hover");

    private static final int VISIBLE_COLORS = 13;
    private static final int FIELD_HEIGHT = 19;
    private static final int GAP = 2;

    private TextField titleField;
    private TexturedButton leftBtn;
    private TexturedButton rightBtn;
    private final List<SmallTextButton> colorsItem = new ArrayList<>();
    private int colorOffset = 0;

    private int x, y, width;
    private int colorButtonsY;

    @Override public String getId() { return "toast"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.toast").getString();
    }

    @Override public String getDefaultValue() { return ""; }
    @Override public int getLineColor() { return 0xFF5facfa; }
    @Override public int getHeight() { return 57; }

    @Override
    public ToastActionData createDefaultData() { return new ToastActionData(); }

    @Override
    public void buildTasks(ToastActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        ToastInnerData titleData = data.value;
        if (titleData == null) return;

        MutableComponent titleComponent = FormattedTextUtils.buildComponent(titleData.title);

        if (titleComponent.getString().isEmpty()) return;

        String icon = data.value.icon != null ? data.value.icon : "minecraft:diamond";

        final MutableComponent finalTitle = titleComponent;

        if (client.player == null || client.getConnection() == null) return;

        actions.add(() -> {
            if (client.level != null) {
                ToastUtils.showFakeAdvancement(
                        finalTitle,
                        titleData.toastType.toLowerCase(),
                        ItemsMapper.getItemStack(icon)
                );
            }
        });
    }

    @Override
    public void init(int x, int y, int width, ToastActionData data) {
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

        if (data.value != null) {
            if (data.value.title != null) {
                loadFormattedText(this.titleField, data.value.title);
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

    private void loadFormattedText(TextField field, TextFormatData data) {
        String text = data.text;
        if (text != null) field.setText(text);
        if (data.marks != null) {
            field.setFormatMarksFromMap(data.marks);
        }
    }

    @Override
    public void setPosition(int x, int y) {
        this.y = y;
        this.x = x;
        this.colorButtonsY = y + 3 + FIELD_HEIGHT + GAP + FIELD_HEIGHT + GAP;
        if (titleField != null) titleField.setY(y + 3);
        if (leftBtn != null) leftBtn.setY(colorButtonsY);
        if (rightBtn != null) rightBtn.setY(colorButtonsY);
        for (SmallTextButton btn : colorsItem) {
            btn.setY(colorButtonsY);
        }
    }

    private TextFormatData saveFormattedText(TextField field) {
        TextFormatData data = new TextFormatData();
        data.text = field.getText();
        data.marks = field.getFormatMarksAsMap();
        return data;
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        titleField.renderWidget(ctx, mouseX, mouseY, delta);

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

            GUIUtils.matricesUtil(ctx, itemOffset, itemOffset, 0, () -> item.renderWidget(ctx, mouseX, mouseY, delta));
        }

        leftBtn.setY(colorButtonsY);
        rightBtn.setY(colorButtonsY);
        leftBtn.renderWidget(ctx, mouseX, mouseY, delta);
        rightBtn.renderWidget(ctx, mouseX, mouseY, delta);
    }

    @Override
    public ToastActionData getValue() {
        ToastActionData result = new ToastActionData();
        result.value.title = saveFormattedText(titleField);
        return result;
    }

    @Override
    public void reset() {
        titleField.setText("");
    }

    @Override
    public boolean isMouseOverColorButtons(double mouseX, double mouseY) {
        return mouseX >= x + 10 && mouseX <= x + 10 + width - 20
                && mouseY >= colorButtonsY && mouseY <= colorButtonsY + 10;
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        boolean clicked = titleField.mouseClicked(event, bl);
        if (leftBtn.mouseClicked(event, bl) || rightBtn.mouseClicked(event, bl)) clicked = true;
        for (int i = 0; i < VISIBLE_COLORS; i++) {
            int actualIndex = (colorOffset + i) % ChatMessageAction.COLORS_DATA.length;
            if (colorsItem.get(actualIndex).mouseClicked(event, bl)) clicked = true;
        }
        return clicked;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return titleField.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        return titleField.charTyped(event);
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        boolean clicked = titleField.mouseClicked(mx, my, btn);
        if (leftBtn.mouseClicked(mx, my, btn) || rightBtn.mouseClicked(mx, my, btn)) clicked = true;
        for (int i = 0; i < VISIBLE_COLORS; i++) {
            int actualIndex = (colorOffset + i) % ChatMessageAction.COLORS_DATA.length;
            if (colorsItem.get(actualIndex).mouseClicked(mx, my, btn)) clicked = true;
        }
        return clicked;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        return titleField.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return titleField.charTyped(c, mods);
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