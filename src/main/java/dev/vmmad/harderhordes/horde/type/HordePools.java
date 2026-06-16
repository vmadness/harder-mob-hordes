package dev.vmmad.harderhordes.horde.type;

import dev.vmmad.harderhordes.HarderHordes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * Entity-type tags that back each horde's mob pool. These are the primary
 * extension point (MOD_CODING_CONVENTION.md §4): any mod or datapack can add
 * entities to e.g. {@code #harder_hordes:horde_mobs/aquatic} and they show up in
 * hordes with zero code changes. Built-in defaults ship as datapack tags.
 */
public final class HordePools {

    public static final TagKey<EntityType<?>> ZOMBIE = pool("zombie");
    public static final TagKey<EntityType<?>> SKELETON = pool("skeleton");
    public static final TagKey<EntityType<?>> CREEPER = pool("creeper");
    public static final TagKey<EntityType<?>> AQUATIC = pool("aquatic");
    public static final TagKey<EntityType<?>> ELITE = pool("elite");

    private HordePools() {
    }

    public static TagKey<EntityType<?>> pool(String name) {
        return TagKey.create(Registries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(HarderHordes.MOD_ID, "horde_mobs/" + name));
    }
}
