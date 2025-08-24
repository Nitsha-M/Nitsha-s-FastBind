package com.nitsha.binds;

import com.nitsha.binds.configs.BindHandler;
import com.nitsha.binds.configs.KeyBinds;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.gui.utils.TextUtils;
import com.nitsha.binds.utils.BindExecutor;
import net.fabricmc.api.ModInitializer;
//? if >=1.19 {
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//? } else {
/*import net.minecraft.text.LiteralText;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
*/
//? }
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
//? if >=1.17 {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//? } else {
/*import java.util.logging.Logger;*/
//? }

public class MainClass implements ModInitializer {
    public static final String MOD_ID = "nitsha_binds";
    //? if >=1.17 {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    //? } else {
    /*public static final Logger LOGGER = Logger.getLogger(MOD_ID);*/
    //? }
    public static final String MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID)
                                            .map(mod -> mod.getMetadata().getVersion().getFriendlyString())
                                            .orElse("Unknown");
    public static final float GLOBAL_ANIMATION_SPEED = 0.6f;

    @Override
    public void onInitialize() {
        KeyBinds.register();
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        BindsConfig.loadConfigs();
        BindsConfig.migrateOldPresetFileIfExists();
        BindsConfig.migrateOldMultiPresetFiles();
        BindsConfig.loadPresets();
        BindHandler.register();
        ClientTickEvents.END_CLIENT_TICK.register(BindExecutor::onTick);
        //? if >=1.19 {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("nitsha::welcome")
                    .executes(context -> {
                        assert MinecraftClient.getInstance().player != null;
                        ClientPlayerEntity player = MinecraftClient.getInstance().player;

                        player.sendMessage(TextUtils.literal("Hi there! Thank you for downloading my mod! <3")
                                .styled(s -> s.withColor(Formatting.LIGHT_PURPLE).withBold(true)), false);

                        player.sendMessage(TextUtils.literal("Now you can try adding your own binds in the configuration menu!")
                                .styled(s -> s.withColor(Formatting.AQUA)), false);

                        player.sendMessage(TextUtils.literal("Here! Click on the ✎ button in the FastBind menu ")
                                .styled(s -> s.withColor(Formatting.YELLOW))
                                .append(TextUtils.literal("(though mine's mirrored—oops)")
                                        .styled(s -> s.withColor(Formatting.GRAY).withItalic(true))), false);
                        return 0;
                    }));
        });
        //? } else {
        /*CommandRegistrationCallback.EVENT.register((dispatcher, environment) -> {
            dispatcher.register(CommandManager.literal("nitsha::welcome").executes(context -> {
                assert MinecraftClient.getInstance().player != null;
                ClientPlayerEntity player = MinecraftClient.getInstance().player;

                player.sendMessage(new LiteralText("Hi there! Thank you for downloading my mod! <3")
                        .styled(s -> s.withColor(Formatting.LIGHT_PURPLE).withBold(true)), false);

                player.sendMessage(new LiteralText("Now you can try adding your own binds in the configuration menu!")
                        .styled(s -> s.withColor(Formatting.AQUA)), false);

                player.sendMessage(new LiteralText("Here! Click on the ✎ button in the FastBind menu ")
                        .styled(s -> s.withColor(Formatting.YELLOW))
                        .append(new LiteralText("(though mine's mirrored—oops)")
                                .styled(s -> s.withColor(Formatting.GRAY).withItalic(true))), false);
                return 0;
            }));
        });*/
        //? }
    }

    public void tick(MinecraftClient client) {
        KeyBinds.tick(client);
    }

    //? if >=1.20 {
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
    //? } else {
    /*public static Identifier id(String path) { return new Identifier(MOD_ID, path); }*/
    //? }

    public static Identifier idSprite(String path) {
        //? if >=1.20.2 {
        return id(path);
        //? } else {
        /*
        return id("textures/gui/sprites/" + path + ".png");
        *///? }
    }
}
