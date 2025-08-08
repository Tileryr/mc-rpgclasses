package com.mcclasses.rpgclassabilities;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {
    public Map<UUID, PlayerData> players = new HashMap<>();

    private StateSaverAndLoader() {
    }

    private StateSaverAndLoader(Map<UUID, PlayerData> players) {
        players.forEach(((uuid, playerData) -> {LoggerHelper.getLOGGER().info(uuid + " : " + playerData.getClass());}));
        this.players = new HashMap<>(players);
    }

    private Map<UUID, PlayerData> getPlayers() {
        return players;
    }

    private static final Codec<StateSaverAndLoader> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Uuids.CODEC, PlayerData.CODEC).fieldOf("players").forGetter(StateSaverAndLoader::getPlayers)
    ).apply(instance, StateSaverAndLoader::new));

    private static final PersistentStateType<StateSaverAndLoader> type = new PersistentStateType<>(
            Rpgclassabilities.MOD_ID,
            StateSaverAndLoader::new,
            CODEC,
            null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
        assert serverWorld != null;

        StateSaverAndLoader state = serverWorld.getPersistentStateManager().getOrCreate(type);
        state.markDirty();
        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.getServer());

        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> {
            return new PlayerData();
        });

        return playerState;
    }
}
