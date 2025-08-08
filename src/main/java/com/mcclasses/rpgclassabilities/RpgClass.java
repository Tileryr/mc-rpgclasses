package com.mcclasses.rpgclassabilities;

import com.mojang.serialization.Codec;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;

public enum RpgClass implements StringIdentifiable {
    WARRIOR("Warrior"),
    ROGUE("Rogue"),
    CLERIC("Cleric"),
    WIZARD("Wizard");

    private final String id;
    public static final Codec<RpgClass> CODEC = StringIdentifiable.createCodec(RpgClass::values);
    private RpgClass(final String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }
}
