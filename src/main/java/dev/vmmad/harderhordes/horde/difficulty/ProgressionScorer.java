package dev.vmmad.harderhordes.horde.difficulty;

import dev.vmmad.harderhordes.config.HordeConfig;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Computes the blended progression score that drives every horde knob.
 *
 * <p>Three normalized sub-scores (~0..1) are weighted and scaled:
 * <ul>
 *   <li><b>days</b> — in-game days survived, on a diminishing-returns curve;</li>
 *   <li><b>local difficulty</b> — vanilla regional/effective difficulty;</li>
 *   <li><b>gear</b> — the player's armor + weapon strength, read from item
 *       attribute modifiers so modded gear counts too.</li>
 * </ul>
 */
public final class ProgressionScorer {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private ProgressionScorer() {
    }

    public static ProgressionScore score(ServerLevel level, Player player, HordeConfig cfg) {
        HordeConfig.Difficulty d = cfg.difficulty();

        double days = level.getDayTime() / 24000.0;
        double dayScore = 1.0 - Math.exp(-days / Math.max(0.01, d.dayHalfLife()));

        DifficultyInstance di = level.getCurrentDifficultyAt(player.blockPosition());
        double difficultyScore = Mth.clamp(di.getEffectiveDifficulty() / 6.75, 0.0, 1.0);

        double gearScore = gearScore(player);

        double raw = d.weightDays() * dayScore
                + d.weightLocalDifficulty() * difficultyScore
                + d.weightGear() * gearScore;
        double total = raw * d.scoreScale();

        return new ProgressionScore(total, dayScore, difficultyScore, gearScore);
    }

    private static double gearScore(Player player) {
        double sum = 0.0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            sum += armorValue(player.getItemBySlot(slot));
        }
        sum += weaponValue(player.getItemBySlot(EquipmentSlot.MAINHAND));
        return Mth.clamp(sum / 5.0, 0.0, 1.0);
    }

    private static double armorValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0.0;
        }
        double armor = attributeSum(stack, Attributes.ARMOR);
        double toughness = attributeSum(stack, Attributes.ARMOR_TOUGHNESS);
        return Mth.clamp(armor / 8.0, 0.0, 1.0) * 0.7 + Mth.clamp(toughness / 3.0, 0.0, 1.0) * 0.3;
    }

    private static double weaponValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0.0;
        }
        double damage = attributeSum(stack, Attributes.ATTACK_DAMAGE);
        // Wood/gold ~4 → 0, netherite ~9 → 1.
        return Mth.clamp((damage - 4.0) / 5.0, 0.0, 1.0);
    }

    private static double attributeSum(ItemStack stack, Holder<Attribute> attribute) {
        ItemAttributeModifiers modifiers = stack.getAttributeModifiers();
        double total = 0.0;
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (entry.attribute().equals(attribute)) {
                total += entry.modifier().amount();
            }
        }
        return total;
    }
}
