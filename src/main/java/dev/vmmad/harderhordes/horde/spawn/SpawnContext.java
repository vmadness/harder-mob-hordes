package dev.vmmad.harderhordes.horde.spawn;

import dev.vmmad.harderhordes.horde.type.HordeDimension;
import net.minecraft.core.BlockPos;

/**
 * The resolved spawn location and its environment. {@code aquatic} drives the
 * land-vs-water selection (water → aquatic hordes preferred); {@code dimension}
 * gates which mob pools are eligible so each dimension fields its own creatures.
 *
 * @param pos       ground/water position the horde forms around
 * @param aquatic   whether the position is in or over water
 * @param dimension the dimension family the position is in
 */
public record SpawnContext(BlockPos pos, boolean aquatic, HordeDimension dimension) {
}
