package com.nitsha.binds.configs;

import com.nitsha.binds.FBLogger;
import com.nitsha.binds.Main;
import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.screen.BindsGUI;
import com.nitsha.binds.multiversions.NitshaCraft;
import com.nitsha.binds.multiversions.NitshaInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

//? if fabric {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
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
    private static boolean menuKeyWasDown;

    //? if <1.21.9 {
    /*private static String category = "category.nitsha.binds";
    *///? } else {
     private static KeyMapping.Category category = KeyMapping.Category.register(Main.id("keys"));
    //? }

    public static void register() {
        createMappings();

        //? if fabric {
        KeyMappingHelper.registerKeyMapping(BINDS);
        KeyMappingHelper.registerKeyMapping(EDITOR);
        KeyMappingHelper.registerKeyMapping(NEXT_PAGE);
        KeyMappingHelper.registerKeyMapping(PREV_PAGE);
        //?} elif forge {
        /*//? if >=1.19 {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(KeyBinds::onRegisterKeyMappings);
        //? } else {
        /^registerKeyMappings();^/
        //? }
        *///?}

        FBLogger.info("Finished registering keybinds");
    }

    private static void createMappings() {
        BINDS = new KeyMapping(
                "key.nitsha.binds",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_F8,
                category
        );

        NEXT_PAGE = new KeyMapping(
                "key.nitsha.next_page",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_F9,
                category
        );

        PREV_PAGE = new KeyMapping(
                "key.nitsha.prev_page",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_F7,
                category
        );

        EDITOR = new KeyMapping(
                "key.nitsha.editor",
                InputConstants.Type.KEYBOARD,
                InputConstants.UNKNOWN.getValue(),
                category
        );
    }

    //? if neoforge {
    /*public static void registerNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(KeyBinds::onRegisterKeyMappings);
    }
    *///?}

    //? if neoforge || forge {
    /*//? if >=1.19 {
    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(BINDS);
        event.register(NEXT_PAGE);
        event.register(PREV_PAGE);
        event.register(EDITOR);
    }
    //? } else {
    /^private static void registerKeyMappings() {
        ClientRegistry.registerKeyBinding(BINDS);
        ClientRegistry.registerKeyBinding(NEXT_PAGE);
        ClientRegistry.registerKeyBinding(PREV_PAGE);
        ClientRegistry.registerKeyBinding(EDITOR);
    }^/
    //? }
    *///?}

    public static void tick(Minecraft client) {
        if (BINDS == null || EDITOR == null) {
            return;
        }

        boolean menuKeyDown = NitshaInput.isKeyDown(BINDS);
        boolean menuClicked = false;

        while (BINDS.consumeClick()) {
            menuClicked = true;
        }

        boolean menuPressed = menuKeyDown ? !menuKeyWasDown : menuClicked;
        menuKeyWasDown = menuKeyDown;

        if (!menuKeyDown) {
            BindsGUI.ignoreHoldToOpenOnce = false;
        }

        if (menuPressed && !BindsGUI.ignoreHoldToOpenOnce && client.player != null && NitshaCraft.getScreen() == null) {
            BindsGUI.ignoreHoldToOpenOnce = true;
            NitshaCraft.setScreen(new BindsGUI());
        }

        if (EDITOR.consumeClick() && !(NitshaCraft.getScreen() instanceof BindsGUI)) {
            NitshaCraft.setScreen(new BindsEditor(NitshaCraft.getScreen()));
        }
    }
}
