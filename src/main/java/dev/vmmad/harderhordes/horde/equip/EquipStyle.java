package dev.vmmad.harderhordes.horde.equip;

/** How a horde member fights, derived from the mob instance at spawn time. */
public enum EquipStyle {
    /** Holds a melee weapon (zombies, vindicators, drowned…). */
    MELEE,
    /** Holds a bow, with a small chance the front member wields a sword instead. */
    RANGED,
    /** Carries no weapon (creepers). */
    NONE
}
