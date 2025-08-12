package com.mcclasses.rpgclassabilities.timers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

import java.util.*;

public class TickScheduler implements ServerTickEvents.EndTick, ClientTickEvents.EndTick {
    private static class ScheduledCallback {
        public int ticksUntilCall;
        public Runnable callback;

        ScheduledCallback(int ticksUntilCall, Runnable callback) {
            this.ticksUntilCall = ticksUntilCall;
            this.callback = callback;
        }
    }

    private final Map<UUID, ScheduledCallback> activeTimers = new HashMap<>();
    private final Map<UUID, ScheduledCallback> toBeScheduledTimers = new HashMap<>();

    public void addTimer(int timerTickLength, Runnable callback) {
        addTimer(UUID.randomUUID(), timerTickLength, callback);
    }

    public void addTimer(UUID id, int timerTickLength, Runnable callback) {
        toBeScheduledTimers.put(id, new ScheduledCallback(timerTickLength, callback));
    }

    private void tick() {
        activeTimers.forEach(((uuid, scheduledCallback) -> {
            if (--scheduledCallback.ticksUntilCall == 0) {
                scheduledCallback.callback.run();
            }
        }));

        activeTimers.entrySet().removeIf((entry -> entry.getValue().ticksUntilCall == 0));

        activeTimers.putAll(toBeScheduledTimers);
        toBeScheduledTimers.clear();
    }

    public int getTicksLeft(UUID id) {
        return activeTimers.get(id).ticksUntilCall;
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        tick();
    }

    @Override
    public void onEndTick(MinecraftClient server) {
        tick();
    }
}
