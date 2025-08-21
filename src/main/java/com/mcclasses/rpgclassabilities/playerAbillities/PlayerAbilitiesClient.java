package com.mcclasses.rpgclassabilities.playerAbillities;

import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.FovOveridable;
import com.mcclasses.rpgclassabilities.client.RpgclassabilitiesClient;
import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mcclasses.rpgclassabilities.payload.PayloadRegister;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAbilitiesClient {
    private final Map<UUID, Boolean> canUseAbilityOne = new HashMap<>();

    private boolean playerCanUseAbilityOne(UUID uuid) {
        return canUseAbilityOne.computeIfAbsent(uuid, (uuid_) -> true);
    }

    public void register() {
        ClientPlayNetworking.registerGlobalReceiver(PayloadRegister.ABILITY_ONE_ACTIVE.id, (payload, context) -> {
            canUseAbilityOne.put(context.player().getUuid(), true);
        });
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
        if (!playerCanUseAbilityOne(player.getUuid())) {
            return;
        }

        canUseAbilityOne.put(player.getUuid(), false);

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
