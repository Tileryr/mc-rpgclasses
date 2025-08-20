package com.mcclasses.rpgclassabilities.playerAbillities;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.CanCharge;
import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.FovOveridable;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.client.RpgclassabilitiesClient;
import com.mcclasses.rpgclassabilities.timers.TickScheduler;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.logging.Log;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class PlayerCharge {
    public static final int CHARGE_LENGTH = 60;
    public static final int BLOCK_BREAK_POWER = 50;
    public static final int EFFECT_DURATIONS = 40;

    public static final double CHARGE_SPEED = 0.1;
    public static final double MAX_CHARGE_DAMAGE_MULTIPLIER = 5.0;
    public static final double MAX_CHARGE_KNOCKBACK_MULTIPLIER = 2.5;

    private static final Identifier CHARGE_MOVEMENT_MODIFIER_ID = Identifier.of(Rpgclassabilities.MOD_ID, "charge_speed_increase");

    private final PlayerEntity player;
    private final ServerPlayerEntity serverPlayer;
    private final EntityAttributeInstance playerMovement;
    private final EntityAttributeInstance playerAttackDamage;
    private final TickScheduler scheduler;

    private final boolean onServer;

    public PlayerCharge(PlayerEntity player, TickScheduler scheduler) {
        this.player = player;
        this.playerMovement = getAttribute(player, EntityAttributes.MOVEMENT_SPEED);
        this.playerAttackDamage = getAttribute(player, EntityAttributes.ATTACK_DAMAGE);

        this.onServer = player instanceof ServerPlayerEntity;

        if (this.onServer) {
            this.serverPlayer = (ServerPlayerEntity) player;
        } else {
            this.serverPlayer = null;
        }

        this.scheduler = scheduler;
    }

    public void charge() {
        if (!(player instanceof CanCharge chargeablePlayer)) {return;}
        if (chargeablePlayer.rpgclassabilities$getCurrentlyCharing()) {return;}

        startCharge(player);
        chargeablePlayer.rpgclassabilities$activateCharge(player.getYaw());

        scheduler.addTimer(CHARGE_LENGTH, this::endCharge);
    }

    private void startCharge(PlayerEntity player) {
        if (player instanceof FovOveridable fovOverridablePlayer) {
            fovOverridablePlayer.rpgclassabilities$activateOverrideFov(1.8F);
        }
        player.setSprinting(true);

        playerMovement.addTemporaryModifier(new EntityAttributeModifier(
                CHARGE_MOVEMENT_MODIFIER_ID,
                CHARGE_SPEED,
                EntityAttributeModifier.Operation.ADD_VALUE
        ));
    }

    private void endCharge() {
        PlayerCharge.endCharge(player, playerMovement, playerAttackDamage);
    }

    private static void endCharge(PlayerEntity player) {
        PlayerCharge.endCharge(
                player,
                getAttribute(player, EntityAttributes.MOVEMENT_SPEED),
                getAttribute(player, EntityAttributes.ATTACK_DAMAGE)
        );
    }

    private static void endCharge(PlayerEntity player, EntityAttributeInstance playerMovement, EntityAttributeInstance playerAttackDamage) {
        if (!(player instanceof CanCharge chargeablePlayer)) {return;}
        if (!chargeablePlayer.rpgclassabilities$getCurrentlyCharing()) {return;}
        chargeablePlayer.rpgclassabilities$setCurrentlyCharging(false);
        chargeablePlayer.rpgclassabilities$setCurrentChangeTime(0);

        if (player instanceof FovOveridable fovOverridablePlayer) {
            fovOverridablePlayer.rpgclassabilities$setOverrideFov(false);
        }
        player.setSprinting(false);

        playerMovement.removeModifier(CHARGE_MOVEMENT_MODIFIER_ID);
    }

    private static EntityAttributeInstance getAttribute(PlayerEntity player, RegistryEntry<EntityAttribute> attribute) {
        return player.getAttributes().getCustomInstance(attribute);
    }

    public static void onEndTick(MinecraftServer server) {
        server.getPlayerManager().getPlayerList().forEach((player -> {
            if (!(player instanceof CanCharge chargeablePlayer)) {return;}
            if (!chargeablePlayer.rpgclassabilities$getCurrentlyCharing()) {return;}

            IntStream.range(-1, 2).forEach((x) -> {
                IntStream.range(0, 2).forEach((y) -> {
                    IntStream.range(-1, 2).forEach((z) -> {
                        Vec3d breakPosition = new Vec3d(x, y, z);
                        breakPosition = breakPosition.rotateY(chargeablePlayer.rpgclassabilities$getChargeYaw());

                        ServerWorld world = player.getWorld();
                        BlockPos blockPos = BlockPos.ofFloored(player.getPos().add(breakPosition));
                        BlockState blockState = world.getBlockState(blockPos);
                        float blockHardness = blockState.getHardness(world, blockPos);
                        if (blockHardness > 0 && blockHardness < BLOCK_BREAK_POWER) {
                            player.getWorld().breakBlock(blockPos, false);
                        }
                    });
                });
            });


//            player.getBlockPos();

        }));
    }

    public static void onAttackEntity(LivingEntity player, Entity target) {
        if (!(player instanceof CanCharge chargeablePlayer)) {return;}
        if (!(player instanceof PlayerEntity playerEntity)) {return;}

        if (target instanceof LivingEntity hitEntity) {
            playerEntity.sendMessage(Text.literal( "health: " + hitEntity.getHealth()), true);
        }

        if (!(chargeablePlayer.rpgclassabilities$getCurrentlyCharing())) {return;}

        PlayerCharge.endCharge(playerEntity);

        if (target instanceof LivingEntity hitEntity) {
            hitEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, EFFECT_DURATIONS, 2));
            hitEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, EFFECT_DURATIONS, 2));
            hitEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, EFFECT_DURATIONS, 2));
        }
    }

    public static Vec3d getChargeMovement(Vec3d inputMovement) {
        return new Vec3d(0, 0, 1);
    }
}