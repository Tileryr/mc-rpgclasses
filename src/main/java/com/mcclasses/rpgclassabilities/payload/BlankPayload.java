package com.mcclasses.rpgclassabilities.payload;

import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class BlankPayload implements CustomPayload {
    public Identifier payloadId;
    public CustomPayload.Id<BlankPayload> id;
    public PacketCodec<RegistryByteBuf, BlankPayload> codec;

    public BlankPayload(String path) {
        payloadId = Identifier.of(Rpgclassabilities.MOD_ID, path);
        id = new CustomPayload.Id<>(payloadId);
        codec = PacketCodec.unit(this);
    }

    @Override
    public Id<? extends CustomPayload> getId() { return id; }
}
