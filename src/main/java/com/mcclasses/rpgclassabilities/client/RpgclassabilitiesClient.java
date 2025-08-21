package com.mcclasses.rpgclassabilities.client;

import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.client.render.BindChainRenderer;
import com.mcclasses.rpgclassabilities.client.render.BindProjectileEntityModel;
import com.mcclasses.rpgclassabilities.client.render.BindProjectileEntityRenderer;
import com.mcclasses.rpgclassabilities.payload.PayloadRegister;
import com.mcclasses.rpgclassabilities.payload.s2c.AbilityUseFailedS2CPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.AddBindS2CPayload;
import com.mcclasses.rpgclassabilities.payload.s2c.RemoveBindS2CPayload;
import com.mcclasses.rpgclassabilities.playerAbillities.PlayerAbilities;
import com.mcclasses.rpgclassabilities.playerAbillities.PlayerAbilitiesClient;
import com.mcclasses.rpgclassabilities.playerAbillities.PlayerTransmute;
import com.mcclasses.rpgclassabilities.timers.TickScheduler;
import com.mcclasses.rpgclassabilities.util.Conversion;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;


public class RpgclassabilitiesClient implements ClientModInitializer {
    public static final TickScheduler SCHEDULER = new TickScheduler();
    public static final BindChainRenderer BIND_CHAIN_RENDERER = new BindChainRenderer();
    public static final PlayerAbilitiesClient PLAYER_ABILITIES = new PlayerAbilitiesClient();

    private static final KeyBinding abilityOneKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.rpgclassabilities.ability_1",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "category.rpgclassabilities.rpgclasses"
    ));

    private static final KeyBinding openClassScreenKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.rpgclassabilities.open_class_screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.rpgclassabilities.rpgclasses"
    ));


    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Rpgclassabilities.BIND_PROJECTILE, BindProjectileEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(BindProjectileEntityRenderer.BIND_PROJECTILE_MODEL_LAYER, BindProjectileEntityModel::getTexturedModelData);
        CurrentRpgClass.register();

        ParticleFactoryRegistry.getInstance().register(PlayerTransmute.TRANSMUTE_PARTICLE, EndRodParticle.Factory::new);

        PLAYER_ABILITIES.register();
        ClientPlayNetworking.registerGlobalReceiver(PayloadRegister.OPEN_CLASS_SELECT.id, (payload, context) -> {
            context.client().setScreen(new ClassSelectScreen(Text.empty()));
        });
        ClientPlayNetworking.registerGlobalReceiver(AddBindS2CPayload.ID, (payload, context) -> {
            BIND_CHAIN_RENDERER.addChain(payload.bindId, payload.bindOriginPosition, payload.boundEntityPosition);
        });
        ClientPlayNetworking.registerGlobalReceiver(RemoveBindS2CPayload.ID, (payload, context) -> {
            BIND_CHAIN_RENDERER.removeChain(payload.bindOriginId());
        });
        ClientPlayNetworking.registerGlobalReceiver(AbilityUseFailedS2CPayload.ID, ((payload, context) -> {
            context.player().sendMessage( Text.literal(
                            "Ability cooldown: " + Conversion.ticksToSeconds(payload.ticksUntilActive()) + "s"),
                    true
            );
        }));

        ClientTickEvents.END_CLIENT_TICK.register(SCHEDULER::onEndTick);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (abilityOneKeyBinding.wasPressed()) {
                PLAYER_ABILITIES.runAbilityOneClient(CurrentRpgClass.getCurrentRpgClass(), client.player);
                ClientPlayNetworking.send(PayloadRegister.ABILITY_ONE_PRESSED);
            }
            while (openClassScreenKeyBinding.wasPressed()) {
                client.setScreen(new ClassScreen(Text.empty()));
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(PlayerTransmute::onEndTickClient);
        ClientPlayConnectionEvents.JOIN.register((t, a, client) -> {});
    }
}
