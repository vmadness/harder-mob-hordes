package dev.vmmad.harderhordes.horde.effect;

import dev.vmmad.harderhordes.HarderHordes;
import dev.vmmad.harderhordes.config.HordeConfig;
import dev.vmmad.harderhordes.horde.difficulty.ProgressionScore;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Creeper;

/**
 * The expanded creeper-horde feature pool. Beyond plain Speed, creepers can roll
 * a buff from a small pool and, at high scores, a capped chance to spawn charged
 * — but never enough to turn a horde into a mass detonation.
 */
public final class CreeperFeatures {

    private static final int LONG_DURATION = 20 * 60 * 30;

    /** Weighted-ish effect pool; Glowing also telegraphs the threat to the player. */
    private static final Holder<MobEffect>[] EFFECT_POOL = pool(
            MobEffects.MOVEMENT_SPEED,
            MobEffects.REGENERATION,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.GLOWING,
            MobEffects.INVISIBILITY);

    private CreeperFeatures() {
    }

    public static void apply(Creeper creeper, ProgressionScore score, HordeConfig cfg,
                             RandomSource rng, int[] chargedBudget) {
        // Charged creeper: score-gated, count-capped.
        if (chargedBudget[0] > 0 && rng.nextDouble() < cfg.creeper().chargedChance()) {
            if (setCharged(creeper)) {
                chargedBudget[0]--;
            }
        }

        // One effect from the pool, chance scaled up a little by score.
        double effectChance = cfg.creeper().effectChance() + Math.min(0.3, score.total() * 0.02);
        if (rng.nextDouble() < effectChance) {
            Holder<MobEffect> effect = EFFECT_POOL[rng.nextInt(EFFECT_POOL.length)];
            int amplifier = effect == MobEffects.MOVEMENT_SPEED && score.total() >= cfg.tiers().diamondScoreThreshold() ? 1 : 0;
            creeper.addEffect(new MobEffectInstance(effect, LONG_DURATION, amplifier));
        }
    }

    /**
     * Sets a creeper charged using only public API: round-trip its NBT with the
     * {@code powered} flag added. Must run after the creeper is positioned.
     * Returns false (and leaves it uncharged) if anything goes wrong.
     */
    private static boolean setCharged(Creeper creeper) {
        try {
            CompoundTag tag = creeper.saveWithoutId(new CompoundTag());
            tag.putBoolean("powered", true);
            creeper.load(tag);
            return true;
        } catch (Exception e) {
            HarderHordes.LOGGER.debug("Could not charge creeper", e);
            return false;
        }
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static Holder<MobEffect>[] pool(Holder<MobEffect>... effects) {
        return effects;
    }
}
