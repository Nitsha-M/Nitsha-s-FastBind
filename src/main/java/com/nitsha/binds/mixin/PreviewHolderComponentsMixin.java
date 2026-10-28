package com.nitsha.binds.mixin;

import com.nitsha.binds.preview.PreviewComponentContext;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Holder.Reference.class, remap = false)
public abstract class PreviewHolderComponentsMixin {
    @Inject(method = "components", at = @At("HEAD"), cancellable = true)
    private void nitsha$previewComponents(CallbackInfoReturnable<DataComponentMap> callback) {
        DataComponentMap components = PreviewComponentContext.getComponents((Holder<?>) (Object) this);

        if (components != null) {
            callback.setReturnValue(components);
        }
    }

    @Inject(method = "areComponentsBound", at = @At("HEAD"), cancellable = true)
    private void nitsha$previewComponentsBound(CallbackInfoReturnable<Boolean> callback) {
        if (PreviewComponentContext.getComponents((Holder<?>) (Object) this) != null) {
            callback.setReturnValue(true);
        }
    }
}
