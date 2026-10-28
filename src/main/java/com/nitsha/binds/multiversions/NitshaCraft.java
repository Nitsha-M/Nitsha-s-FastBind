package com.nitsha.binds.multiversions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.screens.Screen;

public final class NitshaCraft {
    private NitshaCraft() {
    }

    public static void addToast(Toast toast) {
        //? if >=26.3 {
        Minecraft.getInstance().gui.toastManager().addToast(toast);
        //? } else if >=1.21.2 {
        /*Minecraft.getInstance().getToastManager().addToast(toast);*/
        //? } else {
        /*Minecraft.getInstance().getToasts().addToast(toast);*/
        //? }
    }

    public static void setScreen(Screen screen) {
        Minecraft minecraft = Minecraft.getInstance();

        //? if >=26.3 {
        minecraft.gui.setScreen(screen);
        //? } else {
        /*minecraft.setScreen(screen);*/
        //? }
    }

    public static void openUri(String url) {
        //? if >=26.2 {
        com.mojang.blaze3d.Blaze3D.openUri(java.net.URI.create(url));
        //? } else {
        /*net.minecraft.Util.getPlatform().openUri(url);*/
        //? }
    }

    public static void confirmLink(String url) {
        Minecraft minecraft = Minecraft.getInstance();

        //$ if >=26.3 'Screen parent = minecraft.gui.screen();' else 'Screen parent = minecraft.screen;'
        Screen parent = minecraft.gui.screen();

        it.unimi.dsi.fastutil.booleans.BooleanConsumer callback = confirmed -> {
            if (confirmed) {
                openUri(url);
            }

            setScreen(parent);
        };

        //$ if >=26.2 'setScreen(new net.minecraft.client.gui.screens.ConfirmLinkScreen(callback, java.net.URI.create(url), true));' else 'setScreen(new net.minecraft.client.gui.screens.ConfirmLinkScreen(callback, url, true));'
        setScreen(new net.minecraft.client.gui.screens.ConfirmLinkScreen(callback, java.net.URI.create(url), true));
    }
}