package dev.vmmad.harderhordes.horde;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import dev.vmmad.harderhordes.HarderHordes;
import dev.vmmad.harderhordes.config.HordeConfig;
import dev.vmmad.harderhordes.config.HordeConfigHolder;
import dev.vmmad.harderhordes.horde.difficulty.ProgressionScore;
import dev.vmmad.harderhordes.horde.difficulty.ProgressionScorer;
import dev.vmmad.harderhordes.horde.spawn.HordeSpawner;
import dev.vmmad.harderhordes.horde.spawn.SpawnContext;
import dev.vmmad.harderhordes.horde.spawn.SpawnPositionFinder;
import dev.vmmad.harderhordes.horde.spawn.SpawnScheduler;
import dev.vmmad.harderhordes.horde.type.HordeComposition;
import dev.vmmad.harderhordes.horde.type.HordeDefinition;
import dev.vmmad.harderhordes.horde.type.HordeTypeSelector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * The brain. Called on a throttled interval (never every tick) by the loader's
 * tick hook; runs the night-weighted per-player roll and, when it fires, decides
 * and spawns a horde. Also exposes {@link #forceSpawn} for the debug command.
 */
public final class HordeManager {

    private static final int RECENT_LIMIT = 10;

    /** A short history of recent spawns, surfaced by {@code /harderhordes recent}. */
    public record RecentHorde(long worldDay, long dayTimeTicks, long gameTime, String type, boolean hybrid,
                              int x, int y, int z, String player, int size) {}

    private static final Deque<RecentHorde> RECENT = new ArrayDeque<>();

    private HordeManager() {
    }

    public static List<RecentHorde> recent() {
        return List.copyOf(RECENT);
    }

    private static synchronized void recordRecent(RecentHorde horde) {
        RECENT.addFirst(horde);
        while (RECENT.size() > RECENT_LIMIT) {
            RECENT.removeLast();
        }
    }

    public static void tick(MinecraftServer server) {
        HordeConfig cfg = HordeConfigHolder.get();
        for (ServerLevel level : server.getAllLevels()) {
            if (cfg.spawn().overworldOnly() && !level.dimension().equals(Level.OVERWORLD)) {
                continue;
            }
            if (level.players().isEmpty()) {
                continue;
            }
            for (ServerPlayer player : List.copyOf(level.players())) {
                tryHorde(level, player, cfg);
            }
        }
    }

    private static void tryHorde(ServerLevel level, ServerPlayer player, HordeConfig cfg) {
        RandomSource rng = level.getRandom();
        if (!SpawnScheduler.ready(player, level, cfg)) {
            return;
        }

        // Early-game protection: nothing before minWorldDay, then ramp the rate up to full by fullFrequencyDay.
        long worldDay = level.getDayTime() / 24000L;
        if (worldDay < cfg.spawn().minWorldDay()) {
            return;
        }
        double rampFactor = frequencyRamp(worldDay, cfg);

        boolean night = !level.isDay();
        double chance = cfg.spawn().dayBaseChance() * (night ? cfg.spawn().nightMultiplier() : 1.0) * rampFactor;
        if (rng.nextDouble() >= chance) {
            return;
        }

        SpawnContext ctx = SpawnPositionFinder.find(level, player.blockPosition(), cfg, rng);
        if (ctx == null) {
            return;
        }
        ProgressionScore score = ProgressionScorer.score(level, player, cfg);
        HordeComposition comp = HordeTypeSelector.pick(ctx, score, cfg, rng);
        if (comp == null) {
            return;
        }
        int spawned = HordeSpawner.spawn(level, player, ctx, score, comp, cfg, rng);
        if (spawned > 0) {
            SpawnScheduler.record(player, level);
            recordRecent(new RecentHorde(worldDay, level.getDayTime() % 24000L, level.getGameTime(),
                    comp.lead().id().toString(), comp.pools().size() > 1,
                    ctx.pos().getX(), ctx.pos().getY(), ctx.pos().getZ(), player.getName().getString(), spawned));
            HarderHordes.LOGGER.debug("Spawned horde {} ({} mobs) near {} (day {}, score {})",
                    comp.lead().id(), spawned, player.getName().getString(), worldDay,
                    String.format("%.2f", score.total()));
        }
    }

    /** 0 at minWorldDay, ramping linearly to 1 by fullFrequencyDay (and capped there). */
    private static double frequencyRamp(long worldDay, HordeConfig cfg) {
        int start = cfg.spawn().minWorldDay();
        int full = cfg.spawn().fullFrequencyDay();
        if (full <= start) {
            return 1.0;
        }
        double t = (double) (worldDay - start) / (double) (full - start);
        return Math.max(0.0, Math.min(1.0, t));
    }

    /** Force a horde for the debug command, bypassing the night/RNG/cooldown gates. */
    public static int forceSpawn(ServerLevel level, ServerPlayer player, HordeDefinition def, boolean hybrid) {
        HordeConfig cfg = HordeConfigHolder.get();
        RandomSource rng = level.getRandom();
        SpawnContext ctx = SpawnPositionFinder.find(level, player.blockPosition(), cfg, rng);
        if (ctx == null) {
            ctx = new SpawnContext(player.blockPosition(), player.isInWater());
        }
        ProgressionScore score = ProgressionScorer.score(level, player, cfg);

        HordeComposition comp;
        if (hybrid) {
            List<HordeDefinition> candidates = HordeTypeSelector.candidates(ctx, cfg);
            if (candidates.isEmpty()) {
                candidates = List.of(def);
            }
            HordeDefinition primary = candidates.contains(def) ? def : candidates.get(0);
            comp = HordeComposition.hybrid(primary, candidates, rng);
        } else {
            comp = HordeComposition.single(def);
        }
        int spawned = HordeSpawner.spawn(level, player, ctx, score, comp, cfg, rng);
        if (spawned > 0) {
            recordRecent(new RecentHorde(level.getDayTime() / 24000L, level.getDayTime() % 24000L,
                    level.getGameTime(), comp.lead().id().toString(), comp.pools().size() > 1,
                    ctx.pos().getX(), ctx.pos().getY(), ctx.pos().getZ(),
                    player.getName().getString(), spawned));
        }
        return spawned;
    }
}
