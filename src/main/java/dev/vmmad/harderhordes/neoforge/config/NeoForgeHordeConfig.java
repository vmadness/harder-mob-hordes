package dev.vmmad.harderhordes.neoforge.config;

import dev.vmmad.harderhordes.config.HordeConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * NeoForge-native ({@link ModConfigSpec}) definition of every tunable, plus a
 * {@link #snapshot()} that packs the current values into the loader-agnostic
 * {@link HordeConfig} record. Registered as a COMMON config so it lives once in
 * the global {@code config/harder_hordes-common.toml}, not per-world.
 */
public final class NeoForgeHordeConfig {

    public static final ModConfigSpec SPEC;

    // spawn
    private static final ModConfigSpec.IntValue CHECK_INTERVAL;
    private static final ModConfigSpec.DoubleValue DAY_BASE_CHANCE;
    private static final ModConfigSpec.DoubleValue NIGHT_MULTIPLIER;
    private static final ModConfigSpec.IntValue MIN_SECONDS;
    private static final ModConfigSpec.IntValue MIN_RADIUS;
    private static final ModConfigSpec.IntValue MAX_RADIUS;
    private static final ModConfigSpec.BooleanValue OVERWORLD_ONLY;
    private static final ModConfigSpec.IntValue MIN_WORLD_DAY;
    private static final ModConfigSpec.IntValue FULL_FREQUENCY_DAY;

    // size
    private static final ModConfigSpec.IntValue BASE_SIZE;
    private static final ModConfigSpec.DoubleValue SIZE_PER_SCORE;
    private static final ModConfigSpec.IntValue MIN_SIZE;
    private static final ModConfigSpec.IntValue MAX_SIZE;

    // equip
    private static final ModConfigSpec.DoubleValue EQUIPPED_FRACTION;
    private static final ModConfigSpec.IntValue MAX_EQUIPPED;
    private static final ModConfigSpec.DoubleValue ARMOR_PIECE_CHANCE;
    private static final ModConfigSpec.BooleanValue ALLOW_MODDED_GEAR;
    private static final ModConfigSpec.DoubleValue MODDED_GEAR_CHANCE;

    // rewards
    private static final ModConfigSpec.DoubleValue REWARD_HORDE_CHANCE;
    private static final ModConfigSpec.DoubleValue REWARD_DROP_CHANCE;
    private static final ModConfigSpec.BooleanValue REWARD_PREFER_BEST;

    // difficulty
    private static final ModConfigSpec.DoubleValue WEIGHT_DAYS;
    private static final ModConfigSpec.DoubleValue WEIGHT_LOCAL_DIFFICULTY;
    private static final ModConfigSpec.DoubleValue WEIGHT_GEAR;
    private static final ModConfigSpec.DoubleValue DAY_HALF_LIFE;
    private static final ModConfigSpec.DoubleValue SCORE_SCALE;

    // hybrid
    private static final ModConfigSpec.DoubleValue HYBRID_SCORE_GATE;
    private static final ModConfigSpec.DoubleValue HYBRID_CHANCE;

    // creeper
    private static final ModConfigSpec.DoubleValue CREEPER_CHARGED_CHANCE;
    private static final ModConfigSpec.IntValue CREEPER_MAX_CHARGED;
    private static final ModConfigSpec.DoubleValue CREEPER_EFFECT_CHANCE;

    // types
    private static final ModConfigSpec.BooleanValue TYPE_ZOMBIE;
    private static final ModConfigSpec.BooleanValue TYPE_SKELETON;
    private static final ModConfigSpec.BooleanValue TYPE_CREEPER;
    private static final ModConfigSpec.BooleanValue TYPE_AQUATIC;
    private static final ModConfigSpec.BooleanValue TYPE_ELITE;
    private static final ModConfigSpec.DoubleValue SKELETON_ENCHANTED_BOW_CHANCE;
    private static final ModConfigSpec.DoubleValue SKELETON_SWORD_LEADER_CHANCE;
    private static final ModConfigSpec.DoubleValue ELITE_SCORE_GATE;
    private static final ModConfigSpec.DoubleValue ELITE_BASE_CHANCE;
    private static final ModConfigSpec.DoubleValue ELITE_PER_SCORE;
    private static final ModConfigSpec.DoubleValue ELITE_MAX_CHANCE;

    // tiers
    private static final ModConfigSpec.DoubleValue DIAMOND_THRESHOLD;
    private static final ModConfigSpec.DoubleValue NETHERITE_THRESHOLD;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.comment("How often hordes spawn and where").push("spawn");
        CHECK_INTERVAL = b.comment("Server ticks between horde checks (20 = 1s). Higher = cheaper, rarer checks.")
                .defineInRange("checkIntervalTicks", 200, 20, 12000);
        DAY_BASE_CHANCE = b.comment("Chance per check, in daytime, that a player triggers a horde. Kept very low so hordes are a rare event.")
                .defineInRange("dayBaseChance", 0.001, 0.0, 1.0);
        NIGHT_MULTIPLIER = b.comment("Night multiplies the base chance by this (hordes are a night threat).")
                .defineInRange("nightMultiplier", 10.0, 1.0, 50.0);
        MIN_SECONDS = b.comment("Minimum real seconds between hordes for the same player.")
                .defineInRange("minSecondsBetweenHordes", 600, 0, 14400);
        MIN_RADIUS = b.comment("Minimum spawn distance from the player (blocks).")
                .defineInRange("minRadius", 24, 8, 128);
        MAX_RADIUS = b.comment("Maximum spawn distance from the player (blocks).")
                .defineInRange("maxRadius", 48, 8, 256);
        OVERWORLD_ONLY = b.comment("Only spawn hordes in the Overworld.")
                .define("overworldOnly", true);
        MIN_WORLD_DAY = b.comment("No hordes before this in-game day (protects early game).")
                .defineInRange("minWorldDay", 4, 0, 100000);
        FULL_FREQUENCY_DAY = b.comment("Spawn chance ramps from 0 at minWorldDay up to full by this day (rarer in early/mid game).")
                .defineInRange("fullFrequencyDay", 12, 0, 100000);
        b.pop();

        b.comment("How big hordes get").push("size");
        BASE_SIZE = b.defineInRange("baseSize", 4, 1, 64);
        SIZE_PER_SCORE = b.comment("Extra mobs per point of progression score.")
                .defineInRange("sizePerScore", 1.5, 0.0, 16.0);
        MIN_SIZE = b.defineInRange("minSize", 3, 1, 64);
        MAX_SIZE = b.defineInRange("maxSize", 24, 1, 128);
        b.pop();

        b.comment("How many mobs are armed and with what").push("equip");
        EQUIPPED_FRACTION = b.comment("Fraction of the horde that carries gear. Keep low so it stays beatable.")
                .defineInRange("equippedFraction", 0.25, 0.0, 1.0);
        MAX_EQUIPPED = b.comment("Hard cap on equipped mobs per horde.")
                .defineInRange("maxEquippedPerHorde", 4, 0, 64);
        ARMOR_PIECE_CHANCE = b.comment("Per-slot chance an equipped mob wears an armor piece.")
                .defineInRange("armorPieceChance", 0.5, 0.0, 1.0);
        ALLOW_MODDED_GEAR = b.comment("Allow modded weapons from #harder_hordes:bonus_weapons (flavor only).")
                .define("allowModdedGear", true);
        MODDED_GEAR_CHANCE = b.comment("Chance an armed mob uses a modded weapon instead of a vanilla one (if any are tagged).")
                .defineInRange("moddedGearChance", 0.15, 0.0, 1.0);
        b.pop();

        b.comment("Rare loot: at most ONE mob per horde drops its gear, and only some hordes give a reward at all")
                .push("rewards");
        REWARD_HORDE_CHANCE = b.comment("Chance a given horde designates a reward mob at all.")
                .defineInRange("hordeChance", 0.15, 0.0, 1.0);
        REWARD_DROP_CHANCE = b.comment("Drop chance applied to the reward mob's equipped slots (1.0 = always).")
                .defineInRange("dropChance", 1.0, 0.0, 1.0);
        REWARD_PREFER_BEST = b.comment("Pick the best-equipped mob as the reward bearer (else random).")
                .define("preferBestEquipped", true);
        b.pop();

        b.comment("The blended progression score that drives everything").push("difficulty");
        WEIGHT_DAYS = b.defineInRange("weightDays", 0.4, 0.0, 1.0);
        WEIGHT_LOCAL_DIFFICULTY = b.defineInRange("weightLocalDifficulty", 0.35, 0.0, 1.0);
        WEIGHT_GEAR = b.defineInRange("weightGear", 0.25, 0.0, 1.0);
        DAY_HALF_LIFE = b.comment("In-game days at which the day component reaches ~63% of its max.")
                .defineInRange("dayHalfLife", 12.0, 0.1, 1000.0);
        SCORE_SCALE = b.comment("Scales the normalized 0..1 blend into the working score range (~0..10).")
                .defineInRange("scoreScale", 10.0, 1.0, 100.0);
        b.pop();

        b.comment("Mixed-type hordes at higher difficulty").push("hybrid");
        HYBRID_SCORE_GATE = b.defineInRange("scoreGate", 5.0, 0.0, 100.0);
        HYBRID_CHANCE = b.defineInRange("chance", 0.35, 0.0, 1.0);
        b.pop();

        b.comment("Creeper-horde extras").push("creeper");
        CREEPER_CHARGED_CHANCE = b.comment("Per-creeper chance to spawn charged (capped below).")
                .defineInRange("chargedChance", 0.1, 0.0, 1.0);
        CREEPER_MAX_CHARGED = b.comment("Max charged creepers per horde.")
                .defineInRange("maxCharged", 2, 0, 32);
        CREEPER_EFFECT_CHANCE = b.comment("Base chance a creeper gets a buff from the effect pool.")
                .defineInRange("effectChance", 0.3, 0.0, 1.0);
        b.pop();

        b.comment("Per-type toggles and tuning").push("types");
        TYPE_ZOMBIE = b.define("zombie", true);
        TYPE_SKELETON = b.define("skeleton", true);
        TYPE_CREEPER = b.define("creeper", true);
        TYPE_AQUATIC = b.comment("Aquatic hordes when the player is in/over water (needs mobs tagged in horde_mobs/aquatic).")
                .define("aquatic", true);
        TYPE_ELITE = b.comment("Rare elite hordes (wither skeletons, vindicators, ...).")
                .define("elite", true);
        SKELETON_ENCHANTED_BOW_CHANCE = b.defineInRange("skeletonEnchantedBowChance", 0.4, 0.0, 1.0);
        SKELETON_SWORD_LEADER_CHANCE = b.comment("Chance the front skeleton wields a sword instead of a bow.")
                .defineInRange("skeletonSwordLeaderChance", 0.5, 0.0, 1.0);
        ELITE_SCORE_GATE = b.defineInRange("eliteScoreGate", 8.0, 0.0, 100.0);
        ELITE_BASE_CHANCE = b.defineInRange("eliteBaseChance", 0.02, 0.0, 1.0);
        ELITE_PER_SCORE = b.defineInRange("elitePerScore", 0.01, 0.0, 1.0);
        ELITE_MAX_CHANCE = b.defineInRange("eliteMaxChance", 0.2, 0.0, 1.0);
        b.pop();

        b.comment("Score thresholds at which higher gear tiers unlock").push("tiers");
        DIAMOND_THRESHOLD = b.defineInRange("diamondScoreThreshold", 5.0, 0.0, 100.0);
        NETHERITE_THRESHOLD = b.defineInRange("netheriteScoreThreshold", 8.0, 0.0, 100.0);
        b.pop();

        SPEC = b.build();
    }

    private NeoForgeHordeConfig() {
    }

    /** Packs the current config values into an immutable snapshot for the core. */
    public static HordeConfig snapshot() {
        return new HordeConfig(
                new HordeConfig.Spawn(CHECK_INTERVAL.get(), DAY_BASE_CHANCE.get(), NIGHT_MULTIPLIER.get(),
                        MIN_SECONDS.get(), MIN_RADIUS.get(), MAX_RADIUS.get(), OVERWORLD_ONLY.get(),
                        MIN_WORLD_DAY.get(), FULL_FREQUENCY_DAY.get()),
                new HordeConfig.Sizing(BASE_SIZE.get(), SIZE_PER_SCORE.get(), MIN_SIZE.get(), MAX_SIZE.get()),
                new HordeConfig.Equip(EQUIPPED_FRACTION.get(), MAX_EQUIPPED.get(), ARMOR_PIECE_CHANCE.get(),
                        ALLOW_MODDED_GEAR.get(), MODDED_GEAR_CHANCE.get()),
                new HordeConfig.Reward(REWARD_HORDE_CHANCE.get(), REWARD_DROP_CHANCE.get(), REWARD_PREFER_BEST.get()),
                new HordeConfig.Difficulty(WEIGHT_DAYS.get(), WEIGHT_LOCAL_DIFFICULTY.get(), WEIGHT_GEAR.get(),
                        DAY_HALF_LIFE.get(), SCORE_SCALE.get()),
                new HordeConfig.Hybrid(HYBRID_SCORE_GATE.get(), HYBRID_CHANCE.get()),
                new HordeConfig.Creeper(CREEPER_CHARGED_CHANCE.get(), CREEPER_MAX_CHARGED.get(), CREEPER_EFFECT_CHANCE.get()),
                new HordeConfig.Types(TYPE_ZOMBIE.get(), TYPE_SKELETON.get(), TYPE_CREEPER.get(), TYPE_AQUATIC.get(),
                        TYPE_ELITE.get(), SKELETON_ENCHANTED_BOW_CHANCE.get(), SKELETON_SWORD_LEADER_CHANCE.get(),
                        ELITE_SCORE_GATE.get(), ELITE_BASE_CHANCE.get(), ELITE_PER_SCORE.get(), ELITE_MAX_CHANCE.get()),
                new HordeConfig.Tiers(DIAMOND_THRESHOLD.get(), NETHERITE_THRESHOLD.get()));
    }
}
