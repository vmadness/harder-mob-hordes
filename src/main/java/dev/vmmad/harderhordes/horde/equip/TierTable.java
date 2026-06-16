package dev.vmmad.harderhordes.horde.equip;

import dev.vmmad.harderhordes.config.HordeConfig;

/** Maps a progression score to the maximum gear tier a horde may roll. */
public final class TierTable {

    private TierTable() {
    }

    public static GearTier maxMelee(double score, HordeConfig cfg) {
        if (score >= cfg.tiers().netheriteScoreThreshold()) {
            return GearTier.NETHERITE;
        }
        if (score >= cfg.tiers().diamondScoreThreshold()) {
            return GearTier.DIAMOND;
        }
        return GearTier.IRON;
    }

    /**
     * Armor caps one notch below weapons at the top: netherite armor is reserved
     * for elite hordes (via {@code allowNetherite}) so ordinary late-game hordes
     * stay beatable rather than becoming damage sponges.
     */
    public static GearTier maxArmor(double score, HordeConfig cfg, boolean allowNetherite) {
        if (allowNetherite && score >= cfg.tiers().netheriteScoreThreshold()) {
            return GearTier.NETHERITE;
        }
        if (score >= cfg.tiers().diamondScoreThreshold()) {
            return GearTier.DIAMOND;
        }
        return GearTier.IRON;
    }
}
