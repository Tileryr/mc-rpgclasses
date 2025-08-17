package com.mcclasses.rpgclassabilities.playerAbillities;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.entities.BindProjectileEntity;
import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mcclasses.rpgclassabilities.timers.TickScheduler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

    public Optional<Integer> runAbilityOne(RpgClass rpgClass, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        UUID playerUuid = player.getUuid();

        if (!playerCanUseAbilityOne(playerUuid)) {
            return Optional.of(scheduler.getTicksLeft(playerUuid));
        }

        canUseAbilityOne.put(playerUuid, false);

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
        }

        scheduler.addTimer(playerUuid, rpgClass.abilityOneCooldown, () -> canUseAbilityOne.put(playerUuid, true));
        return Optional.empty();
    }
}
