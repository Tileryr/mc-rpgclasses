package com.mcclasses.rpgclassabilities.payload;

import com.google.common.collect.ImmutableMap;
import com.mcclasses.rpgclassabilities.payload.c2s.SelectClassC2SPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.OpenClassSelectS2CPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.UpdateCurrentClassS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Map;

public class PayloadRegister {
    private record PayloadData<T extends CustomPayload>(CustomPayload.Id<T> id, PacketCodec<? super RegistryByteBuf, T> codec) {}

    static private final Map<CustomPayload.Id, PacketCodec> S2CPayloads = ImmutableMap.<CustomPayload.Id, PacketCodec>builder()
            .put(OpenClassSelectS2CPayload.ID, OpenClassSelectS2CPayload.CODEC)
            .put(UpdateCurrentClassS2CPayload.ID, UpdateCurrentClassS2CPayload.CODEC)
            .put(SelectClassC2SPayload.ID, SelectClassC2SPayload.CODEC)

            .build();

    static private final Map<CustomPayload.Id, PacketCodec> C2SPayloads = ImmutableMap.<CustomPayload.Id, PacketCodec>builder()
            .put(SelectClassC2SPayload.ID, SelectClassC2SPayload.CODEC)

            .build();

    @SuppressWarnings("unchecked")
    static public void register() {
        S2CPayloads.forEach(((id, codec) -> {
            PayloadTypeRegistry.playS2C().register(id, codec);
        }));

        C2SPayloads.forEach(((id, codec) -> {
            PayloadTypeRegistry.playC2S().register(id, codec);
        }));

    }
}
