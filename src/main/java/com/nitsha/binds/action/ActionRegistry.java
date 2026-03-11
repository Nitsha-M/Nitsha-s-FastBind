package com.nitsha.binds.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ActionRegistry {

    private static final Map<String, Supplier<ActionType>> BY_ID = new LinkedHashMap<>();
    private static final Map<Integer, Supplier<ActionType>> BY_INDEX = new LinkedHashMap<>();

    static {
        register(1, CommandAction::new);
        register(2, DelayAction::new);
        register(3, KeyPressAction::new);
        register(4, KeyDownAction::new);
        register(5, KeyUpAction::new);
        register(6, ChatMessageAction::new);
        register(7, TitleMessageAction::new);
        register(8, KeyEventAction::new);
        register(9, LoopAction::new);
    }

    private static void register(int index, Supplier<ActionType> factory) {
        ActionType sample = factory.get();
        BY_ID.put(sample.getId(), factory);
        BY_INDEX.put(index, factory);
    }

    /** Создаёт новый экземпляр по строковому id ("command", "delay" и т.д.) */
    public static ActionType createById(String id) {
        Supplier<ActionType> factory = BY_ID.get(id);
        if (factory == null) throw new IllegalArgumentException("Unknown action id: " + id);
        return factory.get();
    }

    /** Создаёт новый экземпляр по числовому индексу (1, 2, 3 и т.д.) */
    public static ActionType createByIndex(int index) {
        Supplier<ActionType> factory = BY_INDEX.get(index);
        if (factory == null) throw new IllegalArgumentException("Unknown action index: " + index);
        return factory.get();
    }

    /** Создаёт по одному экземпляру каждого типа — для меню выбора экшена */
    public static List<ActionType> allInstances() {
        List<ActionType> list = new ArrayList<>();
        for (Supplier<ActionType> factory : BY_ID.values()) {
            list.add(factory.get());
        }
        return list;
    }

    /** Высота виджета по id — используется в generateActionList */
    public static int heightById(String id) {
        return createById(id).getHeight();
    }

    /** Все зарегистрированные id — например для меню добавления экшена */
    public static Collection<String> allIds() {
        return BY_ID.keySet();
    }
}