package dev.vmmad.harderhordes.horde.spawn;

import javax.annotation.Nullable;

import dev.vmmad.harderhordes.config.HordeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;

/**
 * Finds a valid place to spawn a horde near a player: a few rings out (so it's
 * "near" but not on top of them), on standable ground or in water. We roll our
 * own checks (heightmap + collision/fluid) rather than depend on the private
 * vanilla nearby-spawn helper, which isn't reliably public.
 */
public final class SpawnPositionFinder {

    private static final int ATTEMPTS = 24;

    private SpawnPositionFinder() {
    }

    @Nullable
    public static SpawnContext find(ServerLevel level, BlockPos around, HordeConfig cfg, RandomSource rng) {
        int min = cfg.spawn().minRadius();
        int max = Math.max(min + 1, cfg.spawn().maxRadius());

        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            double angle = rng.nextDouble() * Math.PI * 2.0;
            int dist = min + rng.nextInt(max - min + 1);
            int x = around.getX() + (int) Math.round(Math.cos(angle) * dist);
            int z = around.getZ() + (int) Math.round(Math.sin(angle) * dist);

            // Only consider already-loaded columns so we never force chunk generation on the tick thread.
            if (!level.isLoaded(new BlockPos(x, around.getY(), z))) {
                continue;
            }

            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);

            FluidState feet = level.getFluidState(pos);
            FluidState below = level.getFluidState(pos.below());
            if (!feet.isEmpty() || !below.isEmpty()) {
                return new SpawnContext(pos, true);
            }

            if (isStandable(level, pos)) {
                return new SpawnContext(pos, false);
            }
        }
        return null;
    }

    private static boolean isStandable(ServerLevel level, BlockPos pos) {
        BlockState ground = level.getBlockState(pos.below());
        if (!ground.isFaceSturdy(level, pos.below(), Direction.UP)) {
            return false;
        }
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
    }
}
