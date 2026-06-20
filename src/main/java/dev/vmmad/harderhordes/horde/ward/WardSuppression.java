package dev.vmmad.harderhordes.horde.ward;

import dev.vmmad.harderhordes.config.HordeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Bell totems: a bell sitting on a 3x3 platform of metal blocks marks a safe zone
 * where hordes won't appear. The platform's material decides the radius
 * (iron &lt; gold &lt; diamond &lt; netherite); a mixed platform counts as its weakest
 * block. No nether star, no sky needed, so it protects underground bases too.
 *
 * <p>Detection piggybacks on vanilla {@link BellBlockEntity} tracking: we only look
 * at the block entities of already-loaded chunks near the player, never a volume
 * scan, and only after the (rare) spawn roll has already passed.
 */
public final class WardSuppression {

    private WardSuppression() {
    }

    /** True if {@code pos} sits inside the safe zone of any valid bell totem nearby. */
    public static boolean isProtected(ServerLevel level, BlockPos pos, HordeConfig cfg) {
        HordeConfig.Ward ward = cfg.ward();
        int maxRadius = ward.maxRadius();
        if (!ward.enabled() || maxRadius <= 0) {
            return false;
        }
        int chunkR = (maxRadius >> 4) + 1;
        int centerX = pos.getX() >> 4;
        int centerZ = pos.getZ() >> 4;
        for (int dx = -chunkR; dx <= chunkR; dx++) {
            for (int dz = -chunkR; dz <= chunkR; dz++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(centerX + dx, centerZ + dz);
                if (chunk == null) {
                    continue; // not loaded; nothing to protect from out here
                }
                for (var entry : chunk.getBlockEntities().entrySet()) {
                    if (!(entry.getValue() instanceof BellBlockEntity)) {
                        continue;
                    }
                    BlockPos bell = entry.getKey();
                    int radius = totemRadius(level, bell, ward);
                    if (radius > 0 && bell.distSqr(pos) <= (double) radius * radius) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Radius granted by the totem under a bell, or 0 if the 3x3 platform below isn't all metal. */
    private static int totemRadius(ServerLevel level, BlockPos bell, HordeConfig.Ward ward) {
        BlockPos base = bell.below();
        int tier = Integer.MAX_VALUE;
        boolean hasNetherite = false;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                cursor.set(base.getX() + dx, base.getY(), base.getZ() + dz);
                int t = materialTier(level.getBlockState(cursor));
                if (t == 0) {
                    return 0; // incomplete platform: not a totem
                }
                tier = Math.min(tier, t);
                hasNetherite |= t == 4;
            }
        }
        // The netherite tier doesn't need an all-netherite platform: a single netherite
        // block on an otherwise diamond (or better) platform is enough.
        if (tier == 3 && hasNetherite) {
            tier = 4;
        }
        return switch (tier) {
            case 1 -> ward.ironRadius();
            case 2 -> ward.goldRadius();
            case 3 -> ward.diamondRadius();
            case 4 -> ward.netheriteRadius();
            default -> 0;
        };
    }

    /** 1=iron, 2=gold, 3=diamond, 4=netherite; 0 for anything that isn't a ward metal block. */
    private static int materialTier(BlockState state) {
        if (state.is(Blocks.IRON_BLOCK)) {
            return 1;
        }
        if (state.is(Blocks.GOLD_BLOCK)) {
            return 2;
        }
        if (state.is(Blocks.DIAMOND_BLOCK)) {
            return 3;
        }
        if (state.is(Blocks.NETHERITE_BLOCK)) {
            return 4;
        }
        return 0;
    }
}
