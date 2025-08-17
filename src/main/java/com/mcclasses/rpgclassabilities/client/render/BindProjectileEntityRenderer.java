package com.mcclasses.rpgclassabilities.client.render;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.client.RpgclassabilitiesClient;
import com.mcclasses.rpgclassabilities.entities.BindProjectileEntity;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.joml.Quaternionf;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Map;
import java.util.function.BiConsumer;

public class BindProjectileEntityRenderer extends EntityRenderer<BindProjectileEntity, BindProjectileEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of(Rpgclassabilities.MOD_ID,"textures/entity/bind_projectile/bind_projectile.png");

    public static final EntityModelLayer BIND_PROJECTILE_MODEL_LAYER = new EntityModelLayer(Rpgclassabilities.BIND_PROJECTILE_ID, "main");

    private final BindProjectileEntityModel model;

    public BindProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new BindProjectileEntityModel(context.getPart(BIND_PROJECTILE_MODEL_LAYER));
    }

    @Override
    public void render(
            BindProjectileEntityRenderState bindProjectileEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
    ) {
        matrixStack.push();

        matrixStack.scale(1, -1, 1);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(bindProjectileEntityRenderState.yaw - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(bindProjectileEntityRenderState.pitch));
        matrixStack.translate(0.0F, -0.5F, 0.0F);

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();

        if (bindProjectileEntityRenderState.ownerPosition != null) {
            BindChainRenderer.renderChain(matrixStack, vertexConsumerProvider, bindProjectileEntityRenderState);
        }

        super.render(bindProjectileEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public BindProjectileEntityRenderState createRenderState() {
        return new BindProjectileEntityRenderState();
    }

    @Override
    public void updateRenderState(BindProjectileEntity bindProjectileEntity, BindProjectileEntityRenderState bindProjectileEntityRenderState, float f) {
        super.updateRenderState(bindProjectileEntity, bindProjectileEntityRenderState, f);
        if (bindProjectileEntity.getOwner() != null) {
            Vec3d ownerPosition = bindProjectileEntity.getOwner().getLerpedPos(f).add(0F, 0F, 0F);
            bindProjectileEntityRenderState.ownerPosition = ownerPosition.subtract(bindProjectileEntity.getLerpedPos(f));
        } else {
            bindProjectileEntityRenderState.ownerPosition = null;
        }
        bindProjectileEntityRenderState.direction = bindProjectileEntity.getTargetDirection();
        bindProjectileEntityRenderState.yaw = bindProjectileEntity.getLerpedYaw(f);
        bindProjectileEntityRenderState.pitch = bindProjectileEntity.getLerpedPitch(f);
    }


}
