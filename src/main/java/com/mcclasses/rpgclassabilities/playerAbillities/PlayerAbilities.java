package com.mcclasses.rpgclassabilities.playerAbillities;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.CanCharge;
import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.FovOveridable;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.client.CurrentRpgClass;
import com.mcclasses.rpgclassabilities.client.RpgclassabilitiesClient;
import com.mcclasses.rpgclassabilities.entities.BindProjectileEntity;
import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mcclasses.rpgclassabilities.payload.PayloadRegister;
import com.mcclasses.rpgclassabilities.timers.TickScheduler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerAbilities {
    private final TickScheduler scheduler;

    private final Map<UUID, Boolean> canUseAbilityOne = new HashMap<>();

    public PlayerAbilities(TickScheduler scheduler) {
        this.scheduler = scheduler;
    }

    private boolean playerCanUseAbilityOne(UUID uuid, boolean isClient) {
        return canUseAbilityOne.computeIfAbsent(uuid, (uuid_) -> true);
    }

    public void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(PayloadRegister.ABILITY_ONE_ACTIVE.id, (payload, context) -> {
            canUseAbilityOne.put(context.player().getUuid(), true);
        });
    }

    private boolean updateAbilityOneTimer(PlayerEntity player, RpgClass rpgClass, boolean isClient) {
        UUID playerUuid = player.getUuid();
        if (!isClient) {
            if (!playerCanUseAbilityOne(playerUuid, false)) {
                return false;
            }
            canUseAbilityOne.put(playerUuid, false);
            scheduler.addTimer(playerUuid, rpgClass.abilityOneCooldown, () -> {
                canUseAbilityOne.put(playerUuid, true);
                ServerPlayNetworking.send((ServerPlayerEntity) player, PayloadRegister.ABILITY_ONE_ACTIVE);
            });

            return true;
        } else {
            canUseAbilityOne.put(playerUuid, false);
            return playerCanUseAbilityOne(playerUuid, true);
        }
    }

    public Optional<Integer> runAbilityOne(RpgClass rpgClass, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if (!updateAbilityOneTimer(player, rpgClass, false)) {
            return Optional.of(scheduler.getTicksLeft(player.getUuid()));
        }

        try {
            switch (rpgClass) {
                case CLERIC -> {
                    LoggerHelper.getLOGGER().info("CLERIC");
                    BindProjectileEntity bindProjectileEntity = new BindProjectileEntity(
                            player.getWorld(),
                            player,
                            player.getRotationVector()
                    );
                    player.getWorld().spawnEntity(bindProjectileEntity);
                    bindProjectileEntity.setPosition(player.getPos().add(0, 1.5, 0));
                }
                case ROGUE -> new PlayerDash(player);
                case WARRIOR -> new PlayerCharge(player, Rpgclassabilities.SCHEDULER).charge();
                case WIZARD -> new PlayerTransmute(player).transmute();
            }
        } catch (Exception e) {
            canUseAbilityOne.put(player.getUuid(), true);
        }

        return Optional.empty();
    }

    private static void dashPlayer(ClientPlayerEntity clientPlayer) {
        if (clientPlayer instanceof FovOveridable player) {
            player.rpgclassabilities$setFovOverrideValue(1.6F);
            player.rpgclassabilities$setOverrideFov(true);

            RpgclassabilitiesClient.SCHEDULER.addTimer(PlayerDash.DASH_TIME, () -> {
                player.rpgclassabilities$setOverrideFov(false);
            });
        }
    }

    public void runAbilityOneClient(RpgClass rpgClass, ClientPlayerEntity player) {
        if (!updateAbilityOneTimer(player, rpgClass, true)) {
            return;
        }

        switch (rpgClass) {
            case CLERIC -> {}
            case ROGUE -> {
                dashPlayer(player);
            }
            case WARRIOR -> new PlayerCharge(player, RpgclassabilitiesClient.SCHEDULER).charge();
            case WIZARD -> new PlayerTransmute(player).transmute();
        }
    }
}
