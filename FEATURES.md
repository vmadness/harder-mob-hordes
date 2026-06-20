# Harder Hordes — Full feature list

The short version lives on the [CurseForge page](CURSEFORGE_DESCRIPTION.md). This is
the complete rundown for anyone who wants the details.

## How hordes work

- **Difficulty that scales with you.** A hidden "danger level" blends three things:
  how many days you've survived, how good your gear is, and Minecraft's own local
  (regional) difficulty. The higher it climbs, the bigger and nastier the hordes.
  Check yours in-game with `/harderhordes score`.
- **Night-weighted spawns.** Hordes can show up any time, but night is far more
  dangerous (10× more likely by default). They appear off-screen and walk toward you.
- **Early-game protection.** No hordes before a configurable day, then the rate ramps
  up gradually, so a fresh world stays survivable while you find your feet.
- **Fight or run.** Hordes aren't built to wipe you on sight: only part of each group
  carries weapons, and good gear shows up gradually.

## The hordes themselves

- **Per-world mob types.** Each dimension fields its own creatures:
  - **Overworld** — zombies, skeletons, creepers, and (near water) drowned & guardians.
  - **Nether** — piglins, blazes, wither skeletons, magma cubes, hoglins.
  - **End** — endermen and friends.
- **Rare elite packs.** Occasionally a tougher group (wither skeletons, vindicators,
  pillagers) shows up once you're strong enough.
- **Hybrid hordes.** At higher danger levels, mixed-type hordes start to appear.
- **Charged creepers & buffed mobs.** Creepers can spawn charged or with effects, and
  mobs gain bonus health and damage as your danger level climbs.

## Gear & loot

- **Gradual gear progression.** Armed mobs unlock iron, then diamond, then netherite
  as your danger level rises. More of them carry gear (and armor) the longer you last.
- **Rare loot.** Now and then a horde tags a single mob whose gear actually drops, so
  clearing one can pay off. Most mobs drop nothing.

## Defend your base

- **Bell-totem safe zones.** Place a bell on top of a 3x3 platform of metal blocks and
  hordes stop spawning around it. The metal decides the radius: iron (small) < gold <
  diamond < netherite (largest); a mixed platform counts as its weakest block. The top
  tier only needs a single netherite block on an otherwise diamond platform, not nine. No
  nether star needed and it works underground, so it protects any house, base, or village.
  Check coverage with `/harderhordes ward`. Tune the radii (or turn it off) in `[ward]`.

## Make it yours

- **Deeply configurable.** Spawn rate, horde size, gear tiers, drop chances, extra
  toughness, elite gates, which worlds get hordes, and per-type on/off switches all
  live in one global config file that reloads without a restart.
- **No-code mod compatibility.** Mob pools and bonus weapons are driven by tags. Add
  modded mob or weapon IDs to the tags and they appear in hordes automatically — no
  add-on required. Empty pools (like an aquatic pool with no water-mob mod installed)
  safely fall back, so the base mod always works.

## Commands

`/harderhordes` (requires operator / cheats):

- `spawn <type>` — force a horde (`zombie`, `skeleton`, `creeper`, `aquatic`, `elite`,
  `nether`, `end`).
- `spawn hybrid` — force a mixed-type horde.
- `score` — show your current danger level and how it breaks down.
- `list` — list horde types and which ones match where you're standing.
- `recent` — show the last few hordes that spawned.
- `ward` — tell you whether you're standing inside a bell-totem safe zone.

## Compatibility

No hard dependencies. Mobs and weapons come in through tags, so the mod runs fine on
its own and gets richer as you add content packs. It adds no items, blocks, or
entities of its own — all logic is server-side gameplay.

Built for **Minecraft 1.21.1** on **NeoForge**.
