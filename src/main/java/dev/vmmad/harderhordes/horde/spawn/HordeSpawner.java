package dev.vmmad.harderhordes.horde.spawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.vmmad.harderhordes.HarderHordes;
import dev.vmmad.harderhordes.config.HordeConfig;
import dev.vmmad.harderhordes.horde.difficulty.ProgressionScore;
import dev.vmmad.harderhordes.horde.effect.HordeEffects;
import dev.vmmad.harderhordes.horde.equip.EquipmentApplier;
import dev.vmmad.harderhordes.horde.reward.HordeLoot;
import dev.vmmad.harderhordes.horde.type.HordeComposition;
import dev.vmmad.harderhordes.horde.type.HordeDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enemy;

/**
 * Assembles and places a horde: draws members from the composition's pools,
 * positions them, finalizes them, applies equipment / reward / effects, and adds
 * them to the world pointed at the target player.
 */
public final class HordeSpawner {

    private HordeSpawner() {
    }

    public static int spawn(ServerLevel level, ServerPlayer player, SpawnContext ctx,
                            ProgressionScore score, HordeComposition comp, HordeConfig cfg, RandomSource rng) {
        int size = Mth.clamp(
                (int) Math.round(cfg.sizing().baseSize() + score.total() * cfg.sizing().sizePerScore()),
                cfg.sizing().minSize(), cfg.sizing().maxSize());

        List<Mob> mobs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            HordeDefinition pool = comp.pickPool(rng);
            EntityType<?> type = resolveType(level, pool, rng);
            Entity entity = type.create(level);
            if (!(entity instanceof Mob mob)) {
                if (entity != null) {
                    entity.discard();
                }
                continue;
            }
            BlockPos p = jitter(ctx.pos(), rng);
            mob.moveTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, rng.nextFloat() * 360.0f, 0.0f);
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(p), MobSpawnType.EVENT, null);
            mob.setCanPickUpLoot(false);
            mob.addTag(HarderHordes.HORDE_TAG);
            mobs.add(mob);
        }
        if (mobs.isEmpty()) {
            return 0;
        }

        List<Mob> equipped = EquipmentApplier.equipHorde(level, mobs, score, cfg, rng);
        HordeLoot.assignReward(equipped, cfg, rng);

        int[] chargedBudget = {cfg.creeper().maxCharged()};
        for (Mob mob : mobs) {
            HordeEffects.apply(level, mob, score, cfg, rng, chargedBudget);
        }

        for (Mob mob : mobs) {
            level.addFreshEntity(mob);
            if (mob instanceof Enemy) {
                mob.setTarget(player);
            }
        }
        return mobs.size();
    }

    private static EntityType<?> resolveType(ServerLevel level, HordeDefinition def, RandomSource rng) {
        Optional<HolderSet.Named<EntityType<?>>> tag = level.registryAccess()
                .registryOrThrow(Registries.ENTITY_TYPE).getTag(def.mobPool());
        if (tag.isPresent() && tag.get().size() > 0) {
            Optional<Holder<EntityType<?>>> picked = tag.get().getRandomElement(rng);
            if (picked.isPresent()) {
                return picked.get().value();
            }
        }
        // Empty/missing pool (e.g. an aquatic pool with no mod installed) → still spawn something.
        return EntityType.ZOMBIE;
    }

    private static BlockPos jitter(BlockPos base, RandomSource rng) {
        return base.offset(rng.nextInt(5) - 2, 0, rng.nextInt(5) - 2);
    }
}
