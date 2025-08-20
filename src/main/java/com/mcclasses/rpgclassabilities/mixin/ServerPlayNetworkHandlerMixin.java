package com.mcclasses.rpgclassabilities.mixin;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.playerAbillities.PlayerCharge;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @ModifyVariable(method = "onPlayerMove", at = @At("HEAD"), argsOnly = true)
    private PlayerMoveC2SPacket onPlayerMove(PlayerMoveC2SPacket playerMovePacket) {
        return playerMovePacket;
//        return PlayerCharge.overridePlayerMovement(playerMovePacket, player);
    }
}
