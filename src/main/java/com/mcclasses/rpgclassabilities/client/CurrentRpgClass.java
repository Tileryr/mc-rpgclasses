package com.mcclasses.rpgclassabilities.client;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.RpgClass;
import com.mcclasses.rpgclassabilities.UpdateCurrentClassS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;

public class CurrentRpgClass {
    private static RpgClass currentRpgClass;

    public static RpgClass getCurrentRpgClass() {
        return currentRpgClass;
    }

    public static void activate() {
        ClientPlayNetworking.registerGlobalReceiver(UpdateCurrentClassS2CPayload.ID, ((payload, context) -> {
            currentRpgClass = payload.newRpgClass();
            context.player().networkHandler.sendChatMessage("The client says our class is: " + currentRpgClass.asString());
        }));
    }
}
