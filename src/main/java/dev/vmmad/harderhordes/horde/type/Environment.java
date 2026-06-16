package dev.vmmad.harderhordes.horde.type;

/** Where a horde definition is allowed to spawn, matched against the resolved {@code SpawnContext}. */
public enum Environment {
    /** Dry land only. */
    LAND,
    /** Water / surrounded-by-water only (the aquatic-mob path). */
    WATER,
    /** Either environment. */
    ANY
}
