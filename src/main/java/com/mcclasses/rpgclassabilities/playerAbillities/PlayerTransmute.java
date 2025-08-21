package com.mcclasses.rpgclassabilities.playerAbillities;

import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.stream.IntStream;

public class PlayerTransmute {
    private final PlayerEntity player;

    public static final Identifier TRANSMUTE_PARTICLE_ID = Identifier.of(Rpgclassabilities.MOD_ID, "transmute_particle");
    public static final SimpleParticleType TRANSMUTE_PARTICLE = FabricParticleTypes.simple();

    private final int EFFECTS_PER_30_LEVELS = 2;
    private final int DURATION_PER_30_LEVELS = 60;
    private final int AMPLIFIER_PER_30_LEVELS = 2;

    public PlayerTransmute(PlayerEntity player) {
        this.player = player;
    }

    public void transmute() {
        transmute(player);
    }

    public void transmute(PlayerEntity player) {
        World playerWorld = player.getWorld();
        int experienceLevel = player.experienceLevel;

        if (experienceLevel == 0) {
            return;
        }

        player.experienceLevel = 0;
        player.experienceProgress = 0.0F;

        if (player.getWorld().isClient) {
        } else {
            if (playerWorld instanceof ServerWorld serverWorld) {

                serverWorld.spawnParticles(
                        TRANSMUTE_PARTICLE,
                        player.getX(), player.getY(), player.getZ(),
                        experienceLevel * 2, 0.2, 1.5, 0.2, 0.05);

            }

            int statusEffects = getEffectCount(experienceLevel);
            while (statusEffects != 0) {
                Registry<StatusEffect> registry = playerWorld.getRegistryManager().getOrThrow(RegistryKeys.STATUS_EFFECT);
                Optional<RegistryEntry.Reference<StatusEffect>> optionalRandomStatusEffect = registry.getRandom(playerWorld.random);
                if (optionalRandomStatusEffect.isEmpty()) {
                    continue;
                }

                RegistryEntry.Reference<StatusEffect> statusEffectRegistryEntry = optionalRandomStatusEffect.get();
                if (statusEffectRegistryEntry.value().getCategory() != StatusEffectCategory.BENEFICIAL) {
                    continue;
                }

                player.addStatusEffect(new StatusEffectInstance(
                        statusEffectRegistryEntry,
                        getDuration(experienceLevel), getAmplifier(experienceLevel), true, true
                ));

                statusEffects--;
            }
        }
    }

    public static double randomRange(double floor, double ceiling) {
        return floor + (ceiling - floor) * Math.random();
    }

    public int getEffectCount(int experienceLevel) {
        return (int) Math.floor(
                ((float)experienceLevel / 30) *
                        EFFECTS_PER_30_LEVELS *
                        randomRange(0.5, 2)
        );
    }

    public int getDuration(int experienceLevel) {
        return (int) Math.floor(
                ((float)experienceLevel / 30) *
                (DURATION_PER_30_LEVELS * 20) *
                randomRange(0.8, 1.2)
        );
    }

    public int getAmplifier(int experienceLevel) {
        return (int) Math.floor(
        ((float)experienceLevel / 30) *
                AMPLIFIER_PER_30_LEVELS *
                randomRange(0.8, 1.5)
        );
    }

    public static void onEndTickClient(MinecraftClient client) {

    }
}
