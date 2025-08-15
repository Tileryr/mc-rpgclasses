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
    public static final Identifier CHAIN_TEXTURE = Identifier.of(Rpgclassabilities.MOD_ID,"textures/entity/bind_projectile/chain.png");
    private static final RenderLayer CHAIN_LAYER = RenderLayer.getEntitySmoothCutout(CHAIN_TEXTURE);
    private final BindProjectileEntityModel model;

    public BindProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new BindProjectileEntityModel(context.getPart(RpgclassabilitiesClient.BIND_PROJECTILE_MODEL_LAYER));
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
            renderChain(matrixStack, vertexConsumerProvider, bindProjectileEntityRenderState);
        }

        super.render(bindProjectileEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    private void renderChain(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BindProjectileEntityRenderState bindProjectileEntityRenderState) {
        matrices.push();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(CHAIN_LAYER);
        MatrixStack.Entry entry = matrices.peek();

        Vec3d startPosition = bindProjectileEntityRenderState.ownerPosition;
        Vector3f direction = bindProjectileEntityRenderState.direction;

        int totalSides = 8;
        double radius = 2.0;

        Vector3f lastPoint = new Vector3f();

        for (int point = 0; point < totalSides + 1; point++) {
            double normalizedPoint = (double) point / totalSides;
            double pointX = Math.cos(normalizedPoint * Math.TAU);
            double pointY = Math.sin(normalizedPoint * Math.TAU);
            pointX *= radius;
            pointY *= radius;

            Vector3f point3d = new Vector3f((float) pointX, (float) pointY, 0.0F);
            point3d.rotate(new Quaternionf().rotateTo(new Vector3f(0, 0, 1), direction));

            if (point > 0) {
                addVertexQuad(
                        vertexConsumer,
                        entry,
                        startPosition.add(0.2, 0, 0),
                        startPosition.add(-0.2, 0, 0),
                        new Vec3d(point3d.x, point3d.y, point3d.z),
                        new Vec3d(lastPoint.x, lastPoint.y, lastPoint.z),
                        new Vec2f(0.0F, 1.0F),
                        new Vec2f(1.0F, 1 - Math.min(bindProjectileEntityRenderState.age / 90.0F, 1.0F))
                );
            }
            lastPoint = point3d;
        }

        matrices.pop();
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

    private void addVertexQuad(
            VertexConsumer vertexConsumer,
            MatrixStack.Entry entry,
            Vec3d corner1,
            Vec3d corner2,
            Vec3d corner3,
            Vec3d corner4,
            Vec2f textureUVCorner1,
            Vec2f textureUVCorner2
    ) {
        addVertex(vertexConsumer, entry,
                corner1,
                new Vec2f(textureUVCorner1.x, textureUVCorner1.y));
        addVertex(vertexConsumer, entry,
                corner2,
                new Vec2f(textureUVCorner2.x, textureUVCorner1.y));
        addVertex(vertexConsumer, entry,
                corner3,
                new Vec2f(textureUVCorner2.x, textureUVCorner2.y));
        addVertex(vertexConsumer, entry,
                corner4,
                new Vec2f(textureUVCorner1.x, textureUVCorner2.y));

    }

    private void addVertex(
            VertexConsumer vertexConsumer, MatrixStack.Entry entry,
            Vec3d position,
            Vec2f textureUV
    ) {

        vertexConsumer.vertex(entry, (float) position.x, (float) position.y, (float) position.z)
                .color(Colors.WHITE)
                .texture(textureUV.x, textureUV.y)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE)
                .normal(entry, 0.0F, 1.0F, 0.0F);
    }
}
