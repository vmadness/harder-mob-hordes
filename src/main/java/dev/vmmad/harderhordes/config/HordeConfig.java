package dev.vmmad.harderhordes.config;

import java.util.List;

/**
 * Immutable snapshot of all tunables, grouped by concern. The loader builds one
 * of these from its native config on load/reload and publishes it to
 * {@link HordeConfigHolder}; the loader-agnostic core only ever reads this
 * record (primitives, so no boxing on the spawn-check hot path).
 */
public record HordeConfig(
        Spawn spawn,
        Sizing sizing,
        Equip equip,
        Scaling scaling,
        Reward reward,
        Difficulty difficulty,
        Hybrid hybrid,
        Creeper creeper,
        Types types,
        Tiers tiers) {

    public record Spawn(
            int checkIntervalTicks,
            double dayBaseChance,
            double nightMultiplier,
            int minSecondsBetweenHordes,
            int minRadius,
            int maxRadius,
            List<String> dimensions,
            int minWorldDay,
            int fullFrequencyDay) {}

    public record Sizing(int baseSize, double sizePerScore, int minSize, int maxSize) {}

    public record Equip(
            double equippedFraction,
            double equippedFractionPerScore,
            int maxEquippedPerHorde,
            double armorPieceChance,
            double armorPieceChancePerScore,
            boolean allowModdedGear,
            double moddedGearChance) {}

    /** Per-score stat boosts applied as attribute modifiers, each capped by its max. */
    public record Scaling(
            double healthPerScore,
            double maxHealthBonus,
            double damagePerScore,
            double maxDamageBonus) {}

    public record Reward(double hordeChance, double dropChance, boolean preferBestEquipped) {}

    public record Difficulty(
            double weightDays,
            double weightLocalDifficulty,
            double weightGear,
            double dayHalfLife,
            double scoreScale) {}

    public record Hybrid(double scoreGate, double chance) {}

    public record Creeper(double chargedChance, int maxCharged, double effectChance) {}

    public record Types(
            boolean zombie,
            boolean skeleton,
            boolean creeper,
            boolean aquatic,
            boolean elite,
            boolean nether,
            boolean end,
            double skeletonEnchantedBowChance,
            double skeletonSwordLeaderChance,
            double eliteScoreGate,
            double eliteBaseChance,
            double elitePerScore,
            double eliteMaxChance) {}

    public record Tiers(double diamondScoreThreshold, double netheriteScoreThreshold) {}

    /** Sensible defaults, also used before any config has loaded. */
    public static HordeConfig defaults() {
        return new HordeConfig(
                new Spawn(200, 0.001, 10.0, 600, 24, 48,
                        List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"), 4, 12),
                new Sizing(4, 1.5, 3, 24),
                new Equip(0.25, 0.02, 4, 0.5, 0.03, true, 0.15),
                new Scaling(0.5, 20.0, 0.15, 4.0),
                new Reward(0.15, 1.0, true),
                new Difficulty(0.4, 0.35, 0.25, 12.0, 10.0),
                new Hybrid(5.0, 0.35),
                new Creeper(0.1, 2, 0.3),
                new Types(true, true, true, true, true, true, true, 0.4, 0.5, 8.0, 0.02, 0.01, 0.2),
                new Tiers(5.0, 8.0));
    }
}
