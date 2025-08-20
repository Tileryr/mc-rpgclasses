package com.mcclasses.rpgclassabilities.mixin;

import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.CanCharge;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    public void setSprintingMixin(boolean sprinting, CallbackInfo ci) {
        if (this instanceof CanCharge chargeable && chargeable.rpgclassabilities$getCurrentlyCharing()) {
            ci.cancel();
        }
    }
}
