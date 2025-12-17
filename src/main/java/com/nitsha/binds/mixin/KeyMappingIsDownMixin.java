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
        if (KeepMovementHandler.shouldKeepMovement()) {
            return true;
        } else {
            //? if neoforge {
            // return IKeyMappingExtension.super.isConflictContextAndModifierActive();
            //? } else if forge {
            // return IForgeKeyMapping.super.isConflictContextAndModifierActive();
            //? }
        }
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