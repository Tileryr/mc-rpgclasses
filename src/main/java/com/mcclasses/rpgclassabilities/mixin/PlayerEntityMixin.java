package com.mcclasses.rpgclassabilities.mixin;

import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.CanCharge;
import com.mcclasses.rpgclassabilities.playerAbillities.PlayerCharge;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements CanCharge {
    @Unique
    private boolean currentlyCharging;
    @Unique
    private float chargeYaw;
    @Unique
    private int currentChargeTime;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "travel", at = @At("HEAD"), argsOnly = true)
    public Vec3d changeTravelValue(Vec3d value) {
        if (currentlyCharging) {
            return PlayerCharge.getChargeMovement(value);
        } else {
            return value;
        }
    }

    @Inject(method = "attack", at = @At("TAIL"))
    public void attackMixin(Entity target, CallbackInfo ci) {
        PlayerCharge.onAttackEntity(this, target);
    }

    @Unique
    public float scaleVariableCharge(float value, float maxScale) {
        if (currentlyCharging) {
            float chargePercentDone = (float) currentChargeTime / PlayerCharge.CHARGE_LENGTH;
            return value * chargePercentDone * maxScale;
        }
        return value;
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 3)
    public float modifyAttackDamage(float attackDamage) {
        return scaleVariableCharge(attackDamage, (float) PlayerCharge.MAX_CHARGE_DAMAGE_MULTIPLIER);
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), ordinal = 5)
    public float modifyKnockbackAmount(float knockback) {
        return scaleVariableCharge(knockback, (float) PlayerCharge.MAX_CHARGE_KNOCKBACK_MULTIPLIER) + 1.0F;
    }


    @Inject(method = "tick", at = @At("TAIL"))
    public void tickMixin(CallbackInfo ci) {
        if (currentlyCharging) {
            currentChargeTime += 1;
        }
    }

    public void rpgclassabilities$setCurrentlyCharging(boolean charging) {
        currentlyCharging = charging;
    }

    public boolean rpgclassabilities$getCurrentlyCharing() {
        return currentlyCharging;
    }

    public void rpgclassabilities$setChargeYaw(float yaw) {
        chargeYaw = yaw;
    }

    public float rpgclassabilities$getChargeYaw() {
        return chargeYaw;
    }

    public void rpgclassabilities$setCurrentChangeTime(int chargeTime) {
        currentChargeTime = chargeTime;
    }

    public int rpgclassabilities$getCurrentChangeTime() {
        return currentChargeTime;
    }

}
