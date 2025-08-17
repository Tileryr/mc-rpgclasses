package com.mcclasses.rpgclassabilities.client.render;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class BindProjectileEntityRenderState extends EntityRenderState {
    public Vec3d ownerPosition;
    public Vec3d direction;
    public float pitch;
    public float yaw;
}
