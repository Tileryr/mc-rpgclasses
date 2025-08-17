package com.mcclasses.rpgclassabilities.playerAbillities;

import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.util.Conversion;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class PlayerDash {
    public static final int DASH_TIME = 6;
    public static final float DASH_DISTANCE = 12;
    private static final Identifier MOVEMENT_MODIFIER_ID = Identifier.of(Rpgclassabilities.MOD_ID, "dash_speed_halt");

    private final ServerPlayerEntity player;
    private final Vec3d playerRotationVector;
    private final float playerHeadYaw;
    private final float playerPitch;
    private final Vec3d playerPosition;
    private final ServerWorld playerWorld;

    public PlayerDash(ServerPlayerEntity player) {
        this.player = player;
        this.playerRotationVector = player.getRotationVector();
        this.playerHeadYaw = player.headYaw;
        this.playerPitch = player.getPitch();
        this.playerPosition = player.getPos();
        this.playerWorld = player.getWorld();

        dashPlayer();
    }

    private void dashPlayer() {
        Vec3d playerPosition = player.getPos();

        EntityAttributeInstance playerMovement = player.getAttributes().getCustomInstance(EntityAttributes.MOVEMENT_SPEED);
        if (!playerMovement.hasModifier(MOVEMENT_MODIFIER_ID)) {
            playerMovement.addTemporaryModifier(new EntityAttributeModifier(
                    MOVEMENT_MODIFIER_ID,
                    -1F,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }

        MarkerEntity markerEntity = EntityType.MARKER.spawn(player.getWorld(), player.getBlockPos(), SpawnReason.EVENT);
        markerEntity.setHeadYaw(player.headYaw);
        markerEntity.setPos(playerPosition.x, playerPosition.y, playerPosition.z);
        player.setCameraEntity(markerEntity);

        spawnDashSmoke(player.getWorld(), playerPosition);
        setHidden(true);
        Rpgclassabilities.SCHEDULER.addTimer(DASH_TIME, this::dashTeleport);
    }

    private void dashTeleport() {
        Vec3d goalDashTarget = playerRotationVector.multiply(DASH_DISTANCE).add(playerPosition);

        BlockPos blockPosition = BlockPos.ofFloored(goalDashTarget);
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

    private static void spawnDashSmoke(ServerWorld world, Vec3d position) {
        world.spawnParticles(
                ParticleTypes.EXPLOSION,
                position.x, position.y, position.z,
                50, 1, 0.5, 1, 1
        );
    }

    private void setHidden(boolean on) {
        player.setInvulnerable(on);
        player.setInvisible(on);
    }
}
