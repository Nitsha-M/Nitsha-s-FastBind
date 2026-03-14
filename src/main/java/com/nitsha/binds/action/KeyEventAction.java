package com.nitsha.binds.action;

import com.nitsha.binds.Main;import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.mixin.KeyMappingAccessor;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
//? if >=1.20 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
 *///?}

import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;
//? if >=1.21.9 {
/*import net.minecraft.client.input.InputWithModifiers;*/
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

        this.selector = new KeyEventSelector(x, y + 24, width, 20);
        this.msField = new TextField(
                Minecraft.getInstance().font,
                x + width - 36 - 20, y + 24, 36, 20,
                6, "",
                TextUtils.translatable("nitsha.binds.advances.actions.delayLine").getString(), true
        );

        this.modeButton = new BedrockIconOptionButton(x + width - 18, y + 24, 18, 20) {
            //? <1.21.9 {
            @Override
            public void onPress() {
                super.onPress();
                rebuildModeWidgets(getSelected(), msField != null ? msField.getText() : "500");
            }
            //? } else {
            /*@Override
            public void onPress(InputWithModifiers inputWithModifiers) {
                super.onPress(inputWithModifiers);
                rebuildModeWidgets(getSelected(), msField != null ? msField.getText() : "500");
            }*/
            //? }
        };

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

        EventBus.off("selectKeyEvent.result");
        EventBus.on("selectKeyEvent.result", (String selectedKey) -> {
            selector.setSelectedItem(selectedKey);
        });
    }

    private void rebuildModeWidgets(String mode, String  msValue) {
        boolean isHold = MODE_HOLD.equals(mode);

        this.selector.setWidth((isHold) ? width - 58 : width - 20);
        this.modeButton.setSelected(mode);
        this.msField.setText(msValue);

        this.msField.visible = isHold;
    }

    @Override
    public void render(Object ctx, int mouseX, int mouseY, float delta) {
        //? if >=1.20 {
        GuiGraphics c = (GuiGraphics) ctx;
        //?} else {
        /*PoseStack c = (PoseStack) ctx;*/
        //?}

        String userLanguage = GUIUtils.getUL();
        if (userLanguage.equals("ja_jp") || userLanguage.equals("uk_ua")) {
            GUIUtils.addText(c, TextUtils.translatable("nitsha.binds.advances.actions.keyEvent_1"), 0,
                    x + 2, y + 4, "top", "left", 0xFF212121, false);
            GUIUtils.addText(c, TextUtils.translatable("nitsha.binds.advances.actions.keyEvent_2"), 0,
                    x + 2, y + 12, "top", "left", 0xFF212121, false);
        } else {
            GUIUtils.addText(c, TextUtils.translatable("nitsha.binds.advances.actions.keyEvent"), 0,
                    x + 2, y + 8, "top", "left", 0xFF212121, false);
        }
        selector.render(c, mouseX, mouseY, delta);
        msField.render(c, mouseX, mouseY, delta);
        modeButton.render(c, mouseX, mouseY, delta);
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
        return r;
    }
    //? }
}