package com.mcclasses.rpgclassabilities.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.apache.commons.logging.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.Logger;

@Mixin(PlayerEntity.class)
public abstract class PlayerMoveSpeedMixin extends LivingEntity {
    @Shadow
    public abstract float getMovementSpeed();

    @Unique
    private static final String MOD_ID = "rpgclassabilities";
    @Unique
    private static final Logger LOGGER = Logger.getLogger(MOD_ID);

    protected PlayerMoveSpeedMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void ChangeMoveSpeed(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir)
    {
        LOGGER.info("SPEED ACTIVATED");
        cir.setReturnValue(cir.getReturnValue().add(EntityAttributes.MOVEMENT_SPEED, 1F));
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setMovementSpeed(F)V"))
    private void hiHello(CallbackInfo ci)
    {
        this.setMovementSpeed(this.getMovementSpeed() + 1F);
//        LOGGER.info("idk");
    }
}
