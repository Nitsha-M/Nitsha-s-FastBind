package com.nitsha.binds.utils;

import com.mojang.blaze3d.platform.InputConstants;
import com.nitsha.binds.configs.Bind;
import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.gui.screen.BindsGUI;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
//? if <1.19.3 {
/*import net.minecraft.network.protocol.game.ServerboundChatPacket;*/
//?}
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class BindExecutor {
    private static final List<BindTask> activeTasks = new ArrayList<>();

    public static void startBind(Bind bind) {
        Minecraft client = Minecraft.getInstance();
        assert client.player != null;

        if ((client.screen != null && !(client.screen instanceof BindsGUI)) || bind.actions.isEmpty())
            return;

        BindTask task = new BindTask(bind, client);
        activeTasks.add(task);
    }

    //? if fabric {
    public static void onTick(Minecraft client) {
        activeTasks.removeIf(BindTask::tick);
    }
    //? } elif neoforge {
    //? if >1.20.4 {
    /*public static void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        activeTasks.removeIf(BindTask::tick);
    }*/
    //? } else {
    /*public static void onClientTick(net.neoforged.neoforge.event.TickEvent.ClientTickEvent event) {
        activeTasks.removeIf(BindTask::tick);
    }*/
    //? }
    //? } else {
    /*public static void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        activeTasks.removeIf(BindTask::tick);
    }*/
    //? }

    private static class BindTask {
        private final Queue<Runnable> actions = new LinkedList<>();
        private long waitUntil = 0;

        BindTask(Bind bind, Minecraft client) {
            for (Map<String, Object> action : bind.actions) {
                String type = (String) action.get("type");
                Object value = action.get("value");

                switch (type) {
                    case "command":
                        String cmd = String.valueOf(value);
                        if (cmd.isEmpty())
                            break;
                        if (client.player != null && client.getConnection() != null) {
                            //? if >=1.19.3 {
                            actions.add(() -> client.player.connection.sendCommand(cmd));
                            //? } else if >=1.19.1 {
                            /*actions.add(() -> client.player.commandUnsigned(cmd));*/
                            //? } else if >=1.19 {
                            /*actions.add(() -> client.player.command(cmd));*/
                            //? } else {
                            /*actions.add(() -> client.player.connection.send(new ServerboundChatPacket("/" + cmd)));*/
                            //? }
                        }
                    break;
                    case "delay":
                        try {
                            int ms;
                            if (value instanceof Number) {
                                ms = ((Number) value).intValue();
                            } else {
                                String msText = String.valueOf(value);
                                if (msText.isEmpty())
                                    msText = "0";
                                ms = Integer.parseInt(msText);
                            }
                            actions.add(() -> {
                                long end = Util.getMillis() + ms;
                                waitUntil = end;
                            });
                        } catch (NumberFormatException ignored) {
                        }
                    break;
                    case "keybind":
                        try {
                            int keyCode;
                            if (value instanceof Number) {
                                keyCode = ((Number) value).intValue();
                            } else {
                                keyCode = Integer.parseInt(String.valueOf(value));
                            }
                            if (keyCode == 0)
                                break;

                            //? if >=1.21.9 {
                            /*int scancode = GLFW.glfwGetKeyScancode(keyCode);
                            actions.add(() -> {
                                InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(keyCode);
                                KeyMapping.set(key, true);
                                KeyMapping.click(key);
                            });
                            actions.add(() -> waitUntil = 100);
                            actions.add(() -> {
                                InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(keyCode);
                                KeyMapping.set(key, false);
                            });*/
                            //? } else {
                            int scancode = GLFW.glfwGetKeyScancode(keyCode);
                            actions.add(() -> {
                                InputConstants.Key key = InputConstants.getKey(keyCode, scancode);
                                KeyMapping.set(key, true);
                                KeyMapping.click(key);
                            });
                            actions.add(() -> waitUntil = 100);
                            actions.add(() -> {
                                InputConstants.Key key = InputConstants.getKey(keyCode, scancode);
                                KeyMapping.set(key, false);
                            });
                            //? }
                        } catch (NumberFormatException ignored) {
                        }
                    break;
                    case "chatMessage":
                        MutableComponent message;
                        if (value instanceof Map) {
                            Map<String, Object> formattedText = (Map<String, Object>) value;
                            String text = (String) formattedText.get("text");
                            message = TextUtils.empty();

                            if (formattedText.containsKey("marks")) {
                                List<Map<String, Object>> marks = (List<Map<String, Object>>) formattedText.get("marks");
                                Style currentStyle = Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));
                                int lastPos = 0;

                                for (Map<String, Object> markData : marks) {
                                    int pos = ((Number) markData.get("pos")).intValue();
                                    int styleCode = ((Number) markData.get("style")).intValue();

                                    if (pos > lastPos) {
                                        message = message.append(
                                                TextUtils.literal(text.substring(lastPos, pos)).setStyle(currentStyle));
                                    }

                                    currentStyle = applyStyleCode(styleCode, currentStyle);
                                    lastPos = pos;
                                }

                                if (lastPos < text.length()) {
                                    message = message
                                            .append(TextUtils.literal(text.substring(lastPos)).setStyle(currentStyle));
                                }
                            } else {
                                message = TextUtils.literal(text);
                            }
                        } else {
                            message = TextUtils.literal(String.valueOf(value));
                        }

                        final MutableComponent finalMessage = message;

                        actions.add(() -> {
                            if (client.player != null) {
                                client.player.displayClientMessage(finalMessage, false);
                            }
                        });
                        break;
                    case "titleMessage":
                        if (value instanceof Map) {
                            Map<String, Object> titleData = (Map<String, Object>) value;

                            MutableComponent titleComponent = TextUtils.empty();
                            if (titleData.containsKey("title") && titleData.get("title") instanceof Map) {
                                Map<String, Object> titleFormatted = (Map<String, Object>) titleData.get("title");
                                titleComponent = buildFormattedComponent(titleFormatted);
                            }

                            MutableComponent subtitleComponent = TextUtils.empty();
                            if (titleData.containsKey("subtitle") && titleData.get("subtitle") instanceof Map) {
                                Map<String, Object> subtitleFormatted = (Map<String, Object>) titleData.get("subtitle");
                                subtitleComponent = buildFormattedComponent(subtitleFormatted);
                            }

                            final MutableComponent finalTitle = titleComponent;
                            final MutableComponent finalSubtitle = subtitleComponent;

                            if (!finalTitle.getString().isEmpty() || !finalSubtitle.getString().isEmpty()) {
                                //? if >=1.17 {
                                actions.add(() -> {
                                    if (client.gui != null) {
                                        //? if >=1.21.4 {
                                        // client.gui.clearTitles();
                                        //?} else {
                                        client.gui.clear();
                                        //?}
                                        client.gui.setTitle(finalTitle);
                                        client.gui.setSubtitle(finalSubtitle);
                                        client.gui.setTimes(10, 70, 20);
                                    }
                                });
                                //? } else {
                                /*actions.add(() -> {
                                    if (client.gui != null) {
                                        client.gui.setTitles(null, null, 10, 70, 20);
                                        client.gui.setTitles(finalTitle, null, -1, -1, -1);
                                        client.gui.setTitles(null, finalSubtitle, -1, -1, -1);
                                    }
                                });*/
                                //? }
                            }
                        }
                    break;
                }
            }
            assert client.player != null;
            if (BindsStorage.getBooleanConfig("bindMsg", true)) {
                client.player.displayClientMessage(
                        TextUtils.translatable("nitsha.binds.bindActivate", "§3" + bind.name + "§r"), true);
            }
        }

        boolean tick() {
            if (waitUntil > 0) {
                if (Util.getMillis() < waitUntil) {
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

        private static Style applyStyleCode(int code, Style baseStyle) {
            if (code >= 0 && code <= 15) {
                int[] colors = {
                        0x1a1a1a, 0x0000AA, 0x00AA00, 0x00AAAA,
                        0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
                        0x555555, 0x5555FF, 0x55FF55, 0x55FFFF,
                        0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
                };
                Style newStyle = baseStyle.withColor(TextColor.fromRgb(colors[code]));
                if (baseStyle.isBold())
                    newStyle = newStyle.withBold(true);
                if (baseStyle.isItalic())
                    newStyle = newStyle.withItalic(true);
                if (baseStyle.isUnderlined())
                    newStyle = newStyle.withUnderlined(true);
                //? if >=1.17 {
                if (baseStyle.isStrikethrough())
                    newStyle = newStyle.withStrikethrough(true);
                if (baseStyle.isObfuscated())
                    newStyle = newStyle.withObfuscated(true);
                //?} else {
                /*if (baseStyle.isStrikethrough())
                    newStyle = newStyle.applyFormat(ChatFormatting.STRIKETHROUGH);
                if (baseStyle.isObfuscated())
                    newStyle = newStyle.applyFormat(ChatFormatting.OBFUSCATED);*/
                    //?}
                return newStyle;
            }

            switch (code) {
                case 20:
                    return baseStyle.withBold(true);
                case 21:
                    return baseStyle.withItalic(true);
                case 22:
                    return baseStyle.withUnderlined(true);
                case 23:
                    //? if >=1.17 {
                    return baseStyle.withStrikethrough(true);
                    //?} else {
                    /*return baseStyle.applyFormat(ChatFormatting.STRIKETHROUGH);*/
                    //?}
                case 24:
                    //? if >=1.17 {
                    return baseStyle.withObfuscated(true);
                    //?} else {
                    /*return baseStyle.applyFormat(ChatFormatting.OBFUSCATED);*/
                    //?}
                case 99:
                    return Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));
                default:
                    return baseStyle;
            }
        }

        private static MutableComponent buildFormattedComponent(Map<String, Object> formattedText) {
            String text = (String) formattedText.get("text");
            MutableComponent message = TextUtils.empty();

            if (text.isEmpty()) {
                return message;
            }

            if (!formattedText.containsKey("marks")) {
                return TextUtils.literal(text);
            }

            List<Map<String, Object>> marks = (List<Map<String, Object>>) formattedText.get("marks");
            if (marks.isEmpty()) {
                return TextUtils.literal(text);
            }

            marks.sort((a, b) -> {
                int posA = ((Number) a.get("pos")).intValue();
                int posB = ((Number) b.get("pos")).intValue();
                return Integer.compare(posA, posB);
            });

            Style currentStyle = Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF));
            int lastPos = 0;

            for (Map<String, Object> markData : marks) {
                int pos = ((Number) markData.get("pos")).intValue();
                int styleCode = ((Number) markData.get("style")).intValue();

                if (pos > lastPos && pos <= text.length()) {
                    message = message.append(TextUtils.literal(text.substring(lastPos, pos)).setStyle(currentStyle));
                }

                currentStyle = applyStyleCode(styleCode, currentStyle);
                lastPos = pos;
            }

            if (lastPos < text.length()) {
                message = message.append(TextUtils.literal(text.substring(lastPos)).setStyle(currentStyle));
            }

            return message;
        }

    }
}