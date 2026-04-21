package com.nitsha.binds.utils;

import com.nitsha.binds.FBLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

import javax.sound.sampled.*;
import java.io.File;

public class AudioPlayer {

    public static void playSound(String namespace, String soundName, float volume, float pitch) {
        ResourceLocation sound = ResourceLocation.fromNamespaceAndPath(namespace, soundName);
        FBLogger.info("Playing sound: {}", sound);
        Minecraft.getInstance().getSoundManager().play(
                new SimpleSoundInstance(sound, SoundSource.MASTER, volume, pitch,
                        SoundInstance.createUnseededRandom(), false, 0,
                        SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true)
        );
    }

    public static void playExternalSound(File wavFile, float volume, float pitch) {
        if (!wavFile.exists()) return;

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
                FBLogger.info("Playing sound: {}", wavFile.getName());
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) clip.close();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
