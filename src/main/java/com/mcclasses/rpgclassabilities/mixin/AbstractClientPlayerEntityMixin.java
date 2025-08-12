package com.mcclasses.rpgclassabilities.mixin;

import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.FovOveridable;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin implements FovOveridable {
    @Unique
    boolean overrideFov = false;
    @Unique
    float fovOverrideValue = 1;

    @Inject(method = "getFovMultiplier", at = @At("HEAD"), cancellable = true)
    public void getFovMultiplier(boolean firstPerson, float fovEffectScale, CallbackInfoReturnable<Float> cir) {
        if (overrideFov) {
            cir.setReturnValue(MathHelper.lerp(fovEffectScale, 1.0F, fovOverrideValue));
        }
    }

    @Override
    public void rpgclassabilities$setOverrideFov(boolean _overrideFov) {
        overrideFov = _overrideFov;
    }

    @Override
    public void rpgclassabilities$setFovOverrideValue(float _fovOverrideValue) {
        fovOverrideValue = _fovOverrideValue;
    }

    @Override
    public boolean rpgclassabilities$getOverrideFov() {
        return overrideFov;
    }
}
