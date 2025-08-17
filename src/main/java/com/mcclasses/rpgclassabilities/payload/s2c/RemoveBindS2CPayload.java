package com.mcclasses.rpgclassabilities.payload.s2c;

import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record RemoveBindS2CPayload(UUID bindOriginId) implements CustomPayload {
    public static final Identifier REMOVE_BIND_PAYLOAD_ID = Identifier.of(Rpgclassabilities.MOD_ID, "remove_bind");
    public static final CustomPayload.Id<RemoveBindS2CPayload> ID = new CustomPayload.Id<>(REMOVE_BIND_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, RemoveBindS2CPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC,
            RemoveBindS2CPayload::bindOriginId,
            RemoveBindS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
