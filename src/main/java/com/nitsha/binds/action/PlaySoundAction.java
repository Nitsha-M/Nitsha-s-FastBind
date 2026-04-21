package com.nitsha.binds.action;

import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.mixin.KeyMappingAccessor;
import com.nitsha.binds.utils.AudioPlayer;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.Minecraft;
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

import com.nitsha.binds.configs.dto.actions.AllActionsData.PlaySoundActionData;
import net.minecraft.network.chat.Component;

public class PlaySoundAction extends ActionType<PlaySoundActionData> {

    private int x, y, width;
    private KeySelector selector;
    private BedrockIconButton playButton;

    private Slider volumeSlider;
    private Slider pitchSlider;

    @Override
    public String getId() { return "playSound"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.playSound").getString();
    }

    @Override public String getDefaultValue() { return ""; }
    @Override public int getLineColor() { return 0xFFD50FC2; }
    @Override public int getHeight() { return 76; }

    @Override
    public PlaySoundActionData createDefaultData() { return new PlaySoundActionData("playSound"); }

    @Override
    public void buildTasks(PlaySoundActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        if (data.value == null) return;
        String id = data.value.value;
        boolean isExternal = data.value.isExternal;
        float volume = data.value.volume;
        float pitch = data.value.pitch;

        actions.add(() -> {
            if (!isExternal) {
                if (!id.contains(":")) return;
                String[] parts = id.split(":");
                String namespace = parts[0];
                String name = parts[1];
                AudioPlayer.playSound(namespace, name, volume, pitch);
            } else {
                AudioPlayer.playSound("minecraft", "entity.pig.ambient", volume, pitch);
            }
        });
    }

    @Override
    public void init(int x, int y, int width, PlaySoundActionData data) {
        this.x = x;
        this.y = y;
        this.width = width;

        String savedKey = data.value.value != null ? data.value.value : "";

        this.selector = new KeySelector(x, y + 24, width - 20, 20, () -> {
            EventBus.off("selectKeyEvent.result");
            EventBus.on("selectKeyEvent.result", (String selectedKey) -> {
                selector.setSelectedItem(selectedKey);
            });
            EventBus.emit("selectKeyEvent.open", null);
        })  {
            @Override
            protected void updateName() {
                super.updateName();
                if (!this.getSelectedItem().isEmpty()) {
                    Component name = TextUtils.literal(savedKey);
                    int maxWidth = this.width - 8;
                    int avgCharWidth = 7;
                    setName(GUIUtils.truncateString(name.getString(), maxWidth / avgCharWidth));
                }
            }
        };
        this.selector.setSelectedItem(savedKey);


        this.playButton = new BedrockIconButton(x + width - 18, y + 24, 18, 20, "paste", true, () -> {
            if (!data.value.isExternal) {
                if (!savedKey.contains(":")) return;
                String[] parts = savedKey.split(":", 2);
                String namespace = parts[0];
                String name = parts[1];
                AudioPlayer.playSound(namespace, name, this.volumeSlider.getValue(), this.pitchSlider.getValue());
            } else {
                AudioPlayer.playSound("minecraft", "entity.pig.ambient", this.volumeSlider.getValue(), this.pitchSlider.getValue());
            }
        });

        this.volumeSlider = new Slider(x + width - 70, y + 47, 70, 12, true, 0.1f, 2.0f, data.value.volume);
        this.pitchSlider = new Slider(x + width - 70, y + 62, 70, 12, true, 0.1f, 2.0f, data.value.pitch);
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.playSound"), 0,
                x + 2, y + 12, "left", "center", 0xFF212121, false);

        GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.volume"), 0,
                x + 2, y + 52, "left", "center", 0xFF212121, false);

        GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.pitch"), 0,
                x + 2, y + 67, "left", "center", 0xFF212121, false);

        // Volume
        GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.toSuper(String.valueOf(this.volumeSlider.getValue()))), 0,
                x + width - 70 - 2, y + 54, "right", "center", 0xFF212121, false);
        // Pitch
        GUIUtils.addText(ctx, TextUtils.literal(GUIUtils.toSuper(String.valueOf(this.pitchSlider.getValue()))), 0,
                x + width - 70 - 2, y + 69, "right", "center", 0xFF212121, false);

        selector.renderWidget(ctx, mouseX, mouseY, delta);
        volumeSlider.renderWidget(ctx, mouseX, mouseY, delta);
        pitchSlider.renderWidget(ctx, mouseX, mouseY, delta);
        playButton.renderWidget(ctx, mouseX, mouseY, delta);
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (selector != null) selector.setY(y + 24);
        if (playButton != null) playButton.setY(y + 24);
        if (volumeSlider != null) volumeSlider.setY(y + 47);
        if (pitchSlider != null) pitchSlider.setY(y + 62);
    }

    @Override
    public PlaySoundActionData getValue() {
        PlaySoundActionData result = new PlaySoundActionData("playSound");
        result.value.value = selector.getSelectedItem();
        result.value.volume = volumeSlider.getValue();
        result.value.pitch = pitchSlider.getValue();
        return result;
    }

    @Override
    public void reset() {
        selector.setSelectedItem("");
        volumeSlider.setValue(1.0f);
        pitchSlider.setValue(1.0f);
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        boolean r = selector.mouseClicked(event, bl) || volumeSlider.mouseClicked(event, bl) || pitchSlider.mouseClicked(event, bl) || playButton.mouseClicked(event, bl);
        return r;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        boolean r = selector.keyPressed(event);
        return r;
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        boolean r = selector.charTyped(event);
        return r;
    }*/
    //? } else {
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        boolean r = selector.mouseClicked(mx, my, btn) || volumeSlider.mouseClicked(mx, my, btn) || pitchSlider.mouseClicked(mx, my, btn) || playButton.mouseClicked(mx, my, btn);
        return r;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        boolean r = selector.keyPressed(key, scan, mods);
        return r;
    }

    @Override
    public boolean charTyped(char c, int mods) {
        boolean r = selector.charTyped(c, mods);
        return r;
    }
    //? }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        boolean r = selector.mouseReleased(event);
        r |= volumeSlider.mouseReleased(event);
        r |= pitchSlider.mouseReleased(event);
        r |= playButton.mouseReleased(event);
        return r;
    }*/
    //? } else {
    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        boolean r = selector.mouseReleased(mx, my, btn);
        r |= volumeSlider.mouseReleased(mx, my, btn);
        r |= pitchSlider.mouseReleased(mx, my, btn);
        r |= playButton.mouseReleased(mx, my, btn);
        return r;
    }
    //? }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double deltaX, double deltaY) {
        boolean r = selector.mouseDragged(event, deltaX, deltaY);
        r |= volumeSlider.mouseDragged(event, deltaX, deltaY);
        r |= pitchSlider.mouseDragged(event, deltaX, deltaY);
        return r;
    }*/
    //? } else {
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean r = selector.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        r |= volumeSlider.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        r |= pitchSlider.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return r;
    }
    //? }
}