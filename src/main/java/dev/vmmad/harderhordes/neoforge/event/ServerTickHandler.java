package dev.vmmad.harderhordes.neoforge.event;

import dev.vmmad.harderhordes.config.HordeConfig;
import dev.vmmad.harderhordes.config.HordeConfigHolder;
import dev.vmmad.harderhordes.horde.HordeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Throttled tick hook: runs the horde check only once every
 * {@code spawn.checkIntervalTicks}, keeping the per-tick cost near zero
 * (MOD_CODING_CONVENTION.md §5 — nothing heavy on the tick hot path).
 */
public final class ServerTickHandler {

    private int counter = 0;

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        HordeConfig cfg = HordeConfigHolder.get();
        if (++counter < cfg.spawn().checkIntervalTicks()) {
            return;
        }
        counter = 0;
        HordeManager.tick(event.getServer());
    }
}
