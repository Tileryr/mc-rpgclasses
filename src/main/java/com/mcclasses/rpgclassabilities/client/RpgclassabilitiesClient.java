package com.mcclasses.rpgclassabilities.client;

import com.mcclasses.rpgclassabilities.MixinDuckInterfaces.FovOveridable;
import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.client.render.BindProjectileEntityModel;
import com.mcclasses.rpgclassabilities.client.render.BindProjectileEntityRenderer;
import com.mcclasses.rpgclassabilities.payload.PayloadRegister;
import com.mcclasses.rpgclassabilities.playerAbillities.PlayerDash;
import com.mcclasses.rpgclassabilities.timers.TickScheduler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;


public class RpgclassabilitiesClient implements ClientModInitializer {
    public static final TickScheduler SCHEDULER = new TickScheduler();

    public static final EntityModelLayer BIND_PROJECTILE_MODEL_LAYER = new EntityModelLayer(Rpgclassabilities.BIND_PROJECTILE_ID, "main");

    private static final KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.rpgclassabilities.ability_1",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "category.rpgclassabilities.rpgclasses"
    ));

    private void dash_player(ClientPlayerEntity clientPlayer) {
        if (clientPlayer instanceof FovOveridable player) {
            player.rpgclassabilities$setFovOverrideValue(1.6F);
            player.rpgclassabilities$setOverrideFov(true);

            SCHEDULER.addTimer((int) PlayerDash.DASH_TIME, () -> {
                player.rpgclassabilities$setOverrideFov(false);
            });
        }
    }

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Rpgclassabilities.BIND_PROJECTILE, BindProjectileEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(BIND_PROJECTILE_MODEL_LAYER, BindProjectileEntityModel::getTexturedModelData);

        CurrentRpgClass.register();
        ClientPlayNetworking.registerGlobalReceiver(PayloadRegister.OPEN_CLASS_SELECT.id, (payload, context) -> {
            context.client().setScreen(new ClassScreen(Text.empty()));
        });

        ClientTickEvents.END_CLIENT_TICK.register(SCHEDULER);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                dash_player(client.player);
                ClientPlayNetworking.send(PayloadRegister.ABILITY_ONE_PRESSED);
            }
        });

        ClientPlayConnectionEvents.JOIN.register((t, a, client) -> {});
    }
}
