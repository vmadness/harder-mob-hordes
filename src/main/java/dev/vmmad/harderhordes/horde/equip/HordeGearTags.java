package dev.vmmad.harderhordes.horde.equip;

import dev.vmmad.harderhordes.HarderHordes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Optional item tags for <em>bonus</em> (typically modded) gear. Empty by
 * default, so out of the box hordes use vanilla tiers only. A mod/datapack can
 * fill these to have a few horde members brandish modded weapons/armor for
 * flavor — which still only ever drops from the single reward mob.
 */
public final class HordeGearTags {

    public static final TagKey<Item> BONUS_WEAPONS = itemTag("bonus_weapons");
    public static final TagKey<Item> BONUS_ARMOR = itemTag("bonus_armor");

    private HordeGearTags() {
    }

    private static TagKey<Item> itemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(HarderHordes.MOD_ID, path));
    }
}
