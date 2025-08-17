package com.mcclasses.rpgclassabilities.entities;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.data.RpgclassabilitiesDamageTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.joml.Vector3f;


public class BindProjectileEntity extends ProjectileEntity {
    private static final float TRAVEL_SPEED = 0.8F;

    private static final TrackedData<Vector3f> TARGET_DIRECTION =
            DataTracker.registerData(BindProjectileEntity.class, TrackedDataHandlerRegistry.VECTOR_3F);
    private Vec3d targetDirection = new Vec3d(1, 1, 1);

    public BindProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public BindProjectileEntity(World world, LivingEntity owner, Vec3d targetDirection) {
        this(Rpgclassabilities.BIND_PROJECTILE, world);
        setVelocity(targetDirection.multiply(TRAVEL_SPEED));
        setOwner(owner);
        setTargetDirection(targetDirection);
        dataTracker.set(TARGET_DIRECTION, targetDirection.toVector3f());

        if (!getWorld().isClient) {
            Rpgclassabilities.BIND_MANAGER.removeBind(owner, getServer());
        }
    }

    private void setTargetDirection(Vec3d targetDirection) {
        this.targetDirection = targetDirection.normalize();
    }

    public Vec3d getTargetDirection() {
        Vector3f targetDirection = dataTracker.get(TARGET_DIRECTION);
        return new Vec3d(
                targetDirection.x,
                targetDirection.y,
                targetDirection.z
        );
    }

    @Override
    protected void writeCustomData(WriteView writeView) {
        super.writeCustomData(writeView);
        writeView.put("targetDirection", Vec3d.CODEC, targetDirection);
    }

    @Override
    protected void readCustomData(ReadView readView) {
        super.readCustomData(readView);
        this.targetDirection = readView.read("targetDirection", Vec3d.CODEC).orElse(Vec3d.ZERO);
    }

    @Override
    public void tick() {
        super.tick();
        boolean isClient = this.getWorld().isClient;
        HitResult hitResult = null;

        if (!isClient) {
            setVelocity(targetDirection.multiply(TRAVEL_SPEED));
            updateRotation();

            hitResult = ProjectileUtil.getCollision(this, this::canHit);
        }

        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            hitOrDeflect(hitResult);
        }

        if (isClient) {
            this.getWorld().addParticleClient(ParticleTypes.CRIT, getX(), getY(), getZ(), 0.0, -1.0, 0.0);
        }

        Vec3d vec3d = this.getVelocity();
        this.setPosition(this.getPos().add(vec3d));
        this.tickBlockCollision();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);

        LoggerHelper.getLOGGER().info("HHt");
        if (getWorld().isClient()) {return;}
        if (!(entityHitResult.getEntity() instanceof LivingEntity hitEntity)) {return;}
        if (!(getOwner() instanceof LivingEntity shooter)) {return;}

        if (getWorld() instanceof ServerWorld world) {
            Rpgclassabilities.BIND_MANAGER.addBind(shooter, hitEntity);

            DamageSource damageSource = new DamageSource(
                    world.getRegistryManager().getEntryOrThrow(RpgclassabilitiesDamageTypes.BIND),
                    getPos()
            );

            shooter.damage(world, damageSource, 2.0F);
            hitEntity.damage(world, damageSource, 2.0F);
        }

        this.discard();
    }

    @Override
    public void checkDespawn() {
         if (this.getWorld().getPlayers().stream().allMatch((playerEntity -> distanceTo(playerEntity) > 128))) {
             this.discard();
         }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(TARGET_DIRECTION, new Vector3f(0, 0, 0));
    }
}
