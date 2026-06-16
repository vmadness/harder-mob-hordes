package dev.vmmad.harderhordes.neoforge;

import dev.vmmad.harderhordes.HarderHordes;
import dev.vmmad.harderhordes.config.HordeConfigHolder;
import dev.vmmad.harderhordes.neoforge.config.NeoForgeHordeConfig;
import dev.vmmad.harderhordes.neoforge.event.CommandHandler;
import dev.vmmad.harderhordes.neoforge.event.ServerTickHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * NeoForge bootstrap (the thin loader adapter). Registers config + game-event
 * handlers, then hands off to the loader-agnostic {@link HarderHordes#init()}.
 */
@Mod(HarderHordes.MOD_ID)
public final class HarderHordesNeoForge {

    public HarderHordesNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        HarderHordes.init();

        // COMMON (not SERVER) so the config lives once in the global config/ folder
        // rather than per-world serverconfig/. All logic is server-side, no client reads it.
        modContainer.registerConfig(ModConfig.Type.COMMON, NeoForgeHordeConfig.SPEC);
        modEventBus.addListener(this::onConfigChanged);

        NeoForge.EVENT_BUS.register(new ServerTickHandler());
        NeoForge.EVENT_BUS.register(new CommandHandler());
    }

    private void onConfigChanged(ModConfigEvent event) {
        if (event.getConfig().getSpec() == NeoForgeHordeConfig.SPEC) {
            HordeConfigHolder.set(NeoForgeHordeConfig.snapshot());
        }
    }
}
