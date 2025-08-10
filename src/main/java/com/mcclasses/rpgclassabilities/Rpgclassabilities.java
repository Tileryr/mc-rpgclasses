package com.mcclasses.rpgclassabilities;

import com.mcclasses.rpgclassabilities.commands.SetClassCommand;
import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mcclasses.rpgclassabilities.payload.PayloadRegister;
import com.mcclasses.rpgclassabilities.payload.c2s.SelectClassC2SPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.OpenClassSelectS2CPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.UpdateCurrentClassS2CPayload;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;


public class Rpgclassabilities implements ModInitializer {
    public static final String MOD_ID = "rpgclassabilities";

    public static void setRpgClass(RpgClass rpgClass, ServerPlayerEntity player) {
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player, true);
        playerData.playerClass = rpgClass;
        playerData.classSelected = true;

        UpdateCurrentClassS2CPayload updateClassPayload = new UpdateCurrentClassS2CPayload(rpgClass);
        ServerPlayNetworking.send(player, updateClassPayload);
    }

    @Override
    public void onInitialize() {
        PayloadRegister.register();
//        PayloadTypeRegistry.playS2C().register(OpenClassSelectS2CPayload.ID, OpenClassSelectS2CPayload.CODEC);
//        PayloadTypeRegistry.playS2C().register(UpdateCurrentClassS2CPayload.ID, UpdateCurrentClassS2CPayload.CODEC);
//        PayloadTypeRegistry.playC2S().register(SelectClassC2SPayload.ID, SelectClassC2SPayload.CODEC);

        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            ServerPlayerEntity player = serverPlayNetworkHandler.player;
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player, false);

            if (!playerData.classSelected) {
                OpenClassSelectS2CPayload openClassSelectPayload = new OpenClassSelectS2CPayload();
                ServerPlayNetworking.send(player, openClassSelectPayload);
            } else {
                setRpgClass(playerData.playerClass, player);
            }

            serverPlayNetworkHandler.player.getAttributes().getCustomInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(1.0);
        });

        ServerPlayNetworking.registerGlobalReceiver(SelectClassC2SPayload.ID, (payload, context) -> {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(context.player(), true);
            if (!playerData.classSelected) {
                setRpgClass(payload.rpgClass(), context.player());
            }
        });

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("setclass")
                    .requires(source -> source.hasPermissionLevel(1))
                    .then(CommandManager.argument("new_class", StringArgumentType.word())
                            .suggests(new SetClassCommand.RpgClassSuggestionProvider())
                            .executes(context -> {
                                String newClass = StringArgumentType.getString(context, "new_class");
                                return SetClassCommand.setClass(newClass, context);
                            }))
            );
        }));
    }
}
