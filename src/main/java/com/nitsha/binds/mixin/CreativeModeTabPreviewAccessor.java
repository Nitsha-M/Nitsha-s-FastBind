package com.nitsha.binds.mixin;

import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CreativeModeTab.class, remap = false)
public interface CreativeModeTabPreviewAccessor {
    @Accessor("displayItemsGenerator")
    CreativeModeTab.DisplayItemsGenerator nitsha$getDisplayItemsGenerator();
}
