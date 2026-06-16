package dev.vmmad.harderhordes.horde.spawn;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import dev.vmmad.harderhordes.config.HordeConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Per-player cooldown so a player isn't swarmed back-to-back. Keyed by UUID and
 * compared against world game-time, so it survives dimension changes.
 */
public final class SpawnScheduler {

    private static final Map<UUID, Long> LAST_SPAWN_GAME_TIME = new ConcurrentHashMap<>();

    private SpawnScheduler() {
    }

    public static boolean ready(ServerPlayer player, ServerLevel level, HordeConfig cfg) {
        Long last = LAST_SPAWN_GAME_TIME.get(player.getUUID());
        if (last == null) {
            return true;
        }
        long elapsed = level.getGameTime() - last;
        return elapsed >= (long) cfg.spawn().minSecondsBetweenHordes() * 20L;
    }

    public static void record(ServerPlayer player, ServerLevel level) {
        LAST_SPAWN_GAME_TIME.put(player.getUUID(), level.getGameTime());
    }

    public static void forget(UUID playerId) {
        LAST_SPAWN_GAME_TIME.remove(playerId);
    }
}
