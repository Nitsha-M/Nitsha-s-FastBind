package com.nitsha.binds.utils;

import com.nitsha.binds.configs.BindEntry;
import com.nitsha.binds.configs.BindsConfig;
import com.nitsha.binds.gui.screen.BindsGUI;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
//? if <1.19 {
//? }

import java.util.*;

public class BindExecutor {
    private static final List<BindTask> activeTasks = new ArrayList<>();

    public static void startBind(BindEntry bind) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;

        if ((client.currentScreen != null && !(client.currentScreen instanceof BindsGUI)) || bind.actions.isEmpty()) return;

        BindTask task = new BindTask(bind, client);
        activeTasks.add(task);
    }

    public static void onTick(MinecraftClient client) {
        activeTasks.removeIf(BindTask::tick);
    }

    private static class BindTask {
        private final Queue<Runnable> actions = new LinkedList<>();
        private long waitUntil = 0;

        BindTask(BindEntry bind, MinecraftClient client) {
            for (String raw : bind.actions) {

                FastbindParser.ParsedEntry entry = FastbindParser.parse(raw);

                actions.add(() -> {
                    waitUntil = Util.getMeasuringTimeMs() + 10;
                });

                switch (entry.type) {
                    case 1 -> {
                        String cmd = entry.value;
                        if(cmd.isEmpty()) break;
                        if (client.player != null && client.getNetworkHandler() != null) {
                            //? if >=1.19.3 {
                            actions.add(() -> client.player.networkHandler.sendChatCommand(cmd));
                            //? } else if >=1.19 {
                            /*actions.add(() -> client.player.sendCommand(cmd));*/
                            //? } else {
                            /*client.player.networkHandler.sendPacket(new ChatMessageC2SPacket("/" + cmd));*/
                            //? }
                        }
                    }
                    case 2 -> {
                        try {
                            String msText = entry.value;
                            if (msText.isEmpty()) msText = "0";
                            int ms = Integer.parseInt(msText);
                            actions.add(() -> {
                                long end = Util.getMeasuringTimeMs() + ms;
                                waitUntil = end;
                            });
                        } catch (NumberFormatException ignored) {}
                    }
                    case 3 -> {
                        try {
                            int keyCode = Integer.parseInt(entry.value);
                            if (keyCode == 0) break;
                            long handle = MinecraftClient.getInstance().getWindow().getHandle();
                            int scancode = GLFW.glfwGetKeyScancode(keyCode);

                            actions.add(() -> {
                                client.keyboard.onKey(handle, keyCode, scancode, GLFW.GLFW_PRESS, 0);
                            });

                            actions.add(() -> waitUntil = 100);

                            actions.add(() -> {
                                client.keyboard.onKey(handle, keyCode, scancode, GLFW.GLFW_RELEASE, 0);
                            });
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            assert client.player != null;
            if (BindsConfig.getBooleanConfig("bindMsg", true)) {
                client.player.sendMessage(TextUtils.translatable("nitsha.binds.bindActivate", "§3" + bind.name + "§r"), true);
            }
        }


        boolean tick() {
            if (waitUntil > 0) {
                if (Util.getMeasuringTimeMs() < waitUntil) {
                    return false;
                }
                waitUntil = 0;
            }

            if (!actions.isEmpty()) {
                Runnable action = actions.poll();
                if (action != null) {
                    action.run();
                }
            }

            return actions.isEmpty() && waitUntil <= 0;
        }
    }
}
