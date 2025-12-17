//? if neoforge {
/*package com.nitsha.binds.loaders.neoforge;

import com.nitsha.binds.Main;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.gui.screen.BindsEditor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
//? if >1.20.4 {
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
//? } else {
import net.neoforged.neoforge.client.ConfigScreenHandler;
//? }
@Mod(Main.MOD_ID)
public class Init {
    public Init(IEventBus modEventBus) {
        Main.init();
        KeyBinds.registerNeoForge(modEventBus);

        //? if >1.20.4 {
        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (minecraft, parent) -> new BindsEditor(parent)
        );
        //? } else {
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory((mc, prevScreen) -> new BindsEditor(prevScreen){})
        );
        //? }
    }
}
*///?}