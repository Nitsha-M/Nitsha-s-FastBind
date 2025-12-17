package com.nitsha.binds.configs;

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
    // private static KeyMapping.Category category = KeyMapping.Category.register(ResourceLocation.parse("category.nitsha.binds"));
    //? }

    public static void register() {
        //? if fabric {
        BINDS = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nitsha.binds",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                category
        ));

        NEXT_PAGE = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nitsha.next_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                category
        ));

        PREV_PAGE = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nitsha.prev_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                category
        ));

        EDITOR = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nitsha.editor",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category
        ));
        //?} elif forge {
        //? if >=1.19 {
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(KeyBinds::onRegisterKeyMappings);*/
        //? } else {
        /*registerKeyMappings();*/
        //? }
        //?}
    }

    //? if neoforge {
    /*public static void registerNeoForge(IEventBus modEventBus) {
        // Регистрируем обработчик события на MOD event bus
        modEventBus.addListener(KeyBinds::onRegisterKeyMappings);
    }
    *///?}

    //? if neoforge || forge {
    //? if >=1.19 {
    /*private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        BINDS = new KeyMapping(
                "key.nitsha.binds",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                category
        );
        event.register(BINDS);

        NEXT_PAGE = new KeyMapping(
                "key.nitsha.next_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                category
        );
        event.register(NEXT_PAGE);

        PREV_PAGE = new KeyMapping(
                "key.nitsha.prev_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                category
        );
        event.register(PREV_PAGE);

        EDITOR = new KeyMapping(
                "key.nitsha.editor",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category
        );
        event.register(EDITOR);
    }*/
    //? } else {
    /*private static void registerKeyMappings() {
        BINDS = new KeyMapping(
                "key.nitsha.binds",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                category
        );
        ClientRegistry.registerKeyBinding(BINDS);

        NEXT_PAGE = new KeyMapping(
                "key.nitsha.next_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                category
        );
        ClientRegistry.registerKeyBinding(NEXT_PAGE);

        PREV_PAGE = new KeyMapping(
                "key.nitsha.prev_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                category
        );
        ClientRegistry.registerKeyBinding(PREV_PAGE);

        EDITOR = new KeyMapping(
                "key.nitsha.editor",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                category
        );
        ClientRegistry.registerKeyBinding(EDITOR);
    }*/
    //? }
    //?}

    public static void tick(Minecraft client) {
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