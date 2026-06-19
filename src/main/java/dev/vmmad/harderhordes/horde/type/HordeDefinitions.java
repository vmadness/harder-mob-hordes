package dev.vmmad.harderhordes.horde.type;

import dev.vmmad.harderhordes.HarderHordes;
import dev.vmmad.harderhordes.config.HordeConfig;
import net.minecraft.resources.ResourceLocation;

/**
 * Built-in horde definitions and the config-toggle mapping. Registered once at
 * mod init; the registry stays open for addons to extend.
 */
public final class HordeDefinitions {

    public static final ResourceLocation ZOMBIE_ID = id("zombie");
    public static final ResourceLocation SKELETON_ID = id("skeleton");
    public static final ResourceLocation CREEPER_ID = id("creeper");
    public static final ResourceLocation AQUATIC_ID = id("aquatic");
    public static final ResourceLocation ELITE_ID = id("elite");
    public static final ResourceLocation NETHER_ID = id("nether");
    public static final ResourceLocation END_ID = id("end");

    private HordeDefinitions() {
    }

    public static void bootstrap() {
        HordeTypeRegistry.register(new HordeDefinition(ZOMBIE_ID, HordePools.ZOMBIE, HordeDimension.OVERWORLD, Environment.LAND, 1.0, false));
        HordeTypeRegistry.register(new HordeDefinition(SKELETON_ID, HordePools.SKELETON, HordeDimension.OVERWORLD, Environment.LAND, 1.0, false));
        HordeTypeRegistry.register(new HordeDefinition(CREEPER_ID, HordePools.CREEPER, HordeDimension.OVERWORLD, Environment.LAND, 0.7, false));
        HordeTypeRegistry.register(new HordeDefinition(AQUATIC_ID, HordePools.AQUATIC, HordeDimension.OVERWORLD, Environment.WATER, 1.0, false));
        HordeTypeRegistry.register(new HordeDefinition(ELITE_ID, HordePools.ELITE, HordeDimension.OVERWORLD, Environment.ANY, 1.0, true));
        // Nether/End hordes are drawn from their own pools (Environment.ANY so they ignore the lava/void land-vs-water split).
        HordeTypeRegistry.register(new HordeDefinition(NETHER_ID, HordePools.NETHER, HordeDimension.NETHER, Environment.ANY, 1.0, false));
        HordeTypeRegistry.register(new HordeDefinition(END_ID, HordePools.END, HordeDimension.END, Environment.ANY, 1.0, false));
    }

    /** Maps a built-in definition to its config enable toggle; unknown ids default to enabled. */
    public static boolean isEnabled(HordeDefinition def, HordeConfig cfg) {
        HordeConfig.Types t = cfg.types();
        String path = def.id().getPath();
        return switch (path) {
            case "zombie" -> t.zombie();
            case "skeleton" -> t.skeleton();
            case "creeper" -> t.creeper();
            case "aquatic" -> t.aquatic();
            case "elite" -> t.elite();
            case "nether" -> t.nether();
            case "end" -> t.end();
            default -> true;
        };
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(HarderHordes.MOD_ID, path);
    }
}
