package com.mcclasses.rpgclassabilities.datagen;

import com.mcclasses.rpgclassabilities.data.RpgclassabilitiesDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class RpgclassabilitiesDamageTypesProvider extends FabricDynamicRegistryProvider {

    public RpgclassabilitiesDamageTypesProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup, Entries entries) {
        entries.addAll(wrapperLookup.getOrThrow(RegistryKeys.DAMAGE_TYPE));
    }

    public static void bootstrap(Registerable<DamageType> damageTypeRegisterable) {
        RpgclassabilitiesDamageTypes.bootstrap(damageTypeRegisterable);
//        damageTypeRegisterable.register(TestDamageTypes.TEST_DAMAGE_TYPE, new DamageType("test_damage_type", 0.1F));
    }

    @Override
    public String getName() {
        return "Rpgclassabilities Damage Types Provider";
    }
}
