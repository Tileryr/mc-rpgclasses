package com.mcclasses.rpgclassabilities.client;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.FovOveridable;
import com.mcclasses.rpgclassabilities.PlayerDash;
import com.mcclasses.rpgclassabilities.payload.c2s.PlayerDashC2SPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.OpenClassSelectS2CPayload;
import com.mcclasses.rpgclassabilities.timers.TickScheduler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;


public class RpgclassabilitiesClient implements ClientModInitializer {
    public static final TickScheduler SCHEDULER = new TickScheduler();
    private static KeyBinding keyBinding;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(SCHEDULER);
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rpgclassabilities.checkclass",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.rpgclassabilities.rpgclasses"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed())
            {
                if (client.player instanceof FovOveridable player) {
                    player.rpgclassabilities$setFovOverrideValue(1.6F);
                    player.rpgclassabilities$setOverrideFov(true);

                    SCHEDULER.addTimer((int) PlayerDash.DASH_TIME, () -> {
                        player.rpgclassabilities$setOverrideFov(false);
                    });
                }

                ClientPlayNetworking.send(new PlayerDashC2SPayload());
            }
        });
        CurrentRpgClass.activate();
        ClientPlayNetworking.registerGlobalReceiver(OpenClassSelectS2CPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.setScreen(
                    new ClassScreen(Text.empty())
            );
        });

        ClientPlayConnectionEvents.JOIN.register((t, a, client) -> {

            double moveSpeed = client.player.getAttributeValue(EntityAttributes.MOVEMENT_SPEED);
            LoggerHelper.getLOGGER().info(Double.toString(moveSpeed));
        });
    }
}
