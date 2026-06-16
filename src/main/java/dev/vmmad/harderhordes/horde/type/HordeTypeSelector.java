package dev.vmmad.harderhordes.horde.type;

import java.util.ArrayList;
import java.util.List;

import dev.vmmad.harderhordes.config.HordeConfig;
import dev.vmmad.harderhordes.horde.difficulty.ProgressionScore;
import dev.vmmad.harderhordes.horde.spawn.SpawnContext;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * Chooses which horde to spawn: filters definitions by enablement and
 * environment, rolls the rare elite override, then does a weighted pick — and
 * above the configured score gate may upgrade the result to a hybrid blend.
 */
public final class HordeTypeSelector {

    private HordeTypeSelector() {
    }

    public static HordeComposition pick(SpawnContext ctx, ProgressionScore score, HordeConfig cfg, RandomSource rng) {
        // Rare elite override first.
        HordeDefinition elite = HordeTypeRegistry.get(HordeDefinitions.ELITE_ID);
        if (elite != null
                && HordeDefinitions.isEnabled(elite, cfg)
                && environmentMatches(elite.environment(), ctx)
                && score.total() >= cfg.types().eliteScoreGate()) {
            double chance = Mth.clamp(
                    cfg.types().eliteBaseChance() + score.total() * cfg.types().elitePerScore(),
                    0.0, cfg.types().eliteMaxChance());
            if (rng.nextDouble() < chance) {
                return HordeComposition.single(elite);
            }
        }

        List<HordeDefinition> candidates = candidates(ctx, cfg);
        if (candidates.isEmpty()) {
            HordeDefinition zombie = HordeTypeRegistry.get(HordeDefinitions.ZOMBIE_ID);
            return zombie == null ? null : HordeComposition.single(zombie);
        }

        HordeDefinition primary = weightedPick(candidates, rng);
        if (score.total() >= cfg.hybrid().scoreGate()
                && candidates.size() > 1
                && rng.nextDouble() < cfg.hybrid().chance()) {
            return HordeComposition.hybrid(primary, candidates, rng);
        }
        return HordeComposition.single(primary);
    }

    /** Enabled, non-elite definitions whose environment matches the spawn context. */
    public static List<HordeDefinition> candidates(SpawnContext ctx, HordeConfig cfg) {
        List<HordeDefinition> candidates = new ArrayList<>();
        for (HordeDefinition def : HordeTypeRegistry.all()) {
            if (def.elite() || !HordeDefinitions.isEnabled(def, cfg)) {
                continue;
            }
            if (environmentMatches(def.environment(), ctx)) {
                candidates.add(def);
            }
        }
        return candidates;
    }

    private static boolean environmentMatches(Environment env, SpawnContext ctx) {
        if (env == Environment.ANY) {
            return true;
        }
        return ctx.aquatic() ? env == Environment.WATER : env == Environment.LAND;
    }

    private static HordeDefinition weightedPick(List<HordeDefinition> defs, RandomSource rng) {
        double total = 0.0;
        for (HordeDefinition def : defs) {
            total += Math.max(0.0, def.baseWeight());
        }
        if (total <= 0.0) {
            return defs.get(rng.nextInt(defs.size()));
        }
        double roll = rng.nextDouble() * total;
        for (HordeDefinition def : defs) {
            roll -= Math.max(0.0, def.baseWeight());
            if (roll <= 0.0) {
                return def;
            }
        }
        return defs.get(defs.size() - 1);
    }
}
