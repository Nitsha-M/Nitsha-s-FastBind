package com.nitsha.binds.bind;

import com.nitsha.binds.FBLogger;
import com.nitsha.binds.action.ActionRegistry;
import com.nitsha.binds.action.ActionType;
import com.nitsha.binds.configs.dto.actions.AllActionsData;
import com.nitsha.binds.configs.dto.preset.ActionData;
import com.nitsha.binds.configs.dto.preset.BindData;
import com.nitsha.binds.configs.Storage;
import com.nitsha.binds.gui.screen.BindsGUI;
import com.nitsha.binds.gui.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.Util;

import java.util.*;

public class BindExecutor {
    private static final List<BindTask> activeTasks = new ArrayList<>();

    public static void startBind(BindData bind) {
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

        BindTask(BindData bind, Minecraft client) {
            buildActions(bind.name, bind.actions, client);

            assert client.player != null;
            if (Storage.options.showActivationMessage) {
                //? if >=26.1 {
                /*client.player.sendSystemMessage(
                        TextUtils.translatable("nitsha.binds.bindActivate", "§3" + bind.name + "§r"));*/
                //? } else {
                client.player.displayClientMessage(
                        TextUtils.translatable("nitsha.binds.bindActivate", "§3" + bind.name + "§r"), true);
                //? }
            }
        }

        private void buildActions(String name, List<ActionData> actionDataList, Minecraft client) {
            for (int i = 0; i < actionDataList.size(); i++) {
                ActionData actionData = actionDataList.get(i);
                if (actionData == null) continue;
                String type = actionData.type;
                if (type == null) continue;
                FBLogger.info("Init action for: " + name + ", type: " + type);

                if (type.equals("loop") && actionData instanceof com.nitsha.binds.configs.dto.actions.AllActionsData.LoopActionData) {
                    com.nitsha.binds.configs.dto.actions.AllActionsData.LoopActionData loopData = (com.nitsha.binds.configs.dto.actions.AllActionsData.LoopActionData) actionData;
                    int actions = Math.min(loopData.value.actions, 10);
                    int count = Math.min(loopData.value.count, 10);
                    FBLogger.info("Found loop action!");
                    FBLogger.info("Loop value: actions — " + actions + ", count — " + count);

                    List<com.nitsha.binds.configs.dto.preset.ActionData> body = new ArrayList<>();
                    int collected = 0;
                    int j = i + 1;
                    while (collected < actions && j < actionDataList.size()) {
                        com.nitsha.binds.configs.dto.preset.ActionData item = actionDataList.get(j);
                        String itemType = item.type;
                        body.add(item);
                        j++;
                        collected++;

                        if ("loop".equals(itemType) && item instanceof AllActionsData.LoopActionData) {
                            AllActionsData.LoopActionData itemLoop = (AllActionsData.LoopActionData) item;
                            int nestedActions = itemLoop.value.actions;
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
                    @SuppressWarnings("unchecked")
                    ActionType<ActionData> action = (ActionType<ActionData>) ActionRegistry.createById(type);
                    action.buildTasks(actionData, this.actions, client, ms -> waitUntil = ms);
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