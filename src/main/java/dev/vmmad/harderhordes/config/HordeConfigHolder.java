package dev.vmmad.harderhordes.config;

/**
 * Holds the active {@link HordeConfig} snapshot. The loader's config layer
 * swaps in a fresh snapshot on load/reload; the core reads the current one.
 * {@code volatile} so a config reload on the config thread is visible to the
 * server thread without locking.
 */
public final class HordeConfigHolder {

    private static volatile HordeConfig current = HordeConfig.defaults();

    private HordeConfigHolder() {
    }

    public static HordeConfig get() {
        return current;
    }

    public static void set(HordeConfig config) {
        current = config;
    }
}
