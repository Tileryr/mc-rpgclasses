package com.mcclasses.rpgclassabilities.MixinDuckInterfaces;

public interface CanCharge {
    void rpgclassabilities$setCurrentlyCharging(boolean charging);
    boolean rpgclassabilities$getCurrentlyCharing();

    void rpgclassabilities$setChargeYaw(float yaw);
    float rpgclassabilities$getChargeYaw();

    void rpgclassabilities$setCurrentChangeTime(int chargeTime);
    int rpgclassabilities$getCurrentChangeTime();

    default void rpgclassabilities$activateCharge(float yaw) {
        rpgclassabilities$setCurrentlyCharging(true);
        rpgclassabilities$setChargeYaw(yaw);
    }
}
