package com.nitsha.binds;

import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.gui.BindsConfig;
import com.nitsha.binds.gui.node.NodeEditorGUI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainClass implements ModInitializer {
    public static final String MOD_ID = "nitsha_binds";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        KeyBinds.register();
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        BindsConfig.loadConfig();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("nitsha::welcome")
                    .executes(context -> {
                        assert MinecraftClient.getInstance().player != null;
                        ClientPlayerEntity player = MinecraftClient.getInstance().player;

                        player.sendMessage(Text.literal("Hi there! Thank you for downloading my mod! <3")
                                .styled(s -> s.withColor(Formatting.LIGHT_PURPLE).withBold(true)), false);

                        player.sendMessage(Text.literal("Now you can try adding your own binds in the configuration menu!")
                                .styled(s -> s.withColor(Formatting.AQUA)), false);

                        player.sendMessage(Text.literal("Here! Click on the ✎ button in the FastBind menu ")
                                .styled(s -> s.withColor(Formatting.YELLOW))
                                .append(Text.literal("(though mine's mirrored—oops)")
                                        .styled(s -> s.withColor(Formatting.GRAY).withItalic(true))), false);
                        return 0;
                    }));
            dispatcher.register(ClientCommandManager.literal("nitsha::node-editor")
                    .executes(context -> {
                        MinecraftClient.getInstance().send(() -> MinecraftClient.getInstance().setScreen(new NodeEditorGUI()));
                        return 0;
                    }));
        });
    }

    public void tick(MinecraftClient client) {
        KeyBinds.tick(client);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
