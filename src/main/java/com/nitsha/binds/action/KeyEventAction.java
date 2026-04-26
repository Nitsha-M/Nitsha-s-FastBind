package com.nitsha.binds.action;

import com.nitsha.binds.Main;import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.gui.widget.button.BedrockIconOptionButton;
import com.nitsha.binds.mixin.KeyMappingAccessor;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Queue;
import java.util.function.LongConsumer;
//? if >=1.21.9 {
/*import net.minecraft.client.input.InputWithModifiers;*/
//? }

//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.InputWithModifiers;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.CharacterEvent;*/
//? }

import com.nitsha.binds.configs.dto.actions.AllActionsData.KeyEventActionData;

public class KeyEventAction extends ActionType<KeyEventActionData> {

    private int x, y, width;
    private KeySelector selector;
    private BedrockIconOptionButton modeButton;
    private TextField msField;

    private static final String MODE_PRESS = "press";
    private static final String MODE_HOLD = "hold";

    @Override
    public String getId() { return "keyEvent"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.keyEvent").getString();
    }

    @Override public String getDefaultValue() { return ""; }
    @Override public int getLineColor() { return 0xFFD50FC2; }
    @Override public int getHeight() { return 46; }

    @Override
    public KeyEventActionData createDefaultData() { return new KeyEventActionData("keyEvent"); }

    @Override
    public void buildTasks(KeyEventActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        if (data.value == null) return;

        String id = data.value.value;
        String mode = data.value.mode != null ? data.value.mode : MODE_PRESS;
        int holdMs = 100;
        try { holdMs = Integer.parseInt(data.value.ms != null ? data.value.ms : "100"); } catch (NumberFormatException ignored) {}
        final int finalHoldMs = holdMs;

        actions.add(() -> {
            if (client.player == null || client.getConnection() == null) return;
            KeyMapping key = KeyMappingAccessor.binds$getAll().get(id);
            if (key != null) {
                key.setDown(true);
                ((KeyMappingAccessor) key).binds$setClickCount(1);
            }
        });

        if (mode.equals(MODE_HOLD)) {
            actions.add(() -> setWaitUntil.accept(net.minecraft.Util.getMillis() + finalHoldMs));
        }

        actions.add(() -> {
            KeyMapping key = KeyMappingAccessor.binds$getAll().get(id);
            if (key != null) key.setDown(false);
        });
    }

    @Override
    public void init(int x, int y, int width, KeyEventActionData data) {
        this.x = x;
        this.y = y;
        this.width = width;

        String savedKey = data.value.value != null ? data.value.value : "";
        String savedMode = data.value.mode != null ? data.value.mode : MODE_PRESS;
        String savedMs = data.value.ms != null ? data.value.ms : "500";

        this.selector = new KeySelector(x, y + 24, width, 20, () -> {
            EventBus.off("selectKeyEvent.result");
            EventBus.on("selectKeyEvent.result", (Object[] d) -> {
                selector.setSelectedItem((String) d[0]);
            });
            EventBus.emit("selectKeyEvent.open", null);
        }) {
            @Override
            protected void updateName() {
                super.updateName();
                if (!this.getSelectedItem().isEmpty()) {
                    Component name = TextUtils.translatable(KeyMappingAccessor.binds$getAll().get(this.getSelectedItem()).getName());
                    int maxWidth = this.width - 8;
                    int avgCharWidth = 7;
                    setName(GUIUtils.truncateString(name.getString(), maxWidth / avgCharWidth));
                }
            }
        };

        this.msField = new TextField(
                Minecraft.getInstance().font,
                x + width - 36 - 20, y + 24, 36, 20,
                6, "",
                TextUtils.translatable("nitsha.binds.advances.actions.delayLine").getString(), true
        );

        this.modeButton = new BedrockIconOptionButton(x + width - 18, y + 24, 18, 20, () -> {
                rebuildModeWidgets(this.modeButton.getSelected(), msField != null ? msField.getText() : "500");
        }).addOption("press", "nitsha.binds.advances.actions.option.press", Main.id("textures/gui/sprites/key_press.png"), 0xFF07938d, 0xFF0fb2ab, 0xFFFFFFFF, 0xFFFFFFFF)
          .addOption("hold", "nitsha.binds.advances.actions.option.hold", Main.id("textures/gui/sprites/key_hold.png"), 0xFF9cc708, 0xFFafda19, 0xFFFFFFFF, 0xFFFFFFFF);

        this.selector.setSelectedItem(savedKey);
        rebuildModeWidgets(savedMode, savedMs);
    }

    private void rebuildModeWidgets(String mode, String  msValue) {
        boolean isHold = MODE_HOLD.equals(mode);

        this.selector.setWidth((isHold) ? width - 58 : width - 20);
        this.modeButton.setSelected(mode);
        this.msField.setText(msValue);

        this.msField.visible = isHold;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (selector != null) selector.setY(y + 24);
        if (msField != null) msField.setY(y + 24);
        if (modeButton != null) modeButton.setY(y + 24);
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        String userLanguage = GUIUtils.getUL();
        if (userLanguage.equals("ja_jp") || userLanguage.equals("uk_ua")) {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.keyEvent_1"), 0,
                    x + 2, y + 4, "top", "left", 0xFF212121, false);
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.keyEvent_2"), 0,
                    x + 2, y + 12, "top", "left", 0xFF212121, false);
        } else {
            GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.keyEvent"), 0,
                    x + 2, y + 8, "top", "left", 0xFF212121, false);
        }
        selector.renderWidget(ctx, mouseX, mouseY, delta);
        msField.renderWidget(ctx, mouseX, mouseY, delta);
        modeButton.renderWidget(ctx, mouseX, mouseY, delta);
    }

    @Override
    public KeyEventActionData getValue() {
        KeyEventActionData result = new KeyEventActionData("keyEvent");
        result.value.value = selector.getSelectedItem();
        result.value.mode = modeButton.getSelected();
        result.value.ms = msField != null ? msField.getText() : "500";
        return result;
    }

    @Override
    public void reset() {
        selector.setSelectedItem("");
        rebuildModeWidgets(MODE_PRESS, "500");
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        boolean r = selector.mouseClicked(event, bl) || modeButton.mouseClicked(event, bl);
        if (msField != null) r |= msField.mouseClicked(event, bl);
        return r;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        boolean r = selector.keyPressed(event);
        if (msField != null) r |= msField.keyPressed(event);
        return r;
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        boolean r = selector.charTyped(event);
        if (msField != null) r |= msField.charTyped(event);
        return r;
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        boolean r = selector.mouseClicked(mx, my, btn) || modeButton.mouseClicked(mx, my, btn);
        if (msField != null) r |= msField.mouseClicked(mx, my, btn);
        return r;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        boolean r = selector.keyPressed(key, scan, mods);
        if (msField != null) r |= msField.keyPressed(key, scan, mods);
        return r;
    }

    @Override
    public boolean charTyped(char c, int mods) {
        boolean r = selector.charTyped(c, mods);
        if (msField != null) r |= msField.charTyped(c, mods);
        return r;
    }
    //? }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        boolean r = selector.mouseReleased(event);
        r |= modeButton.mouseReleased(event);
        return r;
    }*/
    //? } else {
    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        boolean r = selector.mouseReleased(mx, my, btn);
        r |= modeButton.mouseReleased(mx, my, btn);
        return r;
    }
    //? }
}