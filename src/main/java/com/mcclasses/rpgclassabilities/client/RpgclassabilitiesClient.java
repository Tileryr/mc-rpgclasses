package com.mcclasses.rpgclassabilities.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class RpgclassabilitiesClient implements ClientModInitializer {
    private static KeyBinding keyBinding;

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
                client.player.sendMessage(Text.literal("C yah!"), false);
            }
        });
    }
}
