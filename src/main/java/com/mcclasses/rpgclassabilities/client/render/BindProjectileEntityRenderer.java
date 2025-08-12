package com.mcclasses.rpgclassabilities.client.render;

import com.mcclasses.rpgclassabilities.entities.BindProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BindProjectileEntityRenderer extends EntityRenderer<BindProjectileEntity, BindProjectileEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/shulker/spark.png");
    private final BindProjectileEntityModel model;

    public BindProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new BindProjectileEntityModel(context.getPart(EntityModelLayers.SHULKER_BULLET));
    }

    @Override
    public BindProjectileEntityRenderState createRenderState() {
        return new BindProjectileEntityRenderState();
    }

    @Override
    public void render(
            BindProjectileEntityRenderState bindProjectileEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
    ) {
        matrixStack.push();
        matrixStack.translate(0.0F, 0.5F, 0.0F);
        matrixStack.scale(1, 1, 1);

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();

        super.render(bindProjectileEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }
}
