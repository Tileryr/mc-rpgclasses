package com.mcclasses.rpgclassabilities;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SelectClassC2SPayload(RpgClass rpgClass) implements CustomPayload {

    public static final Identifier SELECT_CLASS_PAYLOAD_ID = Identifier.of(Rpgclassabilities.MOD_ID, "select_class");
    public static final CustomPayload.Id<SelectClassC2SPayload> ID = new CustomPayload.Id<>(SELECT_CLASS_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SelectClassC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.codec(RpgClass.CODEC),
            SelectClassC2SPayload::rpgClass,
            SelectClassC2SPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
