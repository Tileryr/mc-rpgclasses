package com.mcclasses.rpgclassabilities.enums;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringIdentifiable;

public enum RpgClass implements StringIdentifiable {
    WARRIOR("Warrior", 40),
    ROGUE("Rogue", 40),
    CLERIC("Cleric", 200),
    WIZARD("Wizard", 2);

    private final String id;
    public final int abilityOneCooldown;

    public static final Codec<RpgClass> CODEC = StringIdentifiable.createCodec(RpgClass::values);
    RpgClass(final String id, int abilityOneCooldown) {
        this.id = id;
        this.abilityOneCooldown = abilityOneCooldown;
    }

    @Override
    public String asString() {
        return this.id;
    }
}
