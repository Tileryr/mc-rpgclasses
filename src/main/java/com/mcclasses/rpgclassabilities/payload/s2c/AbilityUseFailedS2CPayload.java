package com.mcclasses.rpgclassabilities.payload.s2c;

import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.enums.RpgClass;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AbilityUseFailedS2CPayload(int ticksUntilActive) implements CustomPayload {
    public static final Identifier ABILITY_USE_FAILED_PAYLOAD_ID = Identifier.of(Rpgclassabilities.MOD_ID, "ability_use_failed");
    public static final CustomPayload.Id<AbilityUseFailedS2CPayload> ID = new CustomPayload.Id<>(ABILITY_USE_FAILED_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, AbilityUseFailedS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            AbilityUseFailedS2CPayload::ticksUntilActive,
            AbilityUseFailedS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
