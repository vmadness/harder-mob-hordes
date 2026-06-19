package dev.vmmad.harderhordes.horde.type;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * Which dimension family a horde definition belongs to. Lets each dimension have
 * its own mob pools (overworld undead, Nether fortress mobs, End horrors) instead
 * of dragging surface mobs somewhere they'd never spawn naturally. Matched against
 * the resolved {@code SpawnContext}; {@link #ANY} matches everywhere.
 */
public enum HordeDimension {
    OVERWORLD,
    NETHER,
    END,
    ANY;

    /** Maps a level to its horde-dimension family; unknown/custom dimensions are treated as overworld. */
    public static HordeDimension of(ServerLevel level) {
        if (level.dimension().equals(Level.NETHER)) {
            return NETHER;
        }
        if (level.dimension().equals(Level.END)) {
            return END;
        }
        return OVERWORLD;
    }
}
