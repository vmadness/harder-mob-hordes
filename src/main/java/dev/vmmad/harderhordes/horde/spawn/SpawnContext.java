package dev.vmmad.harderhordes.horde.spawn;

import net.minecraft.core.BlockPos;

/**
 * The resolved spawn location and its environment. {@code aquatic} drives the
 * environment-aware selection: when the area around the player is water, aquatic
 * hordes are preferred (and land mobs avoided).
 *
 * @param pos     ground/water position the horde forms around
 * @param aquatic whether the position is in or over water
 */
public record SpawnContext(BlockPos pos, boolean aquatic) {
}
