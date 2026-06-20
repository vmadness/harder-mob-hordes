# ⚔️ Harder Hordes

**Mobs come after you in groups, and they get nastier the better you do.** 😈

The longer you survive and the better your gear, the bigger the packs that hunt
you, mostly at night. 🌙 Fight them or run. They won't kill you on sight, your
first days stay calm so you can settle in. 🏕️

## ✨ What you'll notice

- 📈 **Grows with you** — survive and gear up, and hordes get bigger and tougher.
- 🌙 **Nights are scary again** — they show up nearby and walk right at you.
- 🌍 **Every world has its own mobs** — zombies up top, piglins & blazes in the
  Nether, endermen in the End.
- 💎 **Rare loot** — sometimes one mob drops its gear, so clearing a horde can pay off.
- 🔔 **Protect your base** — put a bell on a 3x3 platform of metal blocks (iron → diamond, plus a netherite block for the biggest zone) to make a safe zone hordes won't spawn in.
- 🧩 **Works with other mods** — modded mobs and weapons join in on their own.

👉 **Full feature list:** [FEATURES.md on GitHub](https://github.com/vmadness/harder-mob-hordes/blob/main/FEATURES.md)

## ⚙️ Settings

Everything is in one file: `config/harder_hordes-common.toml`. Open it with any text
editor, every setting has a plain note next to it, and changes apply without a restart. ✅

<details>
<summary>📖 <b>What you can change</b> (click to open)</summary>

- 🗺️ **spawn** — how often hordes come, how close, and which worlds.
- 🔔 **ward** — bell-totem safe zones and how big each metal makes them.
- 👥 **size** — how many mobs per horde (and the max).
- 🛡️ **equip** — how many carry weapons and armor.
- 💪 **scaling** — extra health and damage as you get stronger.
- 🎁 **rewards** — how often hordes drop loot.
- 🎚️ **difficulty** — your "danger level" math.
- 🧟‍♂️ **hybrid** — mixed-type hordes.
- 💥 **creeper** — charged creepers and buffs.
- 🔘 **types** — turn each horde type on or off.
- ⛏️ **tiers** — when mobs get diamond and netherite gear.

**More or fewer hordes?** → change `dayBaseChance` (bigger = more). 📊
**Harder or easier overall?** → change `scoreScale` (bigger = harder). 🔥

</details>

## 🕹️ Commands

`/harderhordes` (needs cheats):

- `spawn <type>` — force a horde 🐺
- `score` — see your danger level 📟
- `list` — see what can spawn where you stand 📍
- `ward` — check if you're in a safe zone 🔔

## 🔌 Compatibility

No required dependencies. Adds no items or blocks, just the hordes.
Built for **Minecraft 1.21.1** on **NeoForge**. 🧱
