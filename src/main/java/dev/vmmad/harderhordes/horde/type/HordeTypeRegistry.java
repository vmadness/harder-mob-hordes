package dev.vmmad.harderhordes.horde.type;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;

/**
 * The public registry of horde definitions and the mod's extension point
 * (MOD_CODING_CONVENTION.md §2/§3): one holder, registration in one place.
 * Other mods may call {@link #register} from their own setup to add hordes.
 */
public final class HordeTypeRegistry {

    private static final Map<ResourceLocation, HordeDefinition> DEFINITIONS = new LinkedHashMap<>();

    private HordeTypeRegistry() {
    }

    public static void register(HordeDefinition definition) {
        DEFINITIONS.put(definition.id(), definition);
    }

    public static HordeDefinition get(ResourceLocation id) {
        return DEFINITIONS.get(id);
    }

    public static Collection<HordeDefinition> all() {
        return Collections.unmodifiableCollection(DEFINITIONS.values());
    }
}
