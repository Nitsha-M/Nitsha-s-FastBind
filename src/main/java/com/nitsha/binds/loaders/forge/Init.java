//? if forge {
/*package com.nitsha.binds.loaders.forge;

import com.nitsha.binds.Main;
import com.nitsha.binds.configs.KeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Main.MOD_ID)
public class Init {
    public Init() {
        KeyBinds.register();

        // ВОТ ЭТО КРИТИЧЕСКИ ВАЖНО!
        MinecraftForge.EVENT_BUS.register(this);

        Main.init();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        KeyBinds.tick(Minecraft.getInstance());
    }
}
*/
//?}