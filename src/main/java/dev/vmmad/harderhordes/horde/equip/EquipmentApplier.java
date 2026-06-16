package dev.vmmad.harderhordes.horde.equip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.vmmad.harderhordes.config.HordeConfig;
import dev.vmmad.harderhordes.horde.difficulty.ProgressionScore;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Decides which members of a horde are armed and equips them. Key design
 * constraints from the brief:
 * <ul>
 *   <li>Only a small, configurable fraction of the horde gets gear, so the
 *       horde is hard but beatable.</li>
 *   <li>Weapons climb above iron only as the score warrants ({@link TierTable}).</li>
 *   <li>Modded weapons are supported (via {@link HordeGearTags#BONUS_WEAPONS})
 *       but drop chances stay 0 here — only the reward mob's chances get raised
 *       later by the reward step.</li>
 * </ul>
 */
public final class EquipmentApplier {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private EquipmentApplier() {
    }

    /**
     * Equips a random subset of the horde and returns the mobs that received
     * gear (candidates for the single reward bearer).
     */
    public static List<Mob> equipHorde(ServerLevel level, List<Mob> mobs, ProgressionScore score,
                                       HordeConfig cfg, RandomSource rng) {
        List<Mob> equipped = new ArrayList<>();
        int n = mobs.size();
        if (n == 0) {
            return equipped;
        }
        int target = (int) Math.round(n * cfg.equip().equippedFraction());
        int count = Mth.clamp(target, 0, Math.min(cfg.equip().maxEquippedPerHorde(), n));
        if (count <= 0) {
            return equipped;
        }

        int[] order = shuffledIndices(n, rng);
        boolean leaderAssigned = false;
        for (int k = 0; k < count; k++) {
            Mob mob = mobs.get(order[k]);
            boolean front = !leaderAssigned;
            leaderAssigned = true;
            equipOne(level, mob, score, cfg, rng, front);
            equipped.add(mob);
        }
        return equipped;
    }

    private static void equipOne(ServerLevel level, Mob mob, ProgressionScore score,
                                 HordeConfig cfg, RandomSource rng, boolean front) {
        double s = score.total();
        GearTier weaponTier = GearTier.rollUpTo(TierTable.maxMelee(s, cfg), rng);
        EquipStyle style = styleFor(mob);

        if (style == EquipStyle.RANGED) {
            if (front && rng.nextDouble() < cfg.types().skeletonSwordLeaderChance()) {
                setMainHand(mob, new ItemStack(weaponTier.sword()), cfg, level, rng);
            } else {
                ItemStack bow = new ItemStack(Items.BOW);
                if (rng.nextDouble() < cfg.types().skeletonEnchantedBowChance()) {
                    enchant(level, bow, Enchantments.POWER, 1 + rng.nextInt(3));
                }
                setMainHand(mob, bow, cfg, level, rng);
            }
        } else if (style == EquipStyle.MELEE) {
            setMainHand(mob, new ItemStack(weaponTier.meleeWeapon(rng)), cfg, level, rng);
        }

        boolean allowNetheriteArmor = s >= cfg.tiers().netheriteScoreThreshold();
        GearTier armorMax = TierTable.maxArmor(s, cfg, allowNetheriteArmor);
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (rng.nextDouble() < cfg.equip().armorPieceChance()) {
                Item armor = GearTier.rollUpTo(armorMax, rng).armorFor(slot);
                if (armor != null) {
                    mob.setItemSlot(slot, new ItemStack(armor));
                    mob.setDropChance(slot, 0.0f);
                }
            }
        }
    }

    private static void setMainHand(Mob mob, ItemStack vanilla, HordeConfig cfg, ServerLevel level, RandomSource rng) {
        ItemStack stack = maybeModded(vanilla, HordeGearTags.BONUS_WEAPONS, cfg, level, rng);
        mob.setItemSlot(EquipmentSlot.MAINHAND, stack);
        mob.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    /** With a small chance, swap in a modded item from the bonus tag (if any exist). */
    private static ItemStack maybeModded(ItemStack fallback, TagKey<Item> tag, HordeConfig cfg,
                                         ServerLevel level, RandomSource rng) {
        if (!cfg.equip().allowModdedGear() || rng.nextDouble() >= cfg.equip().moddedGearChance()) {
            return fallback;
        }
        Optional<HolderSet.Named<Item>> set = level.registryAccess()
                .registryOrThrow(Registries.ITEM).getTag(tag);
        if (set.isEmpty() || set.get().size() == 0) {
            return fallback;
        }
        return set.get().getRandomElement(rng).map(h -> new ItemStack(h.value())).orElse(fallback);
    }

    private static void enchant(ServerLevel level, ItemStack stack,
                                net.minecraft.resources.ResourceKey<Enchantment> key, int enchantLevel) {
        try {
            Holder<Enchantment> holder = level.registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
            stack.enchant(holder, enchantLevel);
        } catch (Exception e) {
            // Enchantment registry unavailable — leave the item plain rather than fail the spawn.
        }
    }

    private static EquipStyle styleFor(Mob mob) {
        if (mob instanceof Creeper) {
            return EquipStyle.NONE;
        }
        if (mob instanceof AbstractSkeleton) {
            return EquipStyle.RANGED;
        }
        return EquipStyle.MELEE;
    }

    private static int[] shuffledIndices(int n, RandomSource rng) {
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) {
            idx[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = idx[i];
            idx[i] = idx[j];
            idx[j] = tmp;
        }
        return idx;
    }
}
