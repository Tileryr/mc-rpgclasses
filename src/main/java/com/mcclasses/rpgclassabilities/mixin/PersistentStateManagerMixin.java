package com.mcclasses.rpgclassabilities.mixin;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentStateManager.class)
public abstract class PersistentStateManagerMixin {
    @Shadow
    public abstract <T extends PersistentState> T get(PersistentStateType<T> type);

    @Inject(method = "getOrCreate", at = @At("HEAD"))
    private void getOrCreateMixin(PersistentStateType type, CallbackInfoReturnable cir) {
//        PersistentState persistentState = get(type);
//        if (persistentState != null) {
//            LoggerHelper.getLOGGER().info(persistentState.toString());
//        } else {
//            LoggerHelper.getLOGGER().info("State null");
//        }
    }
}
