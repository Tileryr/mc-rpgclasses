package com.mcclasses.rpgclassabilities.MixinDuckInterfaces;

public interface FovOveridable {


    void rpgclassabilities$setOverrideFov(boolean overrideFov);
    boolean rpgclassabilities$getOverrideFov();

    void rpgclassabilities$setFovOverrideValue(float fovOverrideValue);

    default void rpgclassabilities$activateOverrideFov(float fovOverrideValue) {
        rpgclassabilities$setOverrideFov(true);
        rpgclassabilities$setFovOverrideValue(fovOverrideValue);
    }
}
