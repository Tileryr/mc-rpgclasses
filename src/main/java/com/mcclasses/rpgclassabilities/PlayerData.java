package com.mcclasses.rpgclassabilities;

import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlayerData {
    public RpgClass playerClass = RpgClass.WARRIOR;
    public boolean classSelected = false;

    public PlayerData() {
    }

    public PlayerData(RpgClass playerClass, boolean classSelected) {
        this.playerClass = playerClass;
        this.classSelected = classSelected;
    }

    public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RpgClass.CODEC.fieldOf("player_class").forGetter(PlayerData::getPlayerClass),
            Codec.BOOL.fieldOf("class_selected").forGetter(PlayerData::getClassSelected)
            ).apply(instance, PlayerData::new));

    private RpgClass getPlayerClass() {
        return playerClass;
    }

    private boolean getClassSelected() {
        return classSelected;
    }
}
