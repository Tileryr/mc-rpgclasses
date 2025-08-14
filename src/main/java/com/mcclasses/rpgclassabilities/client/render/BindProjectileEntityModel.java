package com.mcclasses.rpgclassabilities.client.render;

import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BindProjectileEntityModel extends EntityModel<BindProjectileEntityRenderState> {
    private final ModelPart projectile;

    public BindProjectileEntityModel(ModelPart root) {
        super(root);
        projectile = root.getChild("main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(42, 25).cuboid(-10.0F, -15.0F, -5.0F, 12.0F, 6.0F, 10.0F, new Dilation(0.0F))
                .uv(0, 66).mirrored().cuboid(0.0F, -16.0F, -6.0F, 7.0F, 11.0F, 12.0F, new Dilation(0.0F)).mirrored(false)
                .uv(24, 54).cuboid(-4.0F, -19.0F, -7.0F, 7.0F, 7.0F, 5.0F, new Dilation(0.0F))
                .uv(0, 13).cuboid(-11.0F, -15.0F, -5.0F, 12.0F, 4.0F, 10.0F, new Dilation(0.0F))
                .uv(0, 54).cuboid(-4.0F, -19.0F, 2.0F, 7.0F, 7.0F, 5.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));

        ModelPartData lowerLip = main.addChild("lowerLip", ModelPartBuilder.create().uv(0, 41).cuboid(-3.5F, 2.0F, -5.0F, 11.0F, 3.0F, 10.0F, new Dilation(0.0F))
                .uv(0, 27).cuboid(-3.5F, 1.0F, -5.0F, 11.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-4.5F, -7.0F, 0.0F, -0.0007F, 0.0022F, -0.6546F));
        return TexturedModelData.of(modelData, 320, 180);
    }

    @Override
    public void setAngles(BindProjectileEntityRenderState state) {
        super.setAngles(state);
    }
}
