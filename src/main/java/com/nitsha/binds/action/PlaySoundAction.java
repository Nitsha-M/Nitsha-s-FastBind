package com.nitsha.binds.action;

import com.nitsha.binds.FBLogger;
import com.nitsha.binds.Main;
import com.nitsha.binds.configs.dto.actions.AllActionsData;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.BedrockIconOptionButton;
import com.nitsha.binds.gui.widget.KeyEventSelector;
import com.nitsha.binds.gui.widget.Slider;
import com.nitsha.binds.gui.widget.TextField;
import com.nitsha.binds.mixin.KeyMappingAccessor;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;

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

import com.nitsha.binds.configs.dto.actions.AllActionsData.PlaySoundActionData;
import com.nitsha.binds.configs.dto.actions.AllActionsData.PlaySoundInnerData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class PlaySoundAction extends ActionType<PlaySoundActionData> {

    private int x, y, width;
    private KeyEventSelector selector;

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
    @Override public int getHeight() { return 72; }

    @Override
    public PlaySoundActionData createDefaultData() { return new PlaySoundActionData("playSound"); }

    @Override
    public void buildTasks(PlaySoundActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        if (data.value == null) return;
        String id = data.value.value;
        float volume = data.value.volume;
        float pitch = data.value.pitch;

        actions.add(() -> {
            ResourceLocation sound = ResourceLocation.fromNamespaceAndPath("minecraft", "entity.cow.ambient");
            FBLogger.info("sound: {}", sound);
            Minecraft.getInstance().getSoundManager().play(
                    new SimpleSoundInstance(sound, SoundSource.MASTER, volume, pitch,
                            SoundInstance.createUnseededRandom(), false, 0,
                            SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true)
            );
        });
    }

    @Override
    public void init(int x, int y, int width, PlaySoundActionData data) {
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

        this.volumeSlider = new Slider(x + width - 80, y + 50, 80, 10, true, 0.1f, 2.0f, data.value.volume);
        this.pitchSlider = new Slider(x + width - 80, y + 62, 80, 10, true, 0.1f, 2.0f, data.value.pitch);


    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.playSound"), 0,
                x + 2, y + 8, "top", "left", 0xFF212121, false);
        selector.renderWidget(ctx, mouseX, mouseY, delta);
        volumeSlider.renderWidget(ctx, mouseX, mouseY, delta);
        pitchSlider.renderWidget(ctx, mouseX, mouseY, delta);
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
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        boolean r = selector.mouseClicked(event, bl) || volumeSlider.mouseClicked(event, bl) || pitchSlider.mouseClicked(event, bl);
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
        boolean r = selector.mouseClicked(mx, my, btn) || volumeSlider.mouseClicked(mx, my, btn) || pitchSlider.mouseClicked(mx, my, btn);
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
        return r;
    }*/
    //? } else {
    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        boolean r = selector.mouseReleased(mx, my, btn);
        r |= volumeSlider.mouseReleased(mx, my, btn);
        r |= pitchSlider.mouseReleased(mx, my, btn);
        return r;
    }
    //? }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double deltaX, double deltaY) {
        boolean r = selector.mouseDragged(event, deltaX, deltaY);
        r |= volumeSlider.mouseDragged(event, deltaX, deltaY);
        r |= pitchSlider.mouseDragged(event, deltaX, deltaY);
        FBLogger.info("wqd");
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