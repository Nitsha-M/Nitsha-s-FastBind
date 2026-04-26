package com.nitsha.binds.action;

import com.nitsha.binds.FBLogger;
import com.nitsha.binds.gui.utils.GUIUtils;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.gui.widget.*;
import com.nitsha.binds.gui.widget.button.BedrockIconButton;
import com.nitsha.binds.gui.widget.button.SmallToggleButton;
import com.nitsha.binds.utils.AudioPlayer;
import com.nitsha.binds.utils.EventBus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    private boolean isExternal = false;
    private List<SmallToggleButton> slotBtns = new ArrayList<>();
    private int selectedSlot = 0;

    @Override
    public String getId() { return "playSound"; }

    @Override
    public String getDisplayName() {
        return TextUtils.translatable("nitsha.binds.advances.actions.playSound").getString();
    }

    @Override public String getDefaultValue() { return ""; }
    @Override public int getLineColor() { return 0xFFD50FC2; }
    @Override public int getHeight() { return 91; }

    @Override
    public PlaySoundActionData createDefaultData() { return new PlaySoundActionData("playSound"); }

    @Override
    public void buildTasks(PlaySoundActionData data, Queue<Runnable> actions, Minecraft client, LongConsumer setWaitUntil) {
        if (data.value == null) return;
        String id = data.value.value;
        boolean isExternal = data.value.isExternal;
        float volume = data.value.volume;
        float pitch = data.value.pitch;
        if (id.isEmpty()) return;

        actions.add(() -> {
            if (!isExternal) {
                if (!id.contains(":")) return;
                String[] parts = id.split(":");
                String namespace = parts[0];
                String name = parts[1];
                AudioPlayer.playSound(data.value.channel, namespace, name, volume, pitch, () -> {});
            } else {
                File sound = AudioPlayer.EXTERNAL_SOUNDS.get(id);
                AudioPlayer.playExternalSound(data.value.channel, sound, volume, pitch, () -> {});
            }
        });
    }

    @Override
    public void init(int x, int y, int width, PlaySoundActionData data) {
        this.x = x;
        this.y = y;
        this.width = width;

        this.selectedSlot = data.value.channel;

        String savedKey = data.value.value != null ? data.value.value : "";
        this.isExternal = data.value.isExternal;

        this.selector = new KeySelector(x, y + 24, width - 20, 20, () -> {
            EventBus.off("selectSound.result");
            EventBus.on("selectSound.result", (Object[] d) -> {
                FBLogger.info("key: {}", (String) d[0]);
                selector.setSelectedItem((String) d[0]);
                AudioPlayer.stopChannel(9);
                this.isExternal = (boolean) d[1];
                play(false);
            });
            EventBus.emit("selectSound.open", null);
        })  {
            @Override
            protected void updateName() {
                super.updateName();
                if (!this.getSelectedItem().isEmpty()) {
                    Component name = TextUtils.literal(this.getSelectedItem());
                    int maxWidth = this.width - 8;
                    int avgCharWidth = 7;
                    setName(GUIUtils.truncateString(name.getString(), maxWidth / avgCharWidth));
                }
            }
        };
        this.selector.setSelectedItem(savedKey);

        this.playButton = new BedrockIconButton(x + width - 18, y + 24, 18, 20, "sound_play", true, () -> {
            if (this.selector.getSelectedItem().isEmpty()) return;
            AudioPlayer.previewSound(9, this.selector.getSelectedItem(), this.volumeSlider.getValue(), this.pitchSlider.getValue(), this.isExternal, () -> play(true), () -> play(false), () -> play(false));
        });

        this.volumeSlider = new Slider(x + width - 70, y + 47, 70, 12, true, 0.1f, 1.0f, data.value.volume);
        this.pitchSlider = new Slider(x + width - 70, y + 62, 70, 12, true, 0.1f, 2.0f, data.value.pitch);

        int slotX = x + width - 108;
        for (int i = 0; i < 9; i++) {
            int finalI = i;
            SmallToggleButton it = new SmallToggleButton(TextUtils.literal(String.valueOf(i)), slotX, y + 77, 12, 12, this.selectedSlot == i, () -> {
                selectSlot(finalI);
            });
            switch (i) {
                case 0 -> it.setButtonDirection("_left");
                case 8 -> it.setButtonDirection("_right");
                default -> it.setButtonDirection("_both");
            }
            slotBtns.add(it);
            slotX += 12;
        }
    }

    public void play(boolean status) {
        if (status) {
            this.playButton.setColors(0xFFfac70c, 0xFFfcd02f, 0xFFFFFFFF, 0xFFFFFFFF);
            this.playButton.setIcon("sound_pause");
        } else {
            this.playButton.setColors(0xFFEF4747, 0xFFFF7272, 0xFFFFFFFF, 0xFFFFFFFF);
            this.playButton.setIcon("sound_play");
        }
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.playSound"), 0,
                x + 2, y + 12, "left", "center", 0xFF212121, false);

        GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.volume"), 0,
                x + 2, y + 52, "left", "center", 0xFF212121, false);

        GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.pitch"), 0,
                x + 2, y + 67, "left", "center", 0xFF212121, false);

        GUIUtils.addText(ctx, TextUtils.translatable("nitsha.binds.advances.actions.slot"), 0,
                x + 2, y + 82, "left", "center", 0xFF212121, false);

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
        slotBtns.forEach(btn -> btn.renderWidget(ctx, mouseX, mouseY, delta));
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (selector != null) selector.setY(y + 24);
        if (playButton != null) playButton.setY(y + 24);
        if (volumeSlider != null) volumeSlider.setY(y + 47);
        if (pitchSlider != null) pitchSlider.setY(y + 62);
        if (slotBtns != null) {
            for (SmallToggleButton btn : slotBtns) {
                btn.setY(y + 77);
            }
        }
    }

    @Override
    public PlaySoundActionData getValue() {
        PlaySoundActionData result = new PlaySoundActionData("playSound");
        result.value.value = selector.getSelectedItem();
        result.value.volume = volumeSlider.getValue();
        result.value.pitch = pitchSlider.getValue();
        result.value.isExternal = this.isExternal;
        result.value.channel = this.selectedSlot;
        return result;
    }

    @Override
    public void reset() {
        selector.setSelectedItem("");
        volumeSlider.setValue(1.0f);
        pitchSlider.setValue(1.0f);
        if (!slotBtns.isEmpty()) {
            selectSlot(0);
        }
    }

    private void selectSlot(int slot) {
        slotBtns.forEach(btn -> btn.setToggled(false));
        slotBtns.get(slot).setToggled(true);
        selectedSlot = slot;
    }

    //? if >=1.21.9 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        boolean r = selector.mouseClicked(event, bl) || volumeSlider.mouseClicked(event, bl) || pitchSlider.mouseClicked(event, bl) || playButton.mouseClicked(event, bl);
        for (SmallToggleButton b : slotBtns) {
            r |= b.mouseClicked(event, bl);
        }
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
        for (SmallToggleButton b : slotBtns) {
            r |= b.mouseClicked(mx, my, btn);
        }
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