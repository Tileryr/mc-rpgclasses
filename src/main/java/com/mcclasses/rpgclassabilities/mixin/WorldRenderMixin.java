package com.mcclasses.rpgclassabilities.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.client.RpgclassabilitiesClient;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.PersistentStateManager;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldRenderer.class)
public abstract class WorldRenderMixin {
    @Final
    @Shadow
    private BufferBuilderStorage bufferBuilders;

    @Inject(method = "method_62214",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;renderEntities(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/RenderTickCounter;Ljava/util/List;)V"
    ))
    public void renderChain(
            GpuBufferSlice gpuBufferSlice,
            RenderTickCounter renderTickCounter,
            Camera camera,
            Profiler profiler,
            Matrix4f matrix4f,
            Handle handle,
            Handle handle2,
            boolean bl,
            Frustum frustum,
            Handle handle3,
            Handle handle4,
            CallbackInfo ci,
            @Local MatrixStack matrixStack
            )
    {
        VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
        VertexConsumerProvider vertexConsumerProvider = immediate;
        RpgclassabilitiesClient.BIND_CHAIN_RENDERER.render(matrixStack, vertexConsumerProvider, camera);
    }
}
