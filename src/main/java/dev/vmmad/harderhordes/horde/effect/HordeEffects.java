package dev.vmmad.harderhordes.horde.effect;

import dev.vmmad.harderhordes.config.HordeConfig;
import dev.vmmad.harderhordes.horde.difficulty.ProgressionScore;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;

/**
 * Applies status-effect "flavor" to horde members. Creepers get the richer
 * feature set (see {@link CreeperFeatures}); other mobs occasionally get a small
 * buff at high scores so late-game land hordes feel more dangerous too.
 */
public final class HordeEffects {

    private static final int LONG_DURATION = 20 * 60 * 30; // 30 minutes, effectively the mob's life

    private HordeEffects() {
    }

    /**
     * @param chargedBudget single-element array tracking how many charged creepers
     *                      this horde may still produce (decremented in place)
     */
    public static void apply(ServerLevel level, Mob mob, ProgressionScore score,
                             HordeConfig cfg, RandomSource rng, int[] chargedBudget) {
        if (mob instanceof Creeper creeper) {
            CreeperFeatures.apply(creeper, score, cfg, rng, chargedBudget);
            return;
        }
        // Non-creeper: a rare Speed buff once the horde is genuinely dangerous.
        if (score.total() >= cfg.hybrid().scoreGate() && rng.nextFloat() < 0.10f) {
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, LONG_DURATION, 0));
        }
    }
}
