//? if forge {
/*package com.nitsha.binds.loaders.forge;

import com.nitsha.binds.Main;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.gui.screen.BindsEditor;
import net.minecraft.client.Minecraft;
//? if >=1.19 {
import net.minecraftforge.client.ConfigScreenHandler;
//? } else if >=1.18 {
import net.minecraftforge.client.ConfigGuiHandler;
//? } else {
import net.minecraftforge.fmlclient.ConfigGuiHandler;
//? }
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Main.MOD_ID)
public class Init {
    public Init() {
        MinecraftForge.EVENT_BUS.register(this);

        Main.init();
        //? if >=1.19 {
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory((mc, prevScreen) -> new BindsEditor(prevScreen){})
        );
        //? } else {
        ModLoadingContext.get().registerExtensionPoint(
            ConfigGuiHandler.ConfigGuiFactory.class,
            () -> new ConfigGuiHandler.ConfigGuiFactory((mc, prevScreen) -> new BindsEditor(prevScreen){})
        );
        //? }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        KeyBinds.tick(Minecraft.getInstance());
    }
}
*/
//?}