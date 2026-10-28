//? if fabric {
package com.nitsha.binds.loaders.fabric;

import com.nitsha.binds.Main;
import com.nitsha.binds.ItemsMapper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class Init implements ModInitializer {
    @Override
    public void onInitialize() {
        Main.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            ItemsMapper.initialize();
        });
    }
}
//?}