package com.mcclasses.rpgclassabilities.mixin;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.AttributeCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.Logger;

@Mixin(AttributeCommand.class)
public abstract class DefaultAttributeRegistryMixin {
    @Inject(method = "executeBaseValueSet", at = @At("HEAD"))
    private static void executeBaseValueSetMixin(ServerCommandSource source, Entity target, RegistryEntry<EntityAttribute> attribute, double value, CallbackInfoReturnable<Integer> cir) {
        LoggerHelper.printClassName(target);
    }

//    @Final
//    @Shadow
//    private static Logger LOGGER;
//
//    @Inject(method = "get", at = @At("RETURN"), cancellable = true)
//    private static void ChangeMoveSpeed(EntityType<? extends LivingEntity> type, CallbackInfoReturnable<DefaultAttributeContainer> cir)
//    {
//        if (type == EntityType.PLAYER) {
//            cir.setReturnValue(PlayerEntity.createPlayerAttributes().build());
//            LOGGER.info("DID IT");
//        }
//    }
//    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
//    private static void ChangeMoveSpeed(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir)
//    {
//        LOGGER.info("SPEED ACTIVATED");
//        cir.setReturnValue(cir.getReturnValue().add(EntityAttributes.MOVEMENT_SPEED, 1F));
//    }
//
//    @Inject(method = "get", at = @At("RETURN"))
//    private static void DefaultAttributeContainer getMixin() {
//
//    }
}
