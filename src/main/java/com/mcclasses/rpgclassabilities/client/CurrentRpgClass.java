package com.mcclasses.rpgclassabilities.client;

import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mcclasses.rpgclassabilities.payload.s2c.UpdateCurrentClassS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CurrentRpgClass {
    private static RpgClass currentRpgClass;

    public static RpgClass getCurrentRpgClass() {
        return currentRpgClass;
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(UpdateCurrentClassS2CPayload.ID, ((payload, context) -> {
            currentRpgClass = payload.newRpgClass();
            context.player().networkHandler.sendChatMessage("The client says our class is: " + currentRpgClass.asString());
        }));
    }
}
