package dev.vmmad.harderhordes.horde.effect;

import dev.vmmad.harderhordes.HarderHordes;
import dev.vmmad.harderhordes.config.HordeConfig;
import dev.vmmad.harderhordes.horde.difficulty.ProgressionScore;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Scales a horde member's raw stats with the player's progression score. Vanilla
 * Nether/End mobs are already tougher, so this is a flat additive bump on top of
 * their base attributes (each capped) rather than a multiplier: late-game hordes
 * hit harder and soak more without trivializing early ones.
 */
public final class StatScaling {

    private static final ResourceLocation HEALTH_ID =
            ResourceLocation.fromNamespaceAndPath(HarderHordes.MOD_ID, "horde_health");
    private static final ResourceLocation DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath(HarderHordes.MOD_ID, "horde_damage");

    private StatScaling() {
    }

    public static void apply(Mob mob, ProgressionScore score, HordeConfig cfg) {
        HordeConfig.Scaling sc = cfg.scaling();
        double s = score.total();

        double health = Math.min(s * sc.healthPerScore(), sc.maxHealthBonus());
        boolean buffedHealth = addModifier(mob, Attributes.MAX_HEALTH, HEALTH_ID, health);

        double damage = Math.min(s * sc.damagePerScore(), sc.maxDamageBonus());
        addModifier(mob, Attributes.ATTACK_DAMAGE, DAMAGE_ID, damage);

        if (buffedHealth) {
            mob.setHealth(mob.getMaxHealth());
        }
    }

    private static boolean addModifier(Mob mob, Holder<Attribute> attribute, ResourceLocation id, double amount) {
        if (amount <= 0.0) {
            return false;
        }
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance == null) {
            return false;
        }
        instance.addPermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        return true;
    }
}
