//? if !fabric {
/*package com.nitsha.binds.mixin;

import com.nitsha.binds.utils.KeepMovementHandler;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
//? if neoforge {
// import net.neoforged.neoforge.client.extensions.IKeyMappingExtension;
//? } else if forge {
// import net.minecraftforge.client.extensions.IForgeKeyMapping;
//? }

@Mixin(KeyMapping.class)
//? if neoforge {
// public abstract class KeyMappingIsDownMixin implements IKeyMappingExtension {
//? } else if forge {
// public abstract class KeyMappingIsDownMixin implements IForgeKeyMapping {
//? }

    @Override
    public boolean isConflictContextAndModifierActive() {
        boolean originalResult;
        //? if neoforge {
        originalResult = IKeyMappingExtension.super.isConflictContextAndModifierActive();
        //? } else if forge {
        originalResult = IForgeKeyMapping.super.isConflictContextAndModifierActive();
        //? } else {
        originalResult = true;
        //? }

        if (!originalResult && KeepMovementHandler.shouldKeepMovement()) return true;

        return originalResult;
    }
}*/
//? } else {
package com.nitsha.binds.mixin;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(KeyMapping.class)
public abstract class KeyMappingIsDownMixin {
}
//? }