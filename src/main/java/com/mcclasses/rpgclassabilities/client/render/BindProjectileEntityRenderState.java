package com.mcclasses.rpgclassabilities.client.render;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class BindProjectileEntityRenderState extends EntityRenderState {
    Vec3d ownerPosition;
    Vector3f direction;
}
