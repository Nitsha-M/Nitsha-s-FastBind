package com.nitsha.binds.action;

import com.nitsha.binds.gui.widget.TextField;

import com.nitsha.binds.multiversions.TextUtils;
import com.nitsha.binds.utils.FormattedTextUtils;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.MutableComponent;

import java.util.Queue;
import java.util.function.LongConsumer;

import com.nitsha.binds.configs.dto.actions.AllActionsData.ChatMessageActionData;
import com.nitsha.binds.configs.dto.actions.AllActionsData.TextFormatData;

public class ChatMessageAction extends ActionType<ChatMessageActionData> {

    private TextField field;

    @Override public String getId() { return "chatMessage"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.showMessage").getString();
    }

    @Override public String getDefaultValue() { return ""; }
    @Override public int getLineColor() { return 0xFF174eff; }
    @Override public int getHeight() { return 26; }

    @Override
    public ChatMessageActionData createDefaultData() { return new ChatMessageActionData(); }

    @Override
    public void buildTasks(ChatMessageActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        TextFormatData value = data.value;
        if (value == null) return;

        MutableComponent message = FormattedTextUtils.buildComponent(value);
        if (message.getString().isEmpty()) return;

        final MutableComponent finalMessage = message;
        actions.add(() -> {
            if (client.player != null) {
                //? if <26.1 {
                /*client.player.displayClientMessage(finalMessage, false);
                *///? } else {
                 client.player.sendSystemMessage(finalMessage);
                //? }
            }
        });
    }

    @Override
    public void init(int x, int y, int width, ChatMessageActionData data) {
        this.field = new TextField(Minecraft.getInstance().font, x, y + 3, width - 26, 19, Integer.MAX_VALUE, "", TextUtils.translatable("nitsha.binds.advances.actions.chatMessage").getString());
        this.field.setFormattingAllowed(true);
        if (data.value != null) {
            loadFormattedText(this.field, data.value);
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
    public void setPosition(int x, int y) {
        if (field != null) {
            field.setX(x);
            field.setY(y + 3);
        }
    }
    @Override
    public void render(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        field.onRender(ctx, mouseX, mouseY, delta);
    }
    @Override
    public ChatMessageActionData getValue() {
        ChatMessageActionData result = new ChatMessageActionData();
        result.value = saveFormattedText(field);
        return result;
    }

    @Override public void reset() { field.setText(""); }

    //? if >=1.21.9 {
    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        return field.mouseClicked(event, bl);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return field.keyPressed(event);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        return field.charTyped(event);
    }
    //? } else {
    /*@Override
    public boolean mouseClicked(double mx, double my, int btn) {
        return field.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        return field.keyPressed(key, scan, mods);
    }

    @Override
    public boolean charTyped(char c, int mods) {
        return field.charTyped(c, mods);
    }
    *///? }
}
