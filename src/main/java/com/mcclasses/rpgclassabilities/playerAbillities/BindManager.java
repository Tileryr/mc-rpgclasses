package com.mcclasses.rpgclassabilities.playerAbillities;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.payload.s2c.AddBindS2CPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.RemoveBindS2CPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.logging.Log;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BindManager implements ServerTickEvents.EndTick {
    private static final double TAKE_AMOUNT = 0.2;
    private static final Identifier BOUND_MODIFIER_ID = Identifier.of(Rpgclassabilities.MOD_ID, "bound");
    private static final Identifier BINDING_MODIFIER_ID = Identifier.of(Rpgclassabilities.MOD_ID, "binding");
    private final Map<UUID, UUID> bindPairs = new HashMap<>();
    private final Map<UUID, Vec3d> frozenBoundEntities = new HashMap<>();

    public BindManager() {
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (bindPairs.containsValue(entity.getUuid())) {
                LoggerHelper.getLOGGER().info("Freeze");
                frozenBoundEntities.put(entity.getUuid(), entity.getPos());
            }
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            frozenBoundEntities.remove(entity.getUuid());
        });
        ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register(this::afterChangeWorld);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(this::afterPlayerChangeWorld);
    }

    public void addBind(LivingEntity bindOrigin, LivingEntity boundEntity) {
        if (bindOrigin.getWorld().isClient) {return;}
        if (bindPairs.containsKey(bindOrigin.getUuid())) {
            removeBind(bindOrigin, bindOrigin.getServer());
        }

        bindPairs.put(bindOrigin.getUuid(), boundEntity.getUuid());
        EntityAttributeInstance boundEntityMaxHealthAttribute = boundEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        EntityAttributeInstance bindOriginEntityMaxHealthAttribute = bindOrigin.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        assert boundEntityMaxHealthAttribute != null;
        assert bindOriginEntityMaxHealthAttribute != null;

        boundEntityMaxHealthAttribute.addTemporaryModifier(new EntityAttributeModifier(
                BOUND_MODIFIER_ID,
                -TAKE_AMOUNT,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ));

        double boundEntityMaxHealth = boundEntityMaxHealthAttribute.getBaseValue();
        double takenHealth = boundEntityMaxHealth * TAKE_AMOUNT;
        bindOriginEntityMaxHealthAttribute.addTemporaryModifier(new EntityAttributeModifier(
                BINDING_MODIFIER_ID,
                takenHealth,
                EntityAttributeModifier.Operation.ADD_VALUE
        ));

        if (boundEntity instanceof MobEntity boundMobEntity) {
            boundMobEntity.setPersistent();
        }
    }

    public void removeBind(LivingEntity bindOrigin, MinecraftServer server) {
        removeBind(bindOrigin.getUuid(), server);
    }

    public void removeBind(UUID bindOriginId, MinecraftServer server) {
        removeBind(bindOriginId, bindPairs.get(bindOriginId), server);
    }

    public void removeBind(UUID bindOriginId, UUID boundEntityId, MinecraftServer server) {
        LoggerHelper.getLOGGER().info("Remove");
        LivingEntity bindOrigin = getLivingEntity(bindOriginId, server);
        if (bindOrigin != null) {
            EntityAttributeInstance bindOriginMaxHealthInstance = bindOrigin.getAttributeInstance(EntityAttributes.MAX_HEALTH);
            assert bindOriginMaxHealthInstance != null;
            bindOriginMaxHealthInstance.removeModifier(BINDING_MODIFIER_ID);
        }

        LivingEntity boundEntity = getLivingEntity(boundEntityId, server);
        if (boundEntity != null) {
            EntityAttributeInstance boundEntityMaxHealthInstance = boundEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH);
            assert boundEntityMaxHealthInstance != null;
            boundEntityMaxHealthInstance.removeModifier(BOUND_MODIFIER_ID);
        }

        bindPairs.remove(bindOriginId);
        sendPacketToAllPlayers((ServerWorld) bindOrigin.getWorld(), new RemoveBindS2CPayload(bindOriginId));
    }

    private void sendPacketToAllPlayers(ServerWorld world, CustomPayload payload) {
        for (ServerPlayerEntity player : PlayerLookup.world(world)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Nullable
    private LivingEntity getLivingEntity(UUID uuid, MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            if (world.getEntity(uuid) instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }
        return null;
    }

    public void afterPlayerChangeWorld(ServerPlayerEntity newEntity, ServerWorld origin, ServerWorld destination) {
        afterChangeWorld(newEntity, null, origin, destination);
    }

    public void afterChangeWorld(Entity originalEntity, Entity newEntity, ServerWorld origin, ServerWorld destination) {
        UUID entityUuid = originalEntity.getUuid();
        if (bindPairs.containsKey(entityUuid)) {
            removeBind(entityUuid, origin.getServer());
        }

        if (bindPairs.containsValue(entityUuid)) {
            for (Iterator<Map.Entry<UUID, UUID>> it = bindPairs.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<UUID, UUID> iterator = it.next();
                UUID bindOriginUuid = iterator.getKey();
                UUID boundEntityUuid = iterator.getValue();

                if (entityUuid == boundEntityUuid) {
                    it.remove();
                    removeBind(bindOriginUuid, boundEntityUuid, origin.getServer());
                }
            }
        }
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        for (Iterator<Map.Entry<UUID, UUID>> it = bindPairs.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, UUID> iterator = it.next();
            UUID bindOriginUuid = iterator.getKey();
            UUID boundEntityUuid = iterator.getValue();
            LivingEntity bindOrigin = getLivingEntity(bindOriginUuid, server);
            LivingEntity boundEntity = getLivingEntity(boundEntityUuid, server);

            boolean bindOriginNonExistent = bindOrigin == null || bindOrigin.isDead();
            boolean boundEntityNonExistent = boundEntity == null || boundEntity.isDead();
            boolean boundEntityFrozen = frozenBoundEntities.containsKey(boundEntityUuid);;

            if (bindOriginNonExistent || (boundEntityNonExistent && !boundEntityFrozen)) {
                it.remove();
                removeBind(bindOriginUuid, boundEntityUuid, server);
                if (!boundEntityNonExistent) {
                    frozenBoundEntities.remove(boundEntityUuid);
                }
            }
        }

        bindPairs.forEach(((bindOriginId, boundEntityId) -> {
            LivingEntity bindOrigin = getLivingEntity(bindOriginId, server);
            LivingEntity boundEntity = getLivingEntity(boundEntityId, server);
            assert bindOrigin != null;
            assert boundEntity != null;

            Vec3d frozenBoundEntityPosition = frozenBoundEntities.get(boundEntityId);
            if (frozenBoundEntityPosition != null) {
                sendPacketToAllPlayers((ServerWorld) bindOrigin.getWorld(), new AddBindS2CPayload(bindOrigin, frozenBoundEntityPosition));
                return;
            }

            sendPacketToAllPlayers((ServerWorld) bindOrigin.getWorld(), new AddBindS2CPayload(bindOrigin, boundEntity));
        }));
    }
}
