package com.nitsha.binds;

import com.nitsha.binds.configs.*;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.utils.BindExecutor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.commands.CommandSourceStack;

//? if fabric {
//? if >=1.19 {
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
//?} else if >=1.18 {
/*import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
*///?} else {
/*import net.fabricmc.fabric.api.event.client.ClientTickCallback;
*///?}
//?} elif neoforge {
/*import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
*///?} elif forge {
/*import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
*///?}

//? if >=1.17 {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//?} else {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
*///?}

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
        //? if >=1.19 {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            registerWelcomeCommand(dispatcher);
        });
        ClientTickEvents.END_CLIENT_TICK.register(KeyBinds::tick);
        ClientTickEvents.END_CLIENT_TICK.register(BindExecutor::onTick);
        //?} else if >=1.18 {
        /*net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("nitsha::welcome")
                .executes(context -> {
                    executeWelcomeCommand();
                    return 1;
                })
        );
        ClientTickCallback.EVENT.register(client -> {
            KeyBinds.tick(client);
            BindExecutor.onTick(client);
        });
        *///?} else {
        /*ClientTickCallback.EVENT.register(client -> {
            KeyBinds.tick(client);
            BindExecutor.onTick(client);
        });
        *///?}
        //?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(Main::onRegisterClientCommands);
        NeoForge.EVENT_BUS.addListener(Main::onClientTick);
        NeoForge.EVENT_BUS.addListener(BindExecutor::onClientTick);
        *///?} elif forge {
        /*MinecraftForge.EVENT_BUS.register(new Main());
        MinecraftForge.EVENT_BUS.addListener(Main::onRegisterClientCommands);
        MinecraftForge.EVENT_BUS.addListener(BindExecutor::onClientTick);
        *///?}

        BindsStorage.loadConfigs();
        BindsStorage.load();
        BindHandler.register();
    }

    //? if fabric && >=1.18 {
    //? if >=1.19 {
    private static void registerWelcomeCommand(
            com.mojang.brigadier.CommandDispatcher<net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource> dispatcher) {
    //?} else {
    /*private static void registerWelcomeCommand(
            com.mojang.brigadier.CommandDispatcher<net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource> dispatcher) {
    *///?}
        dispatcher.register(
                ClientCommandManager.literal("nitsha::welcome")
                        .executes(context -> {
                            executeWelcomeCommand();
                            return 1;
                        }));
    }
    //?} elif neoforge || forge {
    /*private static void registerWelcomeCommand(
            com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                net.minecraft.commands.Commands.literal("nitsha::welcome")
                        .executes(context -> {
                            executeWelcomeCommand();
                            return 1;
                        }));
    }
    *///?}

    //? if fabric && >=1.18 {
    private static void executeWelcomeCommand() {
    //?} elif neoforge || forge {
    /*private static void executeWelcomeCommand() {
    *///?}
    //? if fabric && >=1.18 || neoforge || forge {
        var mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        MutableComponent message = TextUtils
                .literal("Hi there! Thank you for downloading my mod! <3");
        MutableComponent message2 = TextUtils
                .literal("Now you can try adding your own binds in the configuration menu!");
        MutableComponent message3 = TextUtils
                .literal("Here! Click on the ✎ button in the FastBind menu ");
        MutableComponent message4 = TextUtils.literal("(though mine's mirrored—oops)");

        //? if >=1.20 {
        message.setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE).withBold(true));
        message2.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
        message3.setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
        message3.append(
                message4.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
        //?} else {
        /*message = message.withStyle(s -> s.withColor(ChatFormatting.LIGHT_PURPLE).withBold(true));
        message2 = message2.withStyle(s -> s.withColor(ChatFormatting.AQUA));
        message3 = message3.withStyle(s -> s.withColor(ChatFormatting.YELLOW));
        message3.append(message4.withStyle(s -> s.withColor(ChatFormatting.GRAY).withItalic(true)));
        *///?}

        //? if >=1.19 {
        mc.player.displayClientMessage(message, false);
        mc.player.displayClientMessage(message2, false);
        mc.player.displayClientMessage(message3, false);
        //?} else {
        /*mc.player.sendMessage(message, mc.player.getUUID());
        mc.player.sendMessage(message2, mc.player.getUUID());
        mc.player.sendMessage(message3, mc.player.getUUID());
        *///?}
    }
    //?}

    //? if neoforge || forge {
    /*private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        registerWelcomeCommand(event.getDispatcher());
    }
    *///?}

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