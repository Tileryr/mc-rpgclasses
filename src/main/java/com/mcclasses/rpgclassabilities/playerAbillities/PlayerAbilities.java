package com.mcclasses.rpgclassabilities.playerAbillities;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.entities.BindProjectileEntity;
import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mcclasses.rpgclassabilities.payload.PayloadRegister;
import com.mcclasses.rpgclassabilities.timers.TickScheduler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

    private boolean playerCanUseAbilityOne(UUID uuid) {
        return canUseAbilityOne.computeIfAbsent(uuid, (uuid_) -> true);
    }

    private boolean updateAbilityOneTimer(PlayerEntity player, RpgClass rpgClass) {
        UUID playerUuid = player.getUuid();
        if (!playerCanUseAbilityOne(playerUuid)) {
            return false;
        }
        canUseAbilityOne.put(playerUuid, false);
        scheduler.addTimer(playerUuid, rpgClass.abilityOneCooldown, () -> {
            canUseAbilityOne.put(playerUuid, true);
            ServerPlayNetworking.send((ServerPlayerEntity) player, PayloadRegister.ABILITY_ONE_ACTIVE);
        });

        return true;
    }

    public Optional<Integer> runAbilityOne(RpgClass rpgClass, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if (!updateAbilityOneTimer(player, rpgClass)) {
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
}
