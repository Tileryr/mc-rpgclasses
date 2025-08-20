package com.mcclasses.rpgclassabilities.mixin;

import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.CanCharge;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    protected static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {return null;}
    @Shadow
    public abstract Vec3d getVelocity();
    @Shadow
    public abstract void setVelocity(Vec3d value);

    @Inject(method = "updateVelocity", at = @At("HEAD"), cancellable = true)
    public void updateVelocity(float speed, Vec3d movementInput, CallbackInfo ci) {
        if (this instanceof CanCharge canCharge && canCharge.rpgclassabilities$getCurrentlyCharing()) {
            Vec3d vec3d = movementInputToVelocity(movementInput, speed, canCharge.rpgclassabilities$getChargeYaw());
            this.setVelocity(this.getVelocity().add(vec3d));
            ci.cancel();
        }
    }
}
