package com.mcclasses.rpgclassabilities.payload;

import com.google.common.collect.ImmutableMap;
import com.mcclasses.rpgclassabilities.payload.c2s.SelectClassC2SPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.UpdateCurrentClassS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Map;

public class PayloadRegister {
    public static final BlankPayload OPEN_CLASS_SELECT = new BlankPayload("ability_one_pressed");

    static private final Map<CustomPayload.Id, PacketCodec> S2CPayloads = ImmutableMap.<CustomPayload.Id, PacketCodec>builder()
            .put(UpdateCurrentClassS2CPayload.ID, UpdateCurrentClassS2CPayload.CODEC)
            .put(OPEN_CLASS_SELECT.id, OPEN_CLASS_SELECT.codec)

            .build();

    public static final BlankPayload ABILITY_ONE_PRESSED = new BlankPayload("ability_one_pressed");

    static private final Map<CustomPayload.Id, PacketCodec> C2SPayloads = ImmutableMap.<CustomPayload.Id, PacketCodec>builder()
            .put(SelectClassC2SPayload.ID, SelectClassC2SPayload.CODEC)
            .put(ABILITY_ONE_PRESSED.id, ABILITY_ONE_PRESSED.codec)

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
