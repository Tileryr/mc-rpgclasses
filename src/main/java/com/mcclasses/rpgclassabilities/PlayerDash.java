package com.mcclasses.rpgclassabilities;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.TeleportTarget;

import java.util.function.Consumer;

public class PlayerDash {
    private static final long DASH_TIME = 6L;
    private static final float DASH_DISTANCE = 12;
    private static final Identifier MOVEMENT_MODIFIER_ID = Identifier.of(Rpgclassabilities.MOD_ID, "dash_speed_halt");

    private ServerPlayerEntity player;
    private float playerHeadYaw;
    private float playerPitch;
    private Vec3d playerPosition;
    private ServerWorld playerWorld;

    public PlayerDash(ServerPlayerEntity player) {
        this.player = player;
        this.playerHeadYaw = player.headYaw;
        this.playerPitch = player.getPitch();
        this.playerPosition = player.getPos();
        this.playerWorld = player.getWorld();

        dashPlayer();
    }

    private void dashPlayer() {
        Vec3d playerPosition = player.getPos();

        EntityAttributeInstance playerMovement = player.getAttributes().getCustomInstance(EntityAttributes.MOVEMENT_SPEED);

        playerMovement.addTemporaryModifier(new EntityAttributeModifier(
                MOVEMENT_MODIFIER_ID,
                -1F,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ));

        MarkerEntity markerEntity = EntityType.MARKER.spawn(player.getWorld(), player.getBlockPos(), SpawnReason.EVENT);
        markerEntity.setHeadYaw(player.headYaw);
        markerEntity.setPos(playerPosition.x, playerPosition.y, playerPosition.z);
        player.setCameraEntity(markerEntity);

        spawnDashSmoke(player.getWorld(), playerPosition);
        setHidden(true);
        DashTeleportTimer.INSTANCE.setTimer(DASH_TIME, this::dashTeleport);
    }

    private static void spawnDashSmoke(ServerWorld world, Vec3d position) {
        world.spawnParticles(
                ParticleTypes.EXPLOSION,
                position.x, position.y, position.z,
                50, 1, 0.5, 1, 1
        );
    }

    private void dashTeleport() {
        float headYaw = playerHeadYaw % 360;
        if (headYaw < 0) {
            headYaw += 360;
        }
        headYaw = Math.abs(headYaw - 360);
        float headYawRadians = (headYaw / 360) * (float) Math.TAU;
        Vec3d yawVector = new Vec3d(Math.sin(headYawRadians), 0, Math.cos(headYawRadians));

        Vec3d goalDashTarget = yawVector.multiply(DASH_DISTANCE).add(playerPosition);
        Vec3i roundedDashTarget = new Vec3i((int) goalDashTarget.x, (int) goalDashTarget.y, (int) goalDashTarget.z);

        BlockPos blockPosition = new BlockPos(roundedDashTarget);
        BlockState blockAtDashTarget = playerWorld.getBlockState(blockPosition);

        int targetYOffset = 0;

        while (blockAtDashTarget.isFullCube(playerWorld, blockPosition)) {
            targetYOffset++;
            blockPosition = blockPosition.up();
            blockAtDashTarget = playerWorld.getBlockState(blockPosition);
        }

        Vec3d dashTarget = goalDashTarget.add(0, targetYOffset, 0);

        player.teleportTo(new TeleportTarget(playerWorld, dashTarget, Vec3d.ZERO, playerHeadYaw, playerPitch, TeleportTarget.NO_OP));
        player.getAttributes().getCustomInstance(EntityAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);
        player.setCameraEntity(player);

        spawnDashSmoke(player.getWorld(), dashTarget);
        setHidden(false);
    }

    private void setHidden(boolean on) {
        player.setInvulnerable(on);
        player.setInvisible(on);
    }
    public static class DashTeleportTimer implements ServerTickEvents.EndTick {
        public static final DashTeleportTimer INSTANCE = new DashTeleportTimer();
        private long ticksUntilTeleport;
        Runnable callback;

        public void setTimer(long ticksUntilTeleport, Runnable callback) {
            this.ticksUntilTeleport = ticksUntilTeleport;
            this.callback = callback;
        }

        @Override
        public void onEndTick(MinecraftServer server) {
            if (--ticksUntilTeleport == 0L) {
                callback.run();
            }
        }
    }
}
