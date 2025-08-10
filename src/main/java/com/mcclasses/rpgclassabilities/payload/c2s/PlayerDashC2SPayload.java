package com.mcclasses.rpgclassabilities.payload.c2s;

import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayerDashC2SPayload() implements CustomPayload {
    public static final Identifier PLAYER_DASH_PAYLOAD_ID = Identifier.of(Rpgclassabilities.MOD_ID, "player_dash");
    public static final CustomPayload.Id<PlayerDashC2SPayload> ID = new CustomPayload.Id<>(PLAYER_DASH_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, PlayerDashC2SPayload> CODEC = PacketCodec.unit(new PlayerDashC2SPayload());

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
