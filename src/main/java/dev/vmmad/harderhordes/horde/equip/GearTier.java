package dev.vmmad.harderhordes.horde.equip;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Vanilla gear tiers the hordes draw from. Deliberately starts at iron — weaker
 * tiers aren't threatening, and the user wants above-iron weapons to appear as
 * difficulty climbs. Modded gear is layered on separately via {@link HordeGearTags}.
 */
public enum GearTier {

    IRON(Items.IRON_SWORD, Items.IRON_AXE,
            Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS),
    DIAMOND(Items.DIAMOND_SWORD, Items.DIAMOND_AXE,
            Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS),
    NETHERITE(Items.NETHERITE_SWORD, Items.NETHERITE_AXE,
            Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS);

    private final Item sword;
    private final Item axe;
    private final Item helmet;
    private final Item chestplate;
    private final Item leggings;
    private final Item boots;

    GearTier(Item sword, Item axe, Item helmet, Item chestplate, Item leggings, Item boots) {
        this.sword = sword;
        this.axe = axe;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public Item meleeWeapon(RandomSource rng) {
        return rng.nextBoolean() ? sword : axe;
    }

    public Item sword() {
        return sword;
    }

    public Item armorFor(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> helmet;
            case CHEST -> chestplate;
            case LEGS -> leggings;
            case FEET -> boots;
            default -> null;
        };
    }

    /** A random tier in {@code [IRON, max]}, biased a little toward the lower end. */
    public static GearTier rollUpTo(GearTier max, RandomSource rng) {
        int top = max.ordinal();
        // Two rolls, take the lower → skews toward iron so top-tier squads stay rare.
        int a = rng.nextInt(top + 1);
        int b = rng.nextInt(top + 1);
        return values()[Math.min(a, b)];
    }
}
