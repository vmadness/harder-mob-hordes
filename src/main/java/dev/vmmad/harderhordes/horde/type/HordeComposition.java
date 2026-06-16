package dev.vmmad.harderhordes.horde.type;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.RandomSource;

/**
 * The concrete plan for one horde: a {@code lead} definition (which themes the
 * overall horde) and the set of pools its members are drawn from. A single-type
 * horde has one pool; a hybrid horde blends several.
 */
public record HordeComposition(HordeDefinition lead, List<HordeDefinition> pools) {

    public static HordeComposition single(HordeDefinition def) {
        return new HordeComposition(def, List.of(def));
    }

    /** Builds a mixed horde: the primary plus 1–2 distinct other candidates. */
    public static HordeComposition hybrid(HordeDefinition primary, List<HordeDefinition> candidates, RandomSource rng) {
        List<HordeDefinition> pools = new ArrayList<>();
        pools.add(primary);
        List<HordeDefinition> others = new ArrayList<>(candidates);
        others.remove(primary);
        int extra = Math.min(others.size(), 1 + rng.nextInt(2));
        for (int i = 0; i < extra && !others.isEmpty(); i++) {
            pools.add(others.remove(rng.nextInt(others.size())));
        }
        return new HordeComposition(primary, pools);
    }

    /** Picks which pool the next member is drawn from. */
    public HordeDefinition pickPool(RandomSource rng) {
        return pools.get(rng.nextInt(pools.size()));
    }
}
