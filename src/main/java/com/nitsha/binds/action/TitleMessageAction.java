package com.nitsha.binds.action;

import com.nitsha.binds.multiversions.TextUtils;
import com.nitsha.binds.gui.widget.TextField;

import com.nitsha.binds.utils.FormattedTextUtils;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;

import java.util.Queue;
import java.util.function.LongConsumer;

import com.nitsha.binds.configs.dto.actions.AllActionsData.TitleMessageActionData;
import com.nitsha.binds.configs.dto.actions.AllActionsData.TitleInnerData;
import com.nitsha.binds.configs.dto.actions.AllActionsData.TextFormatData;

public class TitleMessageAction extends ActionType<TitleMessageActionData> {

    private static final int FIELD_HEIGHT = 19;
    private static final int GAP = 2;
    private TextField titleField;
    private TextField subtitleField;

    @Override public String getId() { return "titleMessage"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.showTitle").getString();
    }

    @Override public String getDefaultValue() { return ""; }
    @Override public int getLineColor() { return 0xFF5facfa; }
    @Override public int getHeight() { return 46; }

    @Override
    public TitleMessageActionData createDefaultData() { return new TitleMessageActionData(); }

    @Override
    public void buildTasks(TitleMessageActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        TitleInnerData titleData = data.value;
        if (titleData == null) return;

        MutableComponent titleComponent = FormattedTextUtils.buildComponent(titleData.title);
        MutableComponent subtitleComponent = FormattedTextUtils.buildComponent(titleData.subtitle);

        if (titleComponent.getString().isEmpty() && subtitleComponent.getString().isEmpty()) return;

        final MutableComponent finalTitle    = titleComponent;
        final MutableComponent finalSubtitle = subtitleComponent;

        //? if >=26.2 {
        actions.add(() -> {
            if (client.gui != null) {
                client.gui.hud.clearTitles();
                client.gui.hud.setTitle(finalTitle);
                client.gui.hud.setSubtitle(finalSubtitle);
                client.gui.hud.setTimes(10, 70, 20);
            }
        });
        //? } else if >=1.17 {
        /*actions.add(() -> {
            if (client.gui != null) {
                //? if >=1.21.4 {
                 client.gui.clearTitles();
                //?} else {
                /^client.gui.clear();
                ^///?}
                client.gui.setTitle(finalTitle);
                client.gui.setSubtitle(finalSubtitle);
                client.gui.setTimes(10, 70, 20);
            }
        });
        *///? } else {
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
    public void init(int x, int y, int width, TitleMessageActionData data) {
        this.titleField = new TextField(Minecraft.getInstance().font, x, y + 3, width - 26, FIELD_HEIGHT, Integer.MAX_VALUE, "", TextUtils.translatable("nitsha.binds.advances.actions.titleLine").getString());
        this.subtitleField = new TextField(Minecraft.getInstance().font, x, y + 3 + FIELD_HEIGHT + GAP, width, FIELD_HEIGHT, Integer.MAX_VALUE, "", TextUtils.translatable("nitsha.binds.advances.actions.subtitleLine").getString());
        this.titleField.setFormattingAllowed(true);
        this.subtitleField.setFormattingAllowed(true);

        if (data.value != null) {
            if (data.value.title != null) {
                loadFormattedText(this.titleField, data.value.title);
            }
            if (data.value.subtitle != null) {
                loadFormattedText(this.subtitleField, data.value.subtitle);
            }
        }
    }
    private void loadFormattedText(TextField field, TextFormatData data) {
        String text = data.text;
        if (text != null) field.setText(text);
        if (data.marks != null) {
            field.setFormatMarksFromMap(data.marks);
        }
    }

    private TextFormatData saveFormattedText(TextField field) {
        TextFormatData data = new TextFormatData();
        data.text = field.getText();
        data.marks = field.getFormatMarksAsMap();
        return data;
    }

    @Override
    public void render(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        titleField.onRender(ctx, mouseX, mouseY, delta);
        subtitleField.onRender(ctx, mouseX, mouseY, delta);
    }
    @Override
    public void setPosition(int x, int y) {
        if (titleField != null) {
            titleField.setX(x);
            titleField.setY(y + 3);
        }
        if (subtitleField != null) {
            subtitleField.setX(x);
            subtitleField.setY(y + 3 + FIELD_HEIGHT + GAP);
        }
    }
    @Override
    public TitleMessageActionData getValue() {
        TitleMessageActionData result = new TitleMessageActionData();
        result.value.title = saveFormattedText(titleField);
        result.value.subtitle = saveFormattedText(subtitleField);
        return result;
    }

    @Override
    public void reset() {
        titleField.setText("");
        subtitleField.setText("");
    }

    //? if >=1.21.9 {
    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        return titleField.mouseClicked(event, bl) || subtitleField.mouseClicked(event, bl);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return titleField.keyPressed(event) || subtitleField.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        return titleField.charTyped(event) || subtitleField.charTyped(event);
    }
    //? } else {
    /*@Override
    public boolean mouseClicked(double mx, double my, int btn) {
        return titleField.mouseClicked(mx, my, btn) || subtitleField.mouseClicked(mx, my, btn);
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
    *///? }
}
