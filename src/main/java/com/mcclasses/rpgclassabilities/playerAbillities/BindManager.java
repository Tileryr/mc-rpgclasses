package com.mcclasses.rpgclassabilities.playerAbillities;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.apache.commons.logging.Log;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class BindManager implements ServerTickEvents.EndTick {
    private static final double TAKE_AMOUNT = 0.2;
    private static final Identifier BOUND_MODIFIER_ID = Identifier.of(Rpgclassabilities.MOD_ID, "bound");
    private static final Identifier BINDING_MODIFIER_ID = Identifier.of(Rpgclassabilities.MOD_ID, "binding");
    private final Map<UUID, UUID> bindPairs = new HashMap<>();

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

        if (!boundEntityMaxHealthAttribute.hasModifier(BOUND_MODIFIER_ID)) {
            boundEntityMaxHealthAttribute.addTemporaryModifier(new EntityAttributeModifier(
                    BOUND_MODIFIER_ID,
                    -TAKE_AMOUNT,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        } else {
            LoggerHelper.getLOGGER().info("Error");
        }

        if (!bindOriginEntityMaxHealthAttribute.hasModifier(BINDING_MODIFIER_ID)) {
            double boundEntityMaxHealth = boundEntityMaxHealthAttribute.getBaseValue();
            double takenHealth = boundEntityMaxHealth * TAKE_AMOUNT;
            bindOriginEntityMaxHealthAttribute.addTemporaryModifier(new EntityAttributeModifier(
                    BINDING_MODIFIER_ID,
                    takenHealth,
                    EntityAttributeModifier.Operation.ADD_VALUE
            ));
        } else {
            LoggerHelper.getLOGGER().info("Error");
        }
    }

    public void removeBind(LivingEntity bindOrigin, MinecraftServer server) {
        removeBind(bindOrigin.getUuid(), server);
    }

    public void removeBind(UUID bindOriginId, MinecraftServer server) {
        LivingEntity bindOrigin = getLivingEntity(bindOriginId, server);
        if (bindOrigin != null) {
            EntityAttributeInstance bindOriginMaxHealthInstance = bindOrigin.getAttributeInstance(EntityAttributes.MAX_HEALTH);
            assert bindOriginMaxHealthInstance != null;
            bindOriginMaxHealthInstance.removeModifier(BINDING_MODIFIER_ID);
        }

        LivingEntity boundEntity = getLivingEntity(bindOriginId, server);
        if (boundEntity != null) {
            EntityAttributeInstance boundEntityMaxHealthInstance = boundEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH);
            assert boundEntityMaxHealthInstance != null;
            boundEntityMaxHealthInstance.removeModifier(BOUND_MODIFIER_ID);
        }

        bindPairs.remove(bindOriginId);
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

    @Override
    public void onEndTick(MinecraftServer server) {
        for (Iterator<Map.Entry<UUID, UUID>> it = bindPairs.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, UUID> iterator = it.next();
            UUID bindOriginUuid = iterator.getKey();
            UUID boundEntityUuid = iterator.getValue();
            LivingEntity bindOrigin = getLivingEntity(bindOriginUuid, server);
            LivingEntity boundEntity = getLivingEntity(boundEntityUuid, server);
            if (bindOrigin == null || boundEntity == null || bindOrigin.isDead() || boundEntity.isDead()) {
                it.remove();
                removeBind(bindOriginUuid, server);
            }
        }
    }
}
