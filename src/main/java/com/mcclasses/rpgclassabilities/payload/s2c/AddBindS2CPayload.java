package com.mcclasses.rpgclassabilities.payload.s2c;

import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AddBindS2CPayload implements CustomPayload {
    public static final Identifier ADD_BIND_PAYLOAD_ID = Identifier.of(Rpgclassabilities.MOD_ID, "add_bind");
    public static final CustomPayload.Id<AddBindS2CPayload> ID = new CustomPayload.Id<>(ADD_BIND_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, AddBindS2CPayload> CODEC = PacketCodec.of(AddBindS2CPayload::write, AddBindS2CPayload::new);

    public final UUID bindId;
    public final Vec3d bindOriginPosition;
    public final Vec3d boundEntityPosition;

    public AddBindS2CPayload(LivingEntity bindOrigin, LivingEntity boundEntity) {
        bindId = bindOrigin.getUuid();
        bindOriginPosition = bindOrigin.getPos();
        boundEntityPosition = boundEntity.getPos();
    }

    public AddBindS2CPayload(LivingEntity bindOrigin, Vec3d boundEntityPosition) {
        this.bindId = bindOrigin.getUuid();
        this.bindOriginPosition = bindOrigin.getPos();
        this.boundEntityPosition = boundEntityPosition;
    }

    private AddBindS2CPayload(RegistryByteBuf buf) {
        bindId = buf.readUuid();
        bindOriginPosition = buf.readVec3d();
        boundEntityPosition = buf.readVec3d();
    }

    private void write(RegistryByteBuf buf) {
        buf.writeUuid(this.bindId);
        buf.writeVec3d(this.bindOriginPosition);
        buf.writeVec3d(this.boundEntityPosition);
    }

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
