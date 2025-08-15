package com.mcclasses.rpgclassabilities.playerAbillities;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BindManager {
    private static final double TAKE_AMOUNT = 0.2;
    private static final Identifier BOUND_MODIFIER_ID = Identifier.of(Rpgclassabilities.MOD_ID, "bound");
    private static final Identifier BINDING_MODIFIER_ID = Identifier.of(Rpgclassabilities.MOD_ID, "binding");
    private final Map<UUID, UUID> bindPairs = new HashMap<>();

    public void addBind(LivingEntity bindOrigin, LivingEntity boundEntity) {
        bindPairs.put(bindOrigin.getUuid(), boundEntity.getUuid());
        EntityAttributeInstance boundEntityMaxHealthAttribute = boundEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        EntityAttributeInstance bindOriginEntityMaxHealthAttribute = bindOrigin.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        assert boundEntityMaxHealthAttribute != null;

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
    }

    public void removeBind(LivingEntity bindOrigin, ServerWorld world) {
        removeBind(bindOrigin.getUuid(), world);
    }

    public void removeBind(UUID bindOriginId, ServerWorld world) {
        if (!(world.getEntity(bindOriginId) instanceof LivingEntity bindOrigin)) {return;}
        if (!(world.getEntity(bindPairs.get(bindOriginId)) instanceof LivingEntity boundEntity)) {return;}

        EntityAttributeInstance bindOriginMaxHealthInstance = bindOrigin.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        EntityAttributeInstance boundEntityMaxHealthInstance = boundEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH);

        assert bindOriginMaxHealthInstance != null;
        assert boundEntityMaxHealthInstance != null;

        bindOriginMaxHealthInstance.removeModifier(BINDING_MODIFIER_ID);
        boundEntityMaxHealthInstance.removeModifier(BOUND_MODIFIER_ID);

        bindPairs.remove(bindOriginId);
    }
}
