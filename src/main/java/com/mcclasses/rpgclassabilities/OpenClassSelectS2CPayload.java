package com.mcclasses.rpgclassabilities;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenClassSelectS2CPayload() implements CustomPayload {
    public static final Identifier OPEN_CLASS_SELECT_PAYLOAD_ID = Identifier.of(Rpgclassabilities.MOD_ID, "open_class_select");
    public static final CustomPayload.Id<OpenClassSelectS2CPayload> ID = new CustomPayload.Id<>(OPEN_CLASS_SELECT_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, OpenClassSelectS2CPayload> CODEC = PacketCodec.unit(new OpenClassSelectS2CPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
