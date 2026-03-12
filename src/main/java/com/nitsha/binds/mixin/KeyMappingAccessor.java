package com.nitsha.binds.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
    @Accessor("key")
    InputConstants.Key nitsha$getKey();
    @Accessor("clickCount")
    void binds$setClickCount(int count);
    @Accessor("clickCount")
    int binds$getClickCount();
    @Accessor("ALL")
    static Map<String, KeyMapping> binds$getAll() { throw new AssertionError(); }
}
