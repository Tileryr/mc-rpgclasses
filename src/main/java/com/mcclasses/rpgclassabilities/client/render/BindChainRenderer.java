package com.mcclasses.rpgclassabilities.client.render;

import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BindChainRenderer {
    public static final Identifier CHAIN_TEXTURE = Identifier.of(Rpgclassabilities.MOD_ID,"textures/entity/bind_projectile/chain.png");
    private static final RenderLayer CHAIN_LAYER = RenderLayer.getEntitySmoothCutout(CHAIN_TEXTURE);

    private final Map<UUID, Pair<Vec3d, Vec3d>> chainPositions = new HashMap<>();

    public void addChain(UUID id, Vec3d startPosition, Vec3d endPosition) {
        chainPositions.put(id, new Pair<>(startPosition, endPosition));
    }

    public void removeChain(UUID id) {
        chainPositions.remove(id);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Camera camera) {
        Vec3d cameraPosition = camera.getCameraPos();

        chainPositions.values().forEach((chainPosition) -> {
            Vec3d origin = chainPosition.getLeft();
            Vec3d end = chainPosition.getRight();

            Vec3d cameraToOrigin = origin.subtract(cameraPosition);

            matrixStack.push();
            matrixStack.translate(cameraToOrigin.getX(), cameraToOrigin.getY(), cameraToOrigin.getZ());

            renderChain(
                    matrixStack,
                    vertexConsumerProvider,
                    origin,
                    end.subtract(origin),
                    0.5,
                    0.5,
                    end.subtract(origin).normalize(),
                    90F
            );
            matrixStack.pop();
        });
    }

    public static void renderChain(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            Vec3d chainStart,
            Vec3d chainEnd,
            double chainStartRadius,
            double chainEndRadius,
            Vec3d direction,
            float age
    ) {
        matrices.push();
//        matrices.translate(chainStart);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(CHAIN_LAYER);
        MatrixStack.Entry entry = matrices.peek();

        int totalSides = 8;

        Vector3f lastChainStart = new Vector3f();
        Vector3f lastChainEnd = new Vector3f();

        for (int point = 0; point < totalSides + 1; point++) {
            double normalizedPoint = (double) point / totalSides;
            double circleX = Math.cos(normalizedPoint * Math.TAU);
            double circleY = Math.sin(normalizedPoint * Math.TAU);

            Vector3f chainStartPoint = new Vector3f((float) (circleX * chainStartRadius), (float) (circleY * chainStartRadius), 0.0F);
            Vector3f chainEndPoint = new Vector3f((float) (circleX * chainEndRadius), (float) (circleY * chainEndRadius), 0.0F);

            chainStartPoint.rotate(new Quaternionf().rotateTo(new Vector3f(0, 0, 1), direction.toVector3f()));
            chainEndPoint.rotate(new Quaternionf().rotateTo(new Vector3f(0, 0, 1), direction.toVector3f()));

            chainEndPoint.add(chainEnd.toVector3f());
            if (point > 0) {
                addVertexQuad(
                        vertexConsumer,
                        entry,
                        new Vec3d(chainEndPoint.x, chainEndPoint.y, chainEndPoint.z),
                        new Vec3d(lastChainEnd.x, lastChainEnd.y, lastChainEnd.z),
                        new Vec3d(lastChainStart.x, lastChainStart.y, lastChainStart.z),
                        new Vec3d(chainStartPoint.x, chainStartPoint.y, chainStartPoint.z),
                        new Vec2f(0.0F, 1.0F),
                        new Vec2f(1.0F, 1 - Math.min(age / 90.0F, 1.0F))
                );
            }
            lastChainStart = chainStartPoint;
            lastChainEnd = chainEndPoint;
        }

        matrices.pop();
    }

    public static void renderChain(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            BindProjectileEntityRenderState bindProjectileEntityRenderState
    ) {
        matrices.push();
        Vec3d bindProjectileEntityPosition = new Vec3d(bindProjectileEntityRenderState.x, bindProjectileEntityRenderState.y, bindProjectileEntityRenderState.z);

//        matrices.translate(bindProjectileEntityPosition.negate());
        renderChain(
                matrices,
                vertexConsumers,
                bindProjectileEntityPosition,
                bindProjectileEntityRenderState.ownerPosition,
                2.0,
                0.2,
                bindProjectileEntityRenderState.direction,
                bindProjectileEntityRenderState.age
        );
        matrices.pop();
    }


    private static void addVertexQuad(
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
                new Vec2f(textureUVCorner1.x, textureUVCorner1.y), Colors.WHITE);
        addVertex(vertexConsumer, entry,
                corner2,
                new Vec2f(textureUVCorner2.x, textureUVCorner1.y), Colors.WHITE);
        addVertex(vertexConsumer, entry,
                corner3,
                new Vec2f(textureUVCorner2.x, textureUVCorner2.y), Colors.WHITE);
        addVertex(vertexConsumer, entry,
                corner4,
                new Vec2f(textureUVCorner1.x, textureUVCorner2.y), Colors.WHITE);

    }

    private static void addVertex(
            VertexConsumer vertexConsumer, MatrixStack.Entry entry,
            Vec3d position,
            Vec2f textureUV,
            int argb
    ) {

        vertexConsumer.vertex(entry, (float) position.x, (float) position.y, (float) position.z)
                .color(argb)
                .texture(textureUV.x, textureUV.y)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE)
                .normal(entry, 0.0F, 1.0F, 0.0F);
    }
}
