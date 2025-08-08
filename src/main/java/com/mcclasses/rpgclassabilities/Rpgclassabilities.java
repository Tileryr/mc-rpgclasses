package com.mcclasses.rpgclassabilities;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.math.DirectionTransformation;


public class Rpgclassabilities implements ModInitializer {
    public static final String MOD_ID = "rpgclassabilities";



    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(SelectClassC2SPayload.ID, SelectClassC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SelectClassC2SPayload.ID, (payload, context) -> {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(context.player());
            if (playerData.playerClass == null) {
                context.player().sendMessage(Text.literal("Save Failed"));
            } else {
                context.player().sendMessage(Text.literal(playerData.playerClass.asString()));
            }

            playerData.playerClass = payload.rpgClass();

            context.player().sendMessage(Text.literal(payload.rpgClass().asString()));
                });

        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            serverPlayNetworkHandler.player.getAttributes().getCustomInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(1.0);
            LoggerHelper.getLOGGER().info("ServerConnected");
        });
    }
}
