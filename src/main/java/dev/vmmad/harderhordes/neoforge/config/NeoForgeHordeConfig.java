package dev.vmmad.harderhordes.neoforge.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private static final ModConfigSpec.ConfigValue<List<? extends String>> DIMENSIONS;
    private static final ModConfigSpec.IntValue MIN_WORLD_DAY;
    private static final ModConfigSpec.IntValue FULL_FREQUENCY_DAY;

    // size
    private static final ModConfigSpec.IntValue BASE_SIZE;
    private static final ModConfigSpec.DoubleValue SIZE_PER_SCORE;
    private static final ModConfigSpec.IntValue MIN_SIZE;
    private static final ModConfigSpec.IntValue MAX_SIZE;

    // equip
    private static final ModConfigSpec.DoubleValue EQUIPPED_FRACTION;
    private static final ModConfigSpec.DoubleValue EQUIPPED_FRACTION_PER_SCORE;
    private static final ModConfigSpec.IntValue MAX_EQUIPPED;
    private static final ModConfigSpec.DoubleValue ARMOR_PIECE_CHANCE;
    private static final ModConfigSpec.DoubleValue ARMOR_PIECE_CHANCE_PER_SCORE;
    private static final ModConfigSpec.BooleanValue ALLOW_MODDED_GEAR;
    private static final ModConfigSpec.DoubleValue MODDED_GEAR_CHANCE;

    // scaling
    private static final ModConfigSpec.DoubleValue HEALTH_PER_SCORE;
    private static final ModConfigSpec.DoubleValue MAX_HEALTH_BONUS;
    private static final ModConfigSpec.DoubleValue DAMAGE_PER_SCORE;
    private static final ModConfigSpec.DoubleValue MAX_DAMAGE_BONUS;

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
    private static final ModConfigSpec.BooleanValue TYPE_NETHER;
    private static final ModConfigSpec.BooleanValue TYPE_END;
    private static final ModConfigSpec.DoubleValue SKELETON_ENCHANTED_BOW_CHANCE;
    private static final ModConfigSpec.DoubleValue SKELETON_SWORD_LEADER_CHANCE;
    private static final ModConfigSpec.DoubleValue ELITE_SCORE_GATE;
    private static final ModConfigSpec.DoubleValue ELITE_BASE_CHANCE;
    private static final ModConfigSpec.DoubleValue ELITE_PER_SCORE;
    private static final ModConfigSpec.DoubleValue ELITE_MAX_CHANCE;

    // tiers
    private static final ModConfigSpec.DoubleValue DIAMOND_THRESHOLD;
    private static final ModConfigSpec.DoubleValue NETHERITE_THRESHOLD;

    // ward (bell totems)
    private static final ModConfigSpec.BooleanValue WARD_ENABLED;
    private static final ModConfigSpec.IntValue WARD_IRON_RADIUS;
    private static final ModConfigSpec.IntValue WARD_GOLD_RADIUS;
    private static final ModConfigSpec.IntValue WARD_DIAMOND_RADIUS;
    private static final ModConfigSpec.IntValue WARD_NETHERITE_RADIUS;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.comment("==========================================================",
                  " WHEN AND WHERE HORDES APPEAR",
                  " Hordes are surprise groups of mobs that show up near you,",
                  " mostly at night. This section controls how often and where.",
                  "==========================================================").push("spawn");
        CHECK_INTERVAL = b.comment("How often the mod checks whether to start a horde (20 = 1 second).",
                        "Bigger number = checks less often. Most players never need to change this.")
                .defineInRange("checkIntervalTicks", 200, 20, 12000);
        DAY_BASE_CHANCE = b.comment("How likely a horde is during the DAY on each check.",
                        "This is the main 'how often' dial: raise it for more hordes, lower it for fewer.",
                        "Nights are much more dangerous (see nightMultiplier).")
                .defineInRange("dayBaseChance", 0.001, 0.0, 1.0);
        NIGHT_MULTIPLIER = b.comment("How much more likely hordes are at night than during the day.",
                        "10 = ten times more likely once the sun goes down.")
                .defineInRange("nightMultiplier", 10.0, 1.0, 50.0);
        MIN_SECONDS = b.comment("Minimum real-time seconds of peace before the same player can get another horde.",
                        "600 = 10 minutes between hordes.")
                .defineInRange("minSecondsBetweenHordes", 600, 0, 14400);
        MIN_RADIUS = b.comment("Closest a horde can appear to you, in blocks.")
                .defineInRange("minRadius", 24, 8, 128);
        MAX_RADIUS = b.comment("Farthest a horde can appear from you, in blocks.")
                .defineInRange("maxRadius", 48, 8, 256);
        DIMENSIONS = b.comment("Worlds where hordes can appear. Each world uses its own mobs:",
                        "  Overworld -> zombies, skeletons, creepers, ...",
                        "  Nether    -> piglins, blazes, wither skeletons, ...",
                        "  End       -> endermen, ...",
                        "Delete a line to stop hordes in that world. You can write the short",
                        "names too: \"overworld\", \"nether\", \"end\".")
                .defineListAllowEmpty("dimensions",
                        List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"),
                        () -> "minecraft:overworld",
                        o -> o instanceof String);
        MIN_WORLD_DAY = b.comment("No hordes until this day of your world. Gives new worlds time to get going.")
                .defineInRange("minWorldDay", 4, 0, 100000);
        FULL_FREQUENCY_DAY = b.comment("Hordes start rare and slowly get more common, reaching full frequency on this day.",
                        "Lower = ramps up faster.")
                .defineInRange("fullFrequencyDay", 12, 0, 100000);
        b.pop();

        b.comment("==========================================================",
                  " HOW MANY MOBS ARE IN A HORDE",
                  " Hordes get bigger as your danger level grows (see [difficulty]).",
                  "==========================================================").push("size");
        BASE_SIZE = b.comment("Starting horde size for a fresh, weak player.")
                .defineInRange("baseSize", 4, 1, 64);
        SIZE_PER_SCORE = b.comment("Extra mobs added as your danger level rises.")
                .defineInRange("sizePerScore", 1.5, 0.0, 16.0);
        MIN_SIZE = b.comment("A horde will never have fewer mobs than this.")
                .defineInRange("minSize", 3, 1, 64);
        MAX_SIZE = b.comment("A horde will never have more mobs than this, no matter how strong you get.")
                .defineInRange("maxSize", 24, 1, 128);
        b.pop();

        b.comment("==========================================================",
                  " MOB GEAR (weapons and armor)",
                  " Only some mobs carry gear so hordes stay beatable. Better",
                  " materials unlock as you get stronger (see [tiers]).",
                  "==========================================================").push("equip");
        EQUIPPED_FRACTION = b.comment("Share of the horde that carries gear early on (0.25 = a quarter of them).")
                .defineInRange("equippedFraction", 0.25, 0.0, 1.0);
        EQUIPPED_FRACTION_PER_SCORE = b.comment("More mobs carry gear as your danger level rises. Never exceeds the whole horde.")
                .defineInRange("equippedFractionPerScore", 0.02, 0.0, 1.0);
        MAX_EQUIPPED = b.comment("Hard limit on how many mobs in one horde can carry gear.")
                .defineInRange("maxEquippedPerHorde", 4, 0, 64);
        ARMOR_PIECE_CHANCE = b.comment("Chance each armor slot (helmet, chestplate, leggings, boots) is filled on a geared mob.")
                .defineInRange("armorPieceChance", 0.5, 0.0, 1.0);
        ARMOR_PIECE_CHANCE_PER_SCORE = b.comment("Armor becomes more common as your danger level rises.")
                .defineInRange("armorPieceChancePerScore", 0.03, 0.0, 1.0);
        ALLOW_MODDED_GEAR = b.comment("Let mobs use weapons added by other mods (only items in the harder_hordes:bonus_weapons list).",
                        "Off = vanilla weapons only.")
                .define("allowModdedGear", true);
        MODDED_GEAR_CHANCE = b.comment("If modded weapons are allowed, how often a geared mob uses one instead of a vanilla weapon.")
                .defineInRange("moddedGearChance", 0.15, 0.0, 1.0);
        b.pop();

        b.comment("==========================================================",
                  " EXTRA TOUGHNESS",
                  " As your danger level rises, mobs gain bonus health and damage",
                  " on top of normal. Set the 'PerScore' values to 0 to keep",
                  " mobs at their vanilla strength.",
                  "==========================================================").push("scaling");
        HEALTH_PER_SCORE = b.comment("Bonus health added per danger-level point (2 = 1 heart).")
                .defineInRange("healthPerScore", 0.5, 0.0, 100.0);
        MAX_HEALTH_BONUS = b.comment("Most bonus health one mob can gain (20 = 10 extra hearts).")
                .defineInRange("maxHealthBonus", 20.0, 0.0, 1024.0);
        DAMAGE_PER_SCORE = b.comment("Bonus attack damage added per danger-level point.")
                .defineInRange("damagePerScore", 0.15, 0.0, 100.0);
        MAX_DAMAGE_BONUS = b.comment("Most bonus attack damage one mob can gain.")
                .defineInRange("maxDamageBonus", 4.0, 0.0, 1024.0);
        b.pop();

        b.comment("==========================================================",
                  " HORDE LOOT",
                  " Most mobs drop nothing. Now and then ONE mob in a horde is",
                  " marked to drop its gear, so clearing hordes occasionally pays off.",
                  "==========================================================").push("rewards");
        REWARD_HORDE_CHANCE = b.comment("Chance a horde has a loot-dropping mob at all (0.15 = about 1 horde in 7).")
                .defineInRange("hordeChance", 0.15, 0.0, 1.0);
        REWARD_DROP_CHANCE = b.comment("Chance that lucky mob actually drops its gear when killed (1.0 = always).")
                .defineInRange("dropChance", 1.0, 0.0, 1.0);
        REWARD_PREFER_BEST = b.comment("On = the best-geared mob is the one that drops loot. Off = a random geared mob.")
                .define("preferBestEquipped", true);
        b.pop();

        b.comment("==========================================================",
                  " YOUR DANGER LEVEL",
                  " Almost everything above scales with a hidden 'danger level',",
                  " built from the three things below. Most players can leave this",
                  " alone. Check your current value in-game with /harderhordes score.",
                  "==========================================================").push("difficulty");
        WEIGHT_DAYS = b.comment("How much 'days survived' counts toward your danger level.")
                .defineInRange("weightDays", 0.4, 0.0, 1.0);
        WEIGHT_LOCAL_DIFFICULTY = b.comment("How much the game's own area difficulty counts (rises the longer you stay put).")
                .defineInRange("weightLocalDifficulty", 0.35, 0.0, 1.0);
        WEIGHT_GEAR = b.comment("How much your equipped armor and weapons count.")
                .defineInRange("weightGear", 0.25, 0.0, 1.0);
        DAY_HALF_LIFE = b.comment("How quickly 'days survived' ramps up. Lower = danger climbs faster over time.")
                .defineInRange("dayHalfLife", 12.0, 0.1, 1000.0);
        SCORE_SCALE = b.comment("MASTER DIFFICULTY DIAL. Higher = bigger, better-armed, tougher hordes overall. Lower = gentler.")
                .defineInRange("scoreScale", 10.0, 1.0, 100.0);
        b.pop();

        b.comment("==========================================================",
                  " MIXED HORDES",
                  " At higher danger levels a horde can blend mob types",
                  " (for example zombies and skeletons together).",
                  "==========================================================").push("hybrid");
        HYBRID_SCORE_GATE = b.comment("Danger level needed before mixed hordes can happen.")
                .defineInRange("scoreGate", 5.0, 0.0, 100.0);
        HYBRID_CHANCE = b.comment("Chance a qualifying horde is a mixed one.")
                .defineInRange("chance", 0.35, 0.0, 1.0);
        b.pop();

        b.comment("==========================================================",
                  " CREEPER HORDE EXTRAS",
                  "==========================================================").push("creeper");
        CREEPER_CHARGED_CHANCE = b.comment("Chance each creeper in a creeper horde is a charged (supercharged) creeper.")
                .defineInRange("chargedChance", 0.1, 0.0, 1.0);
        CREEPER_MAX_CHARGED = b.comment("Most charged creepers allowed in one horde.")
                .defineInRange("maxCharged", 2, 0, 32);
        CREEPER_EFFECT_CHANCE = b.comment("Chance a creeper gets a special buff (like speed or fire resistance).")
                .defineInRange("effectChance", 0.3, 0.0, 1.0);
        b.pop();

        b.comment("==========================================================",
                  " TURN MOB TYPES ON OR OFF",
                  " Set any toggle to false to remove that kind of horde.",
                  "==========================================================").push("types");
        TYPE_ZOMBIE = b.comment("Zombie-type hordes (zombies, husks, zombie villagers).")
                .define("zombie", true);
        TYPE_SKELETON = b.comment("Skeleton-type hordes (skeletons, strays).")
                .define("skeleton", true);
        TYPE_CREEPER = b.comment("Creeper hordes (see the [creeper] section for extras).")
                .define("creeper", true);
        TYPE_AQUATIC = b.comment("Water hordes when you're in or over water (needs water mobs available).")
                .define("aquatic", true);
        TYPE_ELITE = b.comment("Rare, dangerous hordes (wither skeletons, vindicators, ...).")
                .define("elite", true);
        TYPE_NETHER = b.comment("Nether hordes (piglins, blazes, wither skeletons, ...). Only in the Nether.")
                .define("nether", true);
        TYPE_END = b.comment("End hordes (endermen, ...). Only in the End.")
                .define("end", true);
        SKELETON_ENCHANTED_BOW_CHANCE = b.comment("Chance a skeleton's bow is enchanted.")
                .defineInRange("skeletonEnchantedBowChance", 0.4, 0.0, 1.0);
        SKELETON_SWORD_LEADER_CHANCE = b.comment("Chance the lead skeleton carries a sword instead of a bow.")
                .defineInRange("skeletonSwordLeaderChance", 0.5, 0.0, 1.0);
        ELITE_SCORE_GATE = b.comment("Danger level needed before elite hordes can appear.")
                .defineInRange("eliteScoreGate", 8.0, 0.0, 100.0);
        ELITE_BASE_CHANCE = b.comment("Starting chance of an elite horde once it's unlocked.")
                .defineInRange("eliteBaseChance", 0.02, 0.0, 1.0);
        ELITE_PER_SCORE = b.comment("Extra elite chance as your danger level keeps rising.")
                .defineInRange("elitePerScore", 0.01, 0.0, 1.0);
        ELITE_MAX_CHANCE = b.comment("The highest the elite chance can ever reach.")
                .defineInRange("eliteMaxChance", 0.2, 0.0, 1.0);
        b.pop();

        b.comment("==========================================================",
                  " GEAR QUALITY UNLOCKS",
                  " Below these danger levels, mobs use weaker materials.",
                  " (Iron is the starting material.)",
                  "==========================================================").push("tiers");
        DIAMOND_THRESHOLD = b.comment("Danger level at which mobs can start using diamond gear.")
                .defineInRange("diamondScoreThreshold", 5.0, 0.0, 100.0);
        NETHERITE_THRESHOLD = b.comment("Danger level at which mobs can start using netherite gear.")
                .defineInRange("netheriteScoreThreshold", 8.0, 0.0, 100.0);
        b.pop();

        b.comment("==========================================================",
                  " SAFE ZONES (bell totems)",
                  " Put a BELL on top of a 3x3 platform of metal blocks to keep",
                  " hordes away from your base. The metal sets how big the safe",
                  " zone is: iron < gold < diamond < netherite. Mix metals and",
                  " the weakest one counts (so a half-built platform is only as",
                  " strong as its worst block). The top tier is the exception: a",
                  " single netherite block on an otherwise diamond platform counts",
                  " as netherite. No nether star, and it works underground (the",
                  " bell does not need to see the sky). Set a radius to 0 to",
                  " disable that tier.",
                  "==========================================================").push("ward");
        WARD_ENABLED = b.comment("Master switch for bell-totem safe zones. Off = bells do nothing special.")
                .define("enabled", true);
        WARD_IRON_RADIUS = b.comment("Safe-zone radius in blocks for an IRON-block platform (the cheapest totem).")
                .defineInRange("ironRadius", 48, 0, 256);
        WARD_GOLD_RADIUS = b.comment("Safe-zone radius in blocks for a GOLD-block platform.")
                .defineInRange("goldRadius", 64, 0, 256);
        WARD_DIAMOND_RADIUS = b.comment("Safe-zone radius in blocks for a DIAMOND-block platform.")
                .defineInRange("diamondRadius", 80, 0, 256);
        WARD_NETHERITE_RADIUS = b.comment("Safe-zone radius in blocks for a NETHERITE-block platform",
                        "(diamond platform with at least one netherite block). The strongest totem.")
                .defineInRange("netheriteRadius", 112, 0, 256);
        b.pop();

        SPEC = b.build();
    }

    private NeoForgeHordeConfig() {
    }

    /** Packs the current config values into an immutable snapshot for the core. */
    public static HordeConfig snapshot() {
        return new HordeConfig(
                new HordeConfig.Spawn(CHECK_INTERVAL.get(), DAY_BASE_CHANCE.get(), NIGHT_MULTIPLIER.get(),
                        MIN_SECONDS.get(), MIN_RADIUS.get(), MAX_RADIUS.get(), normalizeDimensions(DIMENSIONS.get()),
                        MIN_WORLD_DAY.get(), FULL_FREQUENCY_DAY.get()),
                new HordeConfig.Sizing(BASE_SIZE.get(), SIZE_PER_SCORE.get(), MIN_SIZE.get(), MAX_SIZE.get()),
                new HordeConfig.Equip(EQUIPPED_FRACTION.get(), EQUIPPED_FRACTION_PER_SCORE.get(), MAX_EQUIPPED.get(),
                        ARMOR_PIECE_CHANCE.get(), ARMOR_PIECE_CHANCE_PER_SCORE.get(),
                        ALLOW_MODDED_GEAR.get(), MODDED_GEAR_CHANCE.get()),
                new HordeConfig.Scaling(HEALTH_PER_SCORE.get(), MAX_HEALTH_BONUS.get(),
                        DAMAGE_PER_SCORE.get(), MAX_DAMAGE_BONUS.get()),
                new HordeConfig.Reward(REWARD_HORDE_CHANCE.get(), REWARD_DROP_CHANCE.get(), REWARD_PREFER_BEST.get()),
                new HordeConfig.Difficulty(WEIGHT_DAYS.get(), WEIGHT_LOCAL_DIFFICULTY.get(), WEIGHT_GEAR.get(),
                        DAY_HALF_LIFE.get(), SCORE_SCALE.get()),
                new HordeConfig.Hybrid(HYBRID_SCORE_GATE.get(), HYBRID_CHANCE.get()),
                new HordeConfig.Creeper(CREEPER_CHARGED_CHANCE.get(), CREEPER_MAX_CHARGED.get(), CREEPER_EFFECT_CHANCE.get()),
                new HordeConfig.Types(TYPE_ZOMBIE.get(), TYPE_SKELETON.get(), TYPE_CREEPER.get(), TYPE_AQUATIC.get(),
                        TYPE_ELITE.get(), TYPE_NETHER.get(), TYPE_END.get(),
                        SKELETON_ENCHANTED_BOW_CHANCE.get(), SKELETON_SWORD_LEADER_CHANCE.get(),
                        ELITE_SCORE_GATE.get(), ELITE_BASE_CHANCE.get(), ELITE_PER_SCORE.get(), ELITE_MAX_CHANCE.get()),
                new HordeConfig.Tiers(DIAMOND_THRESHOLD.get(), NETHERITE_THRESHOLD.get()),
                new HordeConfig.Ward(WARD_ENABLED.get(), WARD_IRON_RADIUS.get(), WARD_GOLD_RADIUS.get(),
                        WARD_DIAMOND_RADIUS.get(), WARD_NETHERITE_RADIUS.get()));
    }

    /** Resolves configured dimension entries (short names allowed) to full {@code namespace:path} ids. */
    private static List<String> normalizeDimensions(List<? extends String> raw) {
        List<String> out = new ArrayList<>(raw.size());
        for (String entry : raw) {
            if (entry == null || entry.isBlank()) {
                continue;
            }
            String d = entry.trim().toLowerCase(Locale.ROOT);
            if (!d.contains(":")) {
                d = switch (d) {
                    case "nether", "the_nether" -> "minecraft:the_nether";
                    case "end", "the_end" -> "minecraft:the_end";
                    case "overworld" -> "minecraft:overworld";
                    default -> "minecraft:" + d;
                };
            }
            out.add(d);
        }
        return List.copyOf(out);
    }
}
