package dev.vmmad.harderhordes.horde.type;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * An open, data-light description of one kind of horde. Definitions are
 * registered in {@link HordeTypeRegistry} (built-ins at init, addons via the
 * same call) and are intentionally not a fixed enum, so the set is extensible.
 *
 * @param id          unique id, also the config-toggle key (path segment)
 * @param mobPool     entity-type tag the members are drawn from
 * @param dimension   which dimension family this horde may spawn in
 * @param environment where (land/water) this horde may spawn
 * @param baseWeight  selection weight relative to other matching definitions
 * @param elite       whether this is a rare elite horde (gated by score + chance)
 */
public record HordeDefinition(
        ResourceLocation id,
        TagKey<EntityType<?>> mobPool,
        HordeDimension dimension,
        Environment environment,
        double baseWeight,
        boolean elite) {
}
