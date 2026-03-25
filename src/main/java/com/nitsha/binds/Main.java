package com.nitsha.binds;

import com.nitsha.binds.bind.BindHandler;
import com.nitsha.binds.configs.*;
import com.nitsha.binds.utils.BindExecutor;
import com.nitsha.binds.utils.KeepMovementHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

//? if fabric {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? } elif neoforge {
/*import net.neoforged.neoforge.common.NeoForge;
//? if >1.20.4 {
import net.neoforged.neoforge.client.event.ClientTickEvent;
//? } else {
import net.neoforged.neoforge.event.TickEvent;
//? }*/
//? } elif forge {
/*import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;*/
//?}

public class Main {
    public static final String MOD_ID = "nitsha_fastbind";

    public static final float GLOBAL_ANIMATION_SPEED = 0.6f;

    public static void init() {
        KeyBinds.register();
        BindsStorage.loadConfigs();
        BindsStorage.load();

        //? if fabric {
        ClientTickEvents.END_CLIENT_TICK.register(Main::onClientTick);
        //? } elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(Main::onClientTickEvent);*/
        //? } elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(Main::onClientTickEvent);*/
        //? }
    }

    //? if fabric {
    private static void onClientTick(Minecraft client) {
        tickClient(client);
    }
    //? } elif neoforge {
    //? if >1.20.4 {
    /*private static void onClientTickEvent(ClientTickEvent.Post event) {
        tickClient(Minecraft.getInstance());
    }*/
    //? } else {
    /*private static void onClientTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickClient(Minecraft.getInstance());
    }*/
    //? }
    //? } elif forge {
    /*@SubscribeEvent
    private static void onClientTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickClient(Minecraft.getInstance());
    }*/
    //? }

    private static void tickClient(Minecraft client) {
        KeyBinds.tick(client);
        BindExecutor.tick();
        BindHandler.tick();
        KeepMovementHandler.tick();
    }

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