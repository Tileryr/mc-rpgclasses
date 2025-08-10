package com.mcclasses.rpgclassabilities.payload.s2c;

import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UpdateCurrentClassS2CPayload(RpgClass newRpgClass) implements CustomPayload {
    public static final Identifier UPDATE_CURRENT_CLASS_PAYLOAD_ID = Identifier.of(Rpgclassabilities.MOD_ID, "update_current_class");
    public static final CustomPayload.Id<UpdateCurrentClassS2CPayload> ID = new CustomPayload.Id<>(UPDATE_CURRENT_CLASS_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, UpdateCurrentClassS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.codec(RpgClass.CODEC),
            UpdateCurrentClassS2CPayload::newRpgClass,
            UpdateCurrentClassS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
