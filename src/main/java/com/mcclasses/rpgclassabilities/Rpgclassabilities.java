package com.mcclasses.rpgclassabilities;

import net.fabricmc.api.ModInitializer;

import java.util.logging.Logger;

public class Rpgclassabilities implements ModInitializer {
    public static final String MOD_ID = "rpgclassabilities";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        LOGGER.info("Hello Fabric world!");
    }
}
