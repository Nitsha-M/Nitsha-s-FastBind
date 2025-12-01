package com.nitsha.binds.configs;

import com.nitsha.binds.gui.screen.BindsEditor;
import com.nitsha.binds.gui.screen.BindsGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

//? if fabric {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//?} elif neoforge {
/*import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.IEventBus;
*///?} elif forge {
/*import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
*///?}

public class KeyBinds {
    public static KeyMapping BINDS;
    public static KeyMapping PREV_PAGE;
    public static KeyMapping NEXT_PAGE;
    public static KeyMapping EDITOR;

    public static void register() {
        //? if fabric {
        BINDS = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nitsha.binds",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "category.nitsha.binds"
        ));

        NEXT_PAGE = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nitsha.next_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.nitsha.binds"
        ));

        PREV_PAGE = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nitsha.prev_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.nitsha.binds"
        ));

        EDITOR = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nitsha.editor",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.nitsha.binds"
        ));
        //?} elif forge {
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(KeyBinds::onRegisterKeyMappings);
         *///?}
    }

    //? if neoforge {
    /*public static void registerNeoForge(IEventBus modEventBus) {
        // Регистрируем обработчик события на MOD event bus
        modEventBus.addListener(KeyBinds::onRegisterKeyMappings);
    }
    *///?}

    //? if neoforge || forge {
    /*private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        BINDS = new KeyMapping(
                "key.nitsha.binds",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "category.nitsha.binds"
        );
        event.register(BINDS);

        NEXT_PAGE = new KeyMapping(
                "key.nitsha.next_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.nitsha.binds"
        );
        event.register(NEXT_PAGE);

        PREV_PAGE = new KeyMapping(
                "key.nitsha.prev_page",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.nitsha.binds"
        );
        event.register(PREV_PAGE);

        EDITOR = new KeyMapping(
                "key.nitsha.editor",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.nitsha.binds"
        );
        event.register(EDITOR);
    }
    *///?}

    public static void tick(Minecraft client) {
        if (BINDS.consumeClick()) {
            if (BindsGUI.ignoreHoldToOpenOnce) return;
            if (!(client.screen instanceof BindsGUI)) {
                BindsGUI.ignoreHoldToOpenOnce = true;
                client.setScreen(new BindsGUI());
            }
        }
        if (!InputConstants.isKeyDown(client.getWindow().getWindow(), BINDS.getDefaultKey().getValue())) {
            BindsGUI.ignoreHoldToOpenOnce = false;
        }
        if (EDITOR.consumeClick()) {
            if (!(client.screen instanceof BindsGUI)) {
                client.setScreen(new BindsEditor(client.screen));
            }
        }
    }
}