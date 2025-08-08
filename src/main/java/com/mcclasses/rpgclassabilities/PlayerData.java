package com.mcclasses.rpgclassabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlayerData {
    public RpgClass playerClass;

    public PlayerData() {
    }

    public PlayerData(RpgClass playerClass) {
        this.playerClass = playerClass;
    }

    public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RpgClass.CODEC.fieldOf("player_class").forGetter(PlayerData::getPlayerClass)
            ).apply(instance, PlayerData::new));

    private RpgClass getPlayerClass() {
        return playerClass;
    }
}
