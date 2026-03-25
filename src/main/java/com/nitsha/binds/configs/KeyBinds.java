package com.nitsha.binds.configs;

import com.nitsha.binds.FBLogger;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.screen.BindsGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

//? if fabric {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?} elif neoforge {
/*import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.IEventBus;
*///?} elif forge {
/*import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//? if >=1.19 {
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
//? } else if >=1.18 {
import net.minecraftforge.client.ClientRegistry;
//? } else {
import net.minecraftforge.fmlclient.registry.ClientRegistry;
//? }
*/
//? }

public class KeyBinds {
    public static KeyMapping BINDS;
    public static KeyMapping PREV_PAGE;
    public static KeyMapping NEXT_PAGE;
    public static KeyMapping EDITOR;

    //? if <1.21.9 {
    private static String category = "category.nitsha.binds";
    //? } else {
    // private static KeyMapping.Category category = KeyMapping.Category.register(Main.id("keys"));
    //? }

    public static void register() {
        createMappings();

        //? if fabric {
        KeyBindingHelper.registerKeyBinding(BINDS);
        KeyBindingHelper.registerKeyBinding(EDITOR);
        KeyBindingHelper.registerKeyBinding(NEXT_PAGE);
        KeyBindingHelper.registerKeyBinding(PREV_PAGE);
        //?} elif forge {
        //? if >=1.19 {
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(KeyBinds::onRegisterKeyMappings);*/
        //? } else {
        /*registerKeyMappings();*/
        //? }
        //?}

        FBLogger.info("Finished registering keybinds");
    }

    private static void createMappings() {
        BINDS = new KeyMapping(
                "key.nitsha.binds",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                category
        );

        NEXT_PAGE = new KeyMapping(
                "key.nitsha.next_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                category
        );

        PREV_PAGE = new KeyMapping(
                "key.nitsha.prev_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                category
        );

        EDITOR = new KeyMapping(
                "key.nitsha.editor",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category
        );
    }

    //? if neoforge {
    /*public static void registerNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(KeyBinds::onRegisterKeyMappings);
    }
    *///?}

    //? if neoforge || forge {
    //? if >=1.19 {
    /*private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(BINDS);
        event.register(NEXT_PAGE);
        event.register(PREV_PAGE);
        event.register(EDITOR);
    }*/
    //? } else {
    /*private static void registerKeyMappings() {
        ClientRegistry.registerKeyBinding(BINDS);
        ClientRegistry.registerKeyBinding(NEXT_PAGE);
        ClientRegistry.registerKeyBinding(PREV_PAGE);
        ClientRegistry.registerKeyBinding(EDITOR);
    }*/
    //? }
    //?}

    public static void tick(Minecraft client) {
        if (BINDS == null || EDITOR == null) {
            return;
        }
        if (BINDS.consumeClick()) {
            if (BindsGUI.ignoreHoldToOpenOnce) return;
            if (!(client.screen instanceof BindsGUI)) {
                BindsGUI.ignoreHoldToOpenOnce = true;
                client.setScreen(new BindsGUI());
            }
        }
        if (!InputConstants.isKeyDown(
                //? if <1.21.9 {
                client.getWindow().getWindow()
                //? } else {
                // client.getWindow()
                //? }
                , BINDS.getDefaultKey().getValue())) {
            BindsGUI.ignoreHoldToOpenOnce = false;
        }
        if (EDITOR.consumeClick()) {
            if (!(client.screen instanceof BindsGUI)) {
                client.setScreen(new BindsEditor(client.screen));
            }
        }
    }
}