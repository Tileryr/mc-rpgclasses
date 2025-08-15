package com.mcclasses.rpgclassabilities.data;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface RpgclassabilitiesDamageTypes {
    RegistryKey<DamageType> BIND = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Rpgclassabilities.MOD_ID, "bind"));

    static void bootstrap(Registerable<DamageType> damageTypeRegisterable) {
        damageTypeRegisterable.register(BIND, new DamageType("bind", 1F));
        LoggerHelper.getLOGGER().info("ASAS");
    }
}
