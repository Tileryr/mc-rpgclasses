package com.mcclasses.rpgclassabilities.client.render;

import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BindProjectileEntityModel extends EntityModel<BindProjectileEntityRenderState> {
    protected BindProjectileEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(
                "main",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-4.0F, -4.0F, -1.0F, 8.0F, 8.0F, 2.0F)
                        .uv(0, 10)
                        .cuboid(-1.0F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F)
                        .uv(20, 0)
                        .cuboid(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F),
                ModelTransform.NONE
        );
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(BindProjectileEntityRenderState state) {}
}
