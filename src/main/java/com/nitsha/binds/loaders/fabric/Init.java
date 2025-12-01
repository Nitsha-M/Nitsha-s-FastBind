//? if fabric {
package com.nitsha.binds.loaders.fabric;

import com.nitsha.binds.Main;
import com.nitsha.binds.gui.utils.TextUtils;
import net.fabricmc.api.ModInitializer;

public class Init implements ModInitializer {
    @Override
    public void onInitialize() {
        Main.init();
    }
}
//?}