package com.nitsha.binds.utils;

import com.nitsha.binds.FBLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

import javax.sound.sampled.*;
import java.io.File;
import java.util.*;

import com.nitsha.binds.configs.Storage;

public class AudioPlayer {

    public static final Map<String, File> EXTERNAL_SOUNDS = new HashMap<>();

    private static final int CHANNEL_COUNT = 10;
    private static final Clip[] clips = new Clip[CHANNEL_COUNT];
    private static final SimpleSoundInstance[] sounds = new SimpleSoundInstance[CHANNEL_COUNT];
    private static final Runnable[] onFinishCallbacks = new Runnable[CHANNEL_COUNT];
    private static final Runnable[] onPauseCallbacks = new Runnable[CHANNEL_COUNT];
    private static final String[] currentValues = new String[CHANNEL_COUNT];

    public static void stopChannel(int channel) {
        if (channel < 0 || channel >= CHANNEL_COUNT) return;

        if (clips[channel] != null && clips[channel].isRunning()) {
            clips[channel].stop();
            clips[channel].close();
            clips[channel] = null;
        }

        if (sounds[channel] != null) {
            Minecraft.getInstance().getSoundManager().stop(sounds[channel]);
            sounds[channel] = null;
        }

        if (onFinishCallbacks[channel] != null) {
            onFinishCallbacks[channel].run();
            onFinishCallbacks[channel] = null;
        }
    }

    private static void forceStopChannel(int channel) {
        if (channel < 0 || channel >= CHANNEL_COUNT) return;

        onFinishCallbacks[channel] = null;

        if (onPauseCallbacks[channel] != null) {
            onPauseCallbacks[channel].run();
            onPauseCallbacks[channel] = null;
        }

        if (clips[channel] != null && clips[channel].isRunning()) {
            clips[channel].stop();
            clips[channel].close();
            clips[channel] = null;
        }

        if (sounds[channel] != null) {
            Minecraft.getInstance().getSoundManager().stop(sounds[channel]);
            sounds[channel] = null;
        }

        currentValues[channel] = null;
    }

    public static boolean isChannelPlaying(int channel) {
        if (channel < 0 || channel >= CHANNEL_COUNT) return false;
        boolean extPlaying = clips[channel] != null && clips[channel].isRunning();
        boolean mcPlaying = sounds[channel] != null && Minecraft.getInstance().getSoundManager().isActive(sounds[channel]);
        return extPlaying || mcPlaying;
    }

    public static void playSound(int channel, String namespace, String soundName, float volume, float pitch, Runnable onFinish) {
        if (channel < 0 || channel >= CHANNEL_COUNT) return;
        stopChannel(channel);
        onFinishCallbacks[channel] = onFinish;

        ResourceLocation sound = ResourceLocation.fromNamespaceAndPath(namespace, soundName);
        sounds[channel] = new SimpleSoundInstance(sound, SoundSource.MASTER, volume, pitch,
                SoundInstance.createUnseededRandom(), false, 0,
                SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
        Minecraft.getInstance().getSoundManager().play(sounds[channel]);
    }

    public static void playExternalSound(int channel, File wavFile, float volume, float pitch, Runnable onFinish) {
        if (channel < 0 || channel >= CHANNEL_COUNT) return;
        if (!wavFile.exists()) return;
        stopChannel(channel);

        new Thread(() -> {
            try {
                AudioInputStream audioSrc = AudioSystem.getAudioInputStream(wavFile);
                AudioFormat baseFormat = audioSrc.getFormat();
                AudioFormat pitchedFormat = new AudioFormat(
                        baseFormat.getSampleRate() * pitch,
                        baseFormat.getSampleSizeInBits(),
                        baseFormat.getChannels(),
                        true,
                        baseFormat.isBigEndian()
                );
                AudioInputStream pitchedStream = new AudioInputStream(audioSrc, pitchedFormat, audioSrc.getFrameLength());

                Clip clip = AudioSystem.getClip();
                clip.open(pitchedStream);

                float mcVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER);
                float combined = Mth.clamp(volume * mcVolume, 0.0001f, 1.0f);
                float db = (float) (Math.log10(combined) * 20.0);

                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(Mth.clamp(db, gainControl.getMinimum(), gainControl.getMaximum()));

                clip.start();
                clips[channel] = clip;

                final Runnable finishCallback = onFinish;

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                        clips[channel] = null;
                        onFinishCallbacks[channel] = null;
                        if (finishCallback != null) finishCallback.run();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void stopAll() {
        for (int i = 0; i < CHANNEL_COUNT; i++) stopChannel(i);
    }

    public static void tick() {
        for (int i = 0; i < CHANNEL_COUNT; i++) {
            if (sounds[i] != null && !Minecraft.getInstance().getSoundManager().isActive(sounds[i])) {
                sounds[i] = null;
                Runnable cb = onFinishCallbacks[i];
                onFinishCallbacks[i] = null;
                if (cb != null) cb.run();
            }
        }
    }

    public static void previewSound(int channel, String value, float volume, float pitch, boolean isExternal, Runnable onPlay, Runnable onPause, Runnable onFinish) {
        if (isChannelPlaying(channel) && value.equals(currentValues[channel])) {
            forceStopChannel(channel);
            if (onPause != null) onPause.run();
            return;
        }

        forceStopChannel(channel);
        currentValues[channel] = value;
        onPauseCallbacks[channel] = onPause;

        if (isExternal) {
            File sound = EXTERNAL_SOUNDS.get(value);
            if (sound != null) playExternalSound(channel, sound, volume, pitch, onFinish);
        } else {
            if (!value.contains(":")) return;
            String[] parts = value.split(":", 2);
            playSound(channel, parts[0], parts[1], volume, pitch, onFinish);
        }

        if (onPlay != null) onPlay.run();
    }

    public static void loadExternalSounds() {
        EXTERNAL_SOUNDS.clear();
        File soundsDir = Storage.SOUNDS_DIR.toFile();
        if (soundsDir.exists() && soundsDir.isDirectory()) {
            File[] files = soundsDir.listFiles((dir, name) -> name.endsWith(".wav"));
            if (files != null) {
                for (File file : files) {
                    EXTERNAL_SOUNDS.put(file.getName(), file);
                }
                FBLogger.info("External sounds loaded: {}", EXTERNAL_SOUNDS.size());
            }
        }
    }

    public static List<ResourceLocation> getSortedSounds() {
        //? if >=1.19.3 {
        Set<ResourceLocation> keys = BuiltInRegistries.SOUND_EVENT.keySet();
        //?} else {
        /* Set<ResourceLocation> keys = net.minecraft.core.Registry.SOUND_EVENT.keySet(); */
        //?}
        List<ResourceLocation> sorted = new ArrayList<>(keys);
        sorted.sort((a, b) -> a.getPath().compareToIgnoreCase(b.getPath()));
        return sorted;
    }

    public static List<String> getSoundCategories() {
        //? if >=1.19.3 {
        Set<ResourceLocation> keys = BuiltInRegistries.SOUND_EVENT.keySet();
        //?} else {
        /* Set<ResourceLocation> keys = net.minecraft.core.Registry.SOUND_EVENT.keySet(); */
        //?}
        Set<String> categories = new TreeSet<>();
        for (ResourceLocation rl : keys) {
            String path = rl.getPath();
            int dotIndex = path.indexOf('.');
            if (dotIndex != -1) {
                categories.add(path.substring(0, dotIndex));
            } else {
                categories.add("other");
            }
        }
        return new ArrayList<>(categories);
    }

    public static SoundEvent getSoundByName(String name) {
        ResourceLocation rs = ResourceLocation.parse(name);
        //? if >=1.19.3 {
        return BuiltInRegistries.SOUND_EVENT.getValue(rs);
        //?} else {
        /* return Registry.SOUND_EVENT.get(rs); */
        //?}
    }
}