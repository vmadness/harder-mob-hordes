# Harder Hordes

A NeoForge mod for Minecraft **1.21.1**. It sends mob hordes after you, and they get
worse as you do: the longer you survive and the better your gear, the bigger and
meaner the packs that come hunting, mostly at night.

You can fight or run. They're not built to wipe you instantly: only some of each
horde carries weapons, good gear shows up slowly, and your first few days are left
quiet so you can find your feet.

## How it works

Every few seconds the mod rolls the dice for each player. Win the roll and a horde
spawns off-screen and walks toward you. Three things raise the odds: night (10x by
default), how many days into the world you are (nothing spawns until a day you set,
then it ramps up), and a per-player cooldown so you aren't jumped twice in a row.

When a horde fires, a hidden **progression score** decides how nasty it is. The score
blends days survived, vanilla local difficulty, and the gear you're carrying. The
higher it climbs, the more you get: more mobs, better gear, more **hybrid** (mixed)
hordes, and the occasional rare **elite** pack (wither skeletons, vindicators,
pillagers). Stand near water and one might wade out at you instead.

Run `/harderhordes score` in game to see your own number.

## Configuration

Everything lives in **`config/harder_hordes-common.toml`**, written the first time the
mod runs. It's one global file, and NeoForge reloads it when you save, so you rarely
need a restart.

The things people usually change:

- **More or fewer hordes:** `[spawn] dayBaseChance` (`0.002` for more, `0.0005` for
  less), plus `nightMultiplier` and `minSecondsBetweenHordes`.
- **Protect the early game:** `[spawn] minWorldDay` (nothing before this day) and
  `fullFrequencyDay` (chance ramps to full by this one).
- **Bigger hordes:** `[size] baseSize`, `sizePerScore`, `maxSize`.
- **Harder, faster:** `[difficulty]` weights and `scoreScale`. `scoreScale` is the
  master dial: it drives size, gear tiers, and the hybrid and elite gates at once.
- **Tougher mobs by score:** `[scaling]` adds bonus health and attack damage that grow
  with the progression score (each capped), on top of vanilla stats.
- **Better-armed mobs:** `[equip] equippedFraction` (+ `equippedFractionPerScore`) and
  `maxEquippedPerHorde`, plus the `[tiers]` diamond and netherite thresholds.
- **Which dimensions:** `[spawn] dimensions` lists where hordes spawn. Each dimension
  fields its own mobs (overworld undead, Nether fortress mobs, End horrors).
- **Gear drops:** `[rewards] hordeChance` (set `0.0` to turn drops off). At most one mob
  per horde drops its gear; the rest drop nothing.
- **Toggle mob types:** `[types]` switches `zombie`, `skeleton`, `creeper`, `aquatic`,
  `elite`, `nether`, and `end` on or off.

<details>
<summary>Full config reference</summary>

### `[spawn]` — when and where hordes happen

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `checkIntervalTicks` | 200 | 20–12000 | Ticks between rolls (20 = 1s). Higher = cheaper, rarer checks. |
| `dayBaseChance` | 0.001 | 0–1 | Daytime chance per check. The main frequency dial. |
| `nightMultiplier` | 10.0 | 1–50 | Night multiplies the base chance. |
| `minSecondsBetweenHordes` | 600 | 0–14400 | Per-player cooldown in real seconds. |
| `minRadius` | 24 | 8–128 | Min spawn distance from the player (blocks). |
| `maxRadius` | 48 | 8–256 | Max spawn distance from the player (blocks). |
| `dimensions` | `[overworld, the_nether, the_end]` | id list | Dimensions hordes spawn in. Each draws from its own mob pools. Short names `overworld`/`nether`/`end` also work. |
| `minWorldDay` | 4 | 0–100000 | No hordes before this in-game day. |
| `fullFrequencyDay` | 12 | 0–100000 | Chance ramps 0 → full between `minWorldDay` and this day. |

### `[size]` — how big hordes get

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `baseSize` | 4 | 1–64 | Mobs at score 0. |
| `sizePerScore` | 1.5 | 0–16 | Extra mobs per progression-score point. |
| `minSize` | 3 | 1–64 | Floor on horde size. |
| `maxSize` | 24 | 1–128 | Ceiling on horde size. |

### `[equip]` — how many mobs are armed

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `equippedFraction` | 0.25 | 0–1 | Base fraction of the horde that carries gear. |
| `equippedFractionPerScore` | 0.02 | 0–1 | Extra equipped fraction per score point (capped at 1.0 total). |
| `maxEquippedPerHorde` | 4 | 0–64 | Hard cap on armed mobs. |
| `armorPieceChance` | 0.5 | 0–1 | Base per-slot chance of an armor piece. |
| `armorPieceChancePerScore` | 0.03 | 0–1 | Extra per-slot armor chance per score point (capped at 1.0 total). |
| `allowModdedGear` | true | bool | Allow weapons from `#harder_hordes:bonus_weapons`. |
| `moddedGearChance` | 0.15 | 0–1 | Chance an armed mob uses a modded weapon (if any tagged). |

### `[scaling]` — stat boosts that grow with score

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `healthPerScore` | 0.5 | 0–100 | Bonus max health (half-hearts) added per score point. |
| `maxHealthBonus` | 20.0 | 0–1024 | Cap on bonus max health a single mob can gain. |
| `damagePerScore` | 0.15 | 0–100 | Bonus attack damage added per score point. |
| `maxDamageBonus` | 4.0 | 0–1024 | Cap on bonus attack damage a single mob can gain. |

### `[rewards]` — the rare gear drop

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `hordeChance` | 0.15 | 0–1 | Chance a horde tags a reward mob at all. |
| `dropChance` | 1.0 | 0–1 | Drop chance on the reward mob's equipped slots. |
| `preferBestEquipped` | true | bool | Best-geared mob is the bearer (else random). |

### `[difficulty]` — the progression score

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `weightDays` | 0.4 | 0–1 | Weight of days-survived. |
| `weightLocalDifficulty` | 0.35 | 0–1 | Weight of vanilla regional difficulty. |
| `weightGear` | 0.25 | 0–1 | Weight of your equipped gear. |
| `dayHalfLife` | 12.0 | 0.1–1000 | Days at which the time component is ~63% maxed. |
| `scoreScale` | 10.0 | 1–100 | Stretches the 0..1 blend into the working ~0..10 range. |

### `[hybrid]` — mixed-type hordes

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `scoreGate` | 5.0 | 0–100 | Score before hybrids can occur. |
| `chance` | 0.35 | 0–1 | Chance a qualifying horde is hybrid. |

### `[creeper]` — creeper extras

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `chargedChance` | 0.1 | 0–1 | Per-creeper chance to spawn charged. |
| `maxCharged` | 2 | 0–32 | Cap on charged creepers per horde. |
| `effectChance` | 0.3 | 0–1 | Chance a creeper gets a buff effect. |

### `[types]` — per-type toggles and tuning

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `zombie` / `skeleton` / `creeper` / `aquatic` / `elite` / `nether` / `end` | true | bool | Enable each horde type. |
| `skeletonEnchantedBowChance` | 0.4 | 0–1 | Chance a skeleton bow is enchanted. |
| `skeletonSwordLeaderChance` | 0.5 | 0–1 | Chance the lead skeleton wields a sword. |
| `eliteScoreGate` | 8.0 | 0–100 | Score before elite hordes can occur. |
| `eliteBaseChance` | 0.02 | 0–1 | Base elite chance past the gate. |
| `elitePerScore` | 0.01 | 0–1 | Added elite chance per score point past the gate. |
| `eliteMaxChance` | 0.2 | 0–1 | Ceiling on elite chance. |

### `[tiers]` — gear-tier unlocks

| Key | Default | Range | Meaning |
| --- | --- | --- | --- |
| `diamondScoreThreshold` | 5.0 | 0–100 | Score at which diamond gear can appear. |
| `netheriteScoreThreshold` | 8.0 | 0–100 | Score at which netherite gear can appear. |

</details>

## Adding mobs and weapons (no code)

Mob pools are just **entity-type tags**. Drop entity IDs into the right tag and they
start showing up in hordes:

```
data/harder_hordes/tags/entity_type/horde_mobs/{zombie,skeleton,creeper,aquatic,elite}.json
```

Mark modded IDs `"required": false` so a missing mod is skipped instead of crashing.
Water mobs go in `aquatic` if you want water hordes; that pool falls back to zombies
when empty. The mod reads each mob to arm it: creepers get nothing, anything
skeleton-shaped gets a bow, the rest get melee gear. Modded mobs sort themselves out.

Weapons work the same way. Tag items into `#harder_hordes:bonus_weapons` (empty by
default) and armed mobs may carry them; `equip.allowModdedGear` and
`equip.moddedGearChance` control how often.

## Commands

`/harderhordes` (requires op level 2):

| Command | Effect |
| --- | --- |
| `spawn <type>` | Force a horde: `zombie`, `skeleton`, `creeper`, `aquatic`, `elite`. |
| `spawn hybrid` | Force a mixed-type horde. |
| `score` | Print your progression score and its breakdown. |
| `list` | List horde definitions and which match your surroundings. |

## Compatibility & notes

No hard dependencies. Mobs and weapons come in through tags, so the mod runs fine on
its own and gets richer as you add content packs. It's server-authoritative: the config
and tag datapacks decide difficulty and mob pools on the server, and clients only need
the jar. It adds no items, blocks, or entities of its own, so it's all server-side.

Licensed MIT.
