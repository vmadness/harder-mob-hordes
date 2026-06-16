package dev.vmmad.harderhordes.horde.reward;

import java.util.List;

import dev.vmmad.harderhordes.HarderHordes;
import dev.vmmad.harderhordes.config.HordeConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;

/**
 * Implements the reward rule exactly as specified: a horde <em>may</em> (low
 * chance) yield a reward; if it does, <strong>one</strong> equipped mob is the
 * bearer and only its gear can drop. Every other mob keeps drop chance 0, no
 * matter how much gear it carries. The bearer drops whatever it actually holds —
 * modded items included — handled by vanilla's per-slot drop-chance mechanic.
 */
public final class HordeLoot {

    private static final EquipmentSlot[] EQUIP_SLOTS = {
            EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST,
            EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private HordeLoot() {
    }

    public static void assignReward(List<Mob> equipped, HordeConfig cfg, RandomSource rng) {
        if (equipped.isEmpty() || rng.nextDouble() >= cfg.reward().hordeChance()) {
            return;
        }
        Mob bearer = cfg.reward().preferBestEquipped()
                ? bestEquipped(equipped)
                : equipped.get(rng.nextInt(equipped.size()));

        bearer.addTag(HarderHordes.REWARD_TAG);
        float chance = (float) cfg.reward().dropChance();
        for (EquipmentSlot slot : EQUIP_SLOTS) {
            if (!bearer.getItemBySlot(slot).isEmpty()) {
                bearer.setDropChance(slot, chance);
            }
        }
    }

    private static Mob bestEquipped(List<Mob> equipped) {
        Mob best = equipped.get(0);
        int bestCount = equipmentCount(best);
        for (Mob mob : equipped) {
            int count = equipmentCount(mob);
            if (count > bestCount) {
                best = mob;
                bestCount = count;
            }
        }
        return best;
    }

    private static int equipmentCount(Mob mob) {
        int count = 0;
        for (EquipmentSlot slot : EQUIP_SLOTS) {
            if (!mob.getItemBySlot(slot).isEmpty()) {
                count++;
            }
        }
        return count;
    }
}
