package com.mcclasses.rpgclassabilities;

import com.mcclasses.rpgclassabilities.commands.SetClassCommand;
import com.mcclasses.rpgclassabilities.entities.BindProjectileEntity;
import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mcclasses.rpgclassabilities.payload.PayloadRegister;
import com.mcclasses.rpgclassabilities.payload.c2s.PlayerDashC2SPayload;
import com.mcclasses.rpgclassabilities.payload.c2s.SelectClassC2SPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.OpenClassSelectS2CPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.UpdateCurrentClassS2CPayload;
import com.mcclasses.rpgclassabilities.playerAbillities.PlayerDash;
import com.mcclasses.rpgclassabilities.timers.TickScheduler;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


public class Rpgclassabilities implements ModInitializer {
    public static final TickScheduler SCHEDULER = new TickScheduler();
    public static final String MOD_ID = "rpgclassabilities";

    public static final Identifier BIND_PROJECTILE_ID = Identifier.of(MOD_ID, "bind_projectile");
    public static final EntityType<BindProjectileEntity> BIND_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            BIND_PROJECTILE_ID,
            EntityType.Builder
                    .create(BindProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(1F, 1F)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, BIND_PROJECTILE_ID))
            );

    public static void setRpgClass(RpgClass rpgClass, ServerPlayerEntity player) {
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player, true);
        playerData.playerClass = rpgClass;
        playerData.classSelected = true;

        UpdateCurrentClassS2CPayload updateClassPayload = new UpdateCurrentClassS2CPayload(rpgClass);
        ServerPlayNetworking.send(player, updateClassPayload);
    }

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(SCHEDULER);
        PayloadRegister.register();
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "dash_smoke"), FabricParticleTypes.simple());

        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            ServerPlayerEntity player = serverPlayNetworkHandler.player;
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player, false);

            if (!playerData.classSelected) {
                OpenClassSelectS2CPayload openClassSelectPayload = new OpenClassSelectS2CPayload();
                ServerPlayNetworking.send(player, openClassSelectPayload);
            } else {
                setRpgClass(playerData.playerClass, player);
            }

            serverPlayNetworkHandler.player.getAttributes().getCustomInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.1);
        });

        ServerPlayNetworking.registerGlobalReceiver(SelectClassC2SPayload.ID, (payload, context) -> {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(context.player(), true);
            if (!playerData.classSelected) {
                setRpgClass(payload.rpgClass(), context.player());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(PlayerDashC2SPayload.ID, ((payload, context) -> {
            new PlayerDash(context.player());
        }));

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
