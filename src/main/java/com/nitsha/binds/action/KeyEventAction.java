package com.nitsha.binds.action;

import com.nitsha.binds.Main;import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.mixin.KeyMappingAccessor;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import java.util.Map;
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

public class KeyEventAction extends ActionType {

    private int x, y, width;
    private KeyEventSelector selector;
    private BedrockIconOptionButton modeButton;
    private TextField msField;

    private static final String MODE_PRESS = "press";
    private static final String MODE_HOLD = "hold";

    private static final ResourceLocation NORMAL = Main.id("textures/gui/btns/bedrock_normal_bottom_right.png");
    private static final ResourceLocation PRESSED_NORMAL = Main.id("textures/gui/btns/bedrock_normal_top_right.png");

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
    public void buildTasks(Map<String, Object> data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data.get("value");
        if (map == null) return;

        String id = String.valueOf(map.get("value"));
        String mode = String.valueOf(map.getOrDefault("mode", MODE_PRESS));
        int holdMs = 100;
        try { holdMs = Integer.parseInt(String.valueOf(map.getOrDefault("ms", 100))); } catch (NumberFormatException ignored) {}
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
    public void init(int x, int y, int width, Object value) {
        this.x = x;
        this.y = y;
        this.width = width;

        this.selector = new KeyEventSelector(x, y + 24, width, 20, () -> {
            EventBus.off("selectKeyEvent.result");
            EventBus.on("selectKeyEvent.result", (String selectedKey) -> {
                selector.setSelectedItem(selectedKey);
            });
            EventBus.emit("selectKeyEvent.open", null);
        });

        this.msField = new TextField(
                Minecraft.getInstance().font,
                x + width - 36 - 20, y + 24, 36, 20,
                6, "",
                TextUtils.translatable("nitsha.binds.advances.actions.delayLine").getString(), true
        );

        this.modeButton = new BedrockIconOptionButton(x + width - 18, y + 24, 18, 20, () -> {
                rebuildModeWidgets(this.modeButton.getSelected(), msField != null ? msField.getText() : "500");
        });

        String savedMode = MODE_PRESS;
        String savedMs = "500";
        String savedKey = "";

        if (value instanceof Map<?, ?> map) {
            Object v = map.get("value");
            Object m = map.get("mode");
            Object ms = map.get("ms");
            if (v != null) savedKey = String.valueOf(v);
            if (m != null) savedMode = String.valueOf(m);
            if (ms != null) savedMs = String.valueOf(ms);
        }

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
    public Map<String, Object> getValue() {
        Map<String, Object> inner = new java.util.HashMap<>();
        inner.put("value", selector.getSelectedItem());
        inner.put("mode", modeButton.getSelected());
        inner.put("ms", msField != null ? msField.getText() : "500");

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("type", "keyEvent");
        result.put("value", inner);
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
        System.out.println("i'm  free");
        return r;
    }
    //? }
}