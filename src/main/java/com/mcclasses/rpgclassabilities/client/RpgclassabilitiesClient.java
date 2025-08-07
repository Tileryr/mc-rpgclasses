package com.mcclasses.rpgclassabilities.client;

import com.mcclasses.rpgclassabilities.LoggerHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.logging.Logger;

public class RpgclassabilitiesClient implements ClientModInitializer {
    private static KeyBinding keyBinding;
    public static final String MOD_ID = "rpgclassabilities";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rpgclassabilities.checkclass",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.rpgclassabilities.rpgclasses"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed())
            {
                MinecraftClient.getInstance().setScreen(
                        new ClassScreen(Text.empty())
                );
                double moveSpeed = PlayerEntity.createPlayerAttributes().build().getValue(EntityAttributes.MOVEMENT_SPEED);
                double nextMoveSpeed = new AttributeContainer(PlayerEntity.createPlayerAttributes().build()).getValue(EntityAttributes.MOVEMENT_SPEED);

//                client.player.getAttributes().setFrom(new AttributeContainer(PlayerEntity.createPlayerAttributes().build()));

                double currentMoveSpeed = client.player.getAttributeValue(EntityAttributes.MOVEMENT_SPEED);

                client.player.sendMessage(Text.literal(Double.toString(moveSpeed)), false);
                client.player.sendMessage(Text.literal(Double.toString(nextMoveSpeed)), false);
                client.player.sendMessage(Text.literal(Double.toString(currentMoveSpeed)), false);
            }


        });
        ServerPlayConnectionEvents.JOIN.register((a, b, c) -> {
            a.player.getAttributes().getCustomInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(1.0);
            LoggerHelper.getLOGGER().info("ServerConnected");
        });
        ClientPlayConnectionEvents.JOIN.register((t, a, client) -> {
            client.setScreen(
                    new ClassScreen(Text.empty())
            );
            double moveSpeed = client.player.getAttributeValue(EntityAttributes.MOVEMENT_SPEED);
            LOGGER.info(Double.toString(moveSpeed));
        });
    }
}
