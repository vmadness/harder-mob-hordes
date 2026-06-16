package dev.vmmad.harderhordes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loader-agnostic core of Harder Hordes.
 *
 * <p>Everything in the {@code dev.vmmad.harderhordes} package (excluding the
 * {@code .neoforge} sub-package) must stay free of loader-specific imports
 * ({@code net.neoforged.*}). Loader concerns are reached through
 * {@link dev.vmmad.harderhordes.platform.Services}. This keeps the bulk of the
 * mod portable so a Fabric module can be added later as a thin adapter
 * (MOD_CODING_CONVENTION.md §1).
 */
public final class HarderHordes {

    public static final String MOD_ID = "harder_hordes";
    public static final Logger LOGGER = LoggerFactory.getLogger("HarderHordes");

    /** Entity (scoreboard) tag marking a mob spawned as part of a horde. */
    public static final String HORDE_TAG = "harder_hordes:horde";
    /** Entity tag marking the single reward-bearer of a horde (its gear can drop). */
    public static final String REWARD_TAG = "harder_hordes:reward";

    private HarderHordes() {
    }

    /**
     * Shared initialization entry point, invoked once by the active loader's
     * bootstrap (e.g. the NeoForge {@code @Mod} constructor) after platform
     * services are available.
     */
    public static void init() {
        LOGGER.info("Harder Hordes core initializing");
        dev.vmmad.harderhordes.horde.type.HordeDefinitions.bootstrap();
    }
}
