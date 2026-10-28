package com.nitsha.binds.preview;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;

import java.util.Map;

public final class PreviewComponentContext {
    private static final ScopedValue<Map<Holder<?>, DataComponentMap>> COMPONENTS = ScopedValue.newInstance();

    private PreviewComponentContext() {
    }

    public static void run(Map<Holder<?>, DataComponentMap> components, Runnable task) {
        ScopedValue.where(COMPONENTS, Map.copyOf(components)).run(task);
    }

    public static DataComponentMap getComponents(Holder<?> holder) {
        if (!COMPONENTS.isBound()) {
            return null;
        }

        return COMPONENTS.get().get(holder);
    }
}
