package com.nitsha.binds;

import com.nitsha.binds.configs.*;
import com.nitsha.binds.utils.BindExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

//? if fabric {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? } elif neoforge {
/*import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;*/
//? } elif forge {
/*import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;*/
//?}

//? if >=1.17 {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//? } else {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;*/
//? }

public class Main {
    public static final String MOD_ID = "nitsha_fastbind";

    //? if >=1.17 {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    //?} else {
    /*public static final Logger LOGGER = LogManager.getLogger(MOD_ID);*/
    //?}

    public static final float GLOBAL_ANIMATION_SPEED = 0.6f;

    public static void init() {
        KeyBinds.register();

        //? if fabric {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeyBinds.tick(client);
            BindExecutor.onTick(client);
        });
        //?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(Main::onClientTick);
        NeoForge.EVENT_BUS.addListener(BindExecutor::onClientTick);
        *///?} elif forge {
        /*MinecraftForge.EVENT_BUS.register(new Main());
        MinecraftForge.EVENT_BUS.addListener(BindExecutor::onClientTick);
        *///?}

        BindsStorage.loadConfigs();
        BindsStorage.load();
        BindHandler.register();
    }

    //? if fabric && >=1.19 {
    public static void tick(Minecraft client) {
        KeyBinds.tick(client);
    }
    //?} elif neoforge {
    /*private static void onClientTick(ClientTickEvent.Post event) {
        KeyBinds.tick(Minecraft.getInstance());
    }
    *///?} elif forge {
    /*@SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        KeyBinds.tick(Minecraft.getInstance());
    }
    *///?}

    //? if >=1.21 {
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
    //?} else if >=1.20 {
    /*public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    *///?} else {
    /*public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    *///?}

    public static ResourceLocation idSprite(String path) {
        //? if >=1.20.2 {
        return id(path);
        //?} else {
        /*return id("textures/gui/sprites/" + path + ".png");*/
        //?}
    }
}