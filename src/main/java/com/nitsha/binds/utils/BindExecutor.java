package com.nitsha.binds.utils;

import com.nitsha.binds.FBLogger;
import com.nitsha.binds.action.ActionRegistry;
import com.nitsha.binds.action.ActionType;
import com.nitsha.binds.bind.Bind;
import com.nitsha.binds.configs.BindsStorage;
import com.nitsha.binds.gui.screen.BindsGUI;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.Util;

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
        FBLogger.info("Active bind " + bind.name);
    }

    public static void tick() {
        activeTasks.removeIf(BindTask::tick);
    }

    private static class BindTask {
        private final Queue<Runnable> actions = new LinkedList<>();
        private long waitUntil = 0;

        BindTask(Bind bind, Minecraft client) {
            buildActions(bind.name, bind.actions, client);

            assert client.player != null;
            if (BindsStorage.getBooleanConfig("bindMsg", true)) {
                //? if >=26.1 {
                /*client.player.sendSystemMessage(
                        TextUtils.translatable("nitsha.binds.bindActivate", "§3" + bind.name + "§r"));*/
                //? } else {
                client.player.displayClientMessage(
                        TextUtils.translatable("nitsha.binds.bindActivate", "§3" + bind.name + "§r"), true);
                //? }
            }
        }

        private void buildActions(String name, List<Map<String, Object>> actionDataList, Minecraft client) {
            for (int i = 0; i < actionDataList.size(); i++) {
                Map<String, Object> actionData = actionDataList.get(i);
                String type = (String) actionData.get("type");
                if (type == null) continue;
                FBLogger.info("Init action for: " + name + ", type: " + type);

                if (type.equals("loop")) {
                    Map<String, Object> loopValue = (Map<String, Object>) actionData.getOrDefault("value", Map.of());
                    int actions = Math.min(((Number) loopValue.getOrDefault("actions", 1)).intValue(), 10);
                    int count = Math.min(((Number) loopValue.getOrDefault("count", 1)).intValue(), 10);
                    FBLogger.info("Found loop action!");
                    FBLogger.info("Loop value: actions — " + actions + ", count — " + count);

                    List<Map<String, Object>> body = new ArrayList<>();
                    int collected = 0;
                    int j = i + 1;
                    while (collected < actions && j < actionDataList.size()) {
                        Map<String, Object> item = actionDataList.get(j);
                        String itemType = (String) item.get("type");
                        body.add(item);
                        j++;
                        collected++;

                        if ("loop".equals(itemType)) {
                            Map<String, Object> nestedValue = (Map<String, Object>) item.getOrDefault("value", Map.of());
                            int nestedActions = ((Number) nestedValue.getOrDefault("actions", 1)).intValue();
                            int nestedAvailable = actionDataList.size() - j;
                            int nestedActual = Math.min(nestedActions, nestedAvailable);
                            for (int k = 0; k < nestedActual; k++) {
                                body.add(actionDataList.get(j));
                                j++;
                            }
                        }
                    }
                    i = j - 1;

                    for (int r = 0; r < count; r++) {
                        buildActions(name, body, client);
                    }
                    continue;
                }

                try {
                    ActionType action = ActionRegistry.createById(type);
                    action.buildTasks(actionData, actions, client, ms -> waitUntil = ms);
                } catch (IllegalArgumentException ignored) {}
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
    }
}