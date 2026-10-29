package com.nitsha.binds.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<String, List<Consumer<Object[]>>> listeners = new HashMap<>();

    public static void on(String event, Consumer<Object[]> listener) {
        listeners.computeIfAbsent(event, key -> new ArrayList<>()).add(listener);
    }

    public static void off(String event) {
        listeners.remove(event);
    }

    public static void emit(String event, Object... data) {
        listeners.getOrDefault(event, List.of()).forEach(l -> l.accept(data));
    }
}