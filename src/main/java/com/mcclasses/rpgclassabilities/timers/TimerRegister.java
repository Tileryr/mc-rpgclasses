package com.mcclasses.rpgclassabilities.timers;

import com.mcclasses.rpgclassabilities.PlayerDash;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class TimerRegister {
    static private final ServerTickEvents.EndTick[] TIMERS = {
            PlayerDash.DashTeleportTimer.INSTANCE
    };

    static public void register() {
        for (ServerTickEvents.EndTick timerInstance : TIMERS) {
            ServerTickEvents.END_SERVER_TICK.register(timerInstance);
        }
    }
}
