package com.mcclasses.rpgclassabilities;

import java.util.logging.Logger;

public class LoggerHelper {
    static private final String MOD_ID = "rpgclassabilities";
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(MOD_ID);

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static void printClassName(Object object) {
        LOGGER.info(object.getClass().getSimpleName());
    }
}
