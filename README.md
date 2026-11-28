This mod adds two things.

1. A new ore which ties in to the food system, by being edible, making other things edible and introducing tools, realated to hunger and food.
2. The possibility to optain and use a small selection of stand abilitys from the popular animanga series JoJo's Bizarre Adventure.

These both tie in together, because the stands use food and hunger as their main resources for their abilities. The new ore and it's corresponding items help with that. 

I created this mod as a fun little add-on to a modpack I'm going to play with friends and to start to learning modding in general. Don't take it to serious.

## Minecraft 1.7.10

This branch is a port of the 1.19.2 version (`1.0.2-1.19.2`) to Minecraft 1.7.10 / Forge 10.13.4.1614,
split into two independent mods. Each one works without the other, and together they behave exactly like the
single 1.7.10 mod before the split.

| Folder | Mod (working titles) | Mod ID | Contents |
| --- | --- | --- | --- |
| `guerk/` | Guerk | `guerk` | Guerk Ore, Raw Guerk, ingot, nugget, block, tools, Food Leech, Constipation, support for other mods |
| `stands/` | Minecraft Requiem | `minecraftrequiem` | The stands, the arrows and Gold Infused Obsidian (only used for the arrow tips) |

What changed with the split:

- Each mod has its own creative tab and config file: `config/guerk-common.cfg` (ore spawn, Constipation potion ID,
  Tinkers' material ID) and `config/minecraftrequiem-common.cfg` (stand costs, Killer Queen, stand potion IDs).
- The Guerk items moved to the mod ID `guerk` (e.g. `guerk:guerk_ingot`). Worlds made with the combined 1.7.10 version lose them.
- The 1.7.10 versions have their own numbers: 0.1.0 was the combined mod, 0.2.0 is the split.

### Building

```
./gradlew build
```

The mod jars end up in `guerk/build/libs/guerk-0.2.0-1.7.10.jar` and `stands/build/libs/minecraftrequiem-0.2.0-1.7.10.jar`
(the `-dev` jars are for development only).
The build uses [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle), so a current Gradle works.
Minecraft 1.7.10 needs Java 8; Gradle downloads a Java 8 JDK by itself if none is installed.

`./gradlew :guerk:runClient` or `./gradlew :stands:runClient` starts a development client with that mod.

### Textures

The Guerk palette is the five colors of the Guerk Ingot: `#660066`, `#800080`, `#BE29EC`, `#D896FF` and `#EFBBFF`.
The original item and block textures are drawn in it (the Guerk Block adds its own in-between shades on purpose).
Everything added with the port uses only these colors (plus white highlights): the Molten Guerk textures and the
Tinkers' Construct tool part color `#BE29EC`. `python3 tools/guerk_palette.py check` lists off-palette pixels in those new
textures, `fix` snaps them to the closest palette color, and `molten` generates the Molten Guerk textures.
The stand skins and Sheer Heart Attack come from `tools/stand_textures.py`. Both scripts need Pillow.

### Differences to the 1.19.2 version

Some things don't exist in 1.7.10 and had to be replaced:

- **Deepslate** doesn't exist, so the Deepslate Guerk Ore was removed.
- **Guerk Ore** generates in every dimension that has stone (1.19: overworld biomes only).
  The ore height range was squeezed from the 1.19 world height onto the 1.7.10 one.
- **Raw ores** only came in 1.17. Like iron and gold in 1.7.10, Guerk Ore now drops itself (no Fortune bonus) and
  smelts into an ingot. Raw Guerk is what ore crushers make out of it: 2 Raw Guerk per ore, each smelting into an ingot.
- **Blasting** doesn't exist, only the furnace recipes are left. Item tags became ore dictionary entries
  (`oreGuerk`, `ingotGuerk`, `nuggetGuerk`, `blockGuerk`, and `dustGuerk` for Raw Guerk).
- **Enchanted golden apple**: 1.7.10 still has the vanilla recipe (8 gold blocks + apple), so the recipe from
  Edible Blocks of Gold and with it the Edible Block of Gold were removed.
- **Food Leech** is shown like the vanilla attributes in the tooltip: `+1 Food Leech` below `+5 Attack Damage`.
- **Levitation** (C-Moon) was added in 1.9, so the mod brings its own Levitation effect with the same behaviour,
  including its own icon and the exception from the server's "kicked for floating too long" check.
- **Glowing** (Highway To Hell marks the closest entity) was added in 1.9; the closest entity is marked with purple particles instead.
- **Turtles** don't exist, so Sheer Heart Attack has its own model now: a small blue-green tank with a white skull on the front and an orange-brown knob on the back, like in the series.
- **Stands** are their own entity now (1.19 used an invisible zombie with NoAI, which 1.7.10 can't do), and instead of
  dyed leather armor every stand has its own skin (`textures/entity/stand/`). D4C, Killer Queen and Magicians Red have
  extra model parts (ears, cat ears, beak).
  The stand isn't saved with the world anymore; it gets summoned again automatically after joining, respawning or changing dimension.
- **Sounds**: Warden emerge (D4C) became the nether portal sound, Beacon activate (Weather Report) became thunder.
- **Potion IDs**: 1.7.10 has no effect registry, so the custom effects use IDs 70-76 (Constipation: 75, in the Guerk mod).
  They can be changed in the config files if another mod uses them (client and server must match).
- **Fire resistant items** don't exist in 1.7.10, so the arrows can burn in lava now.
- **Stand Remover Arrow**: it had no recipe in 1.19, and dying no longer removes the stand (see below), so it can be crafted now:
  Requiem Arrow + Milk Bucket (shapeless, the empty bucket goes back into the inventory). The Remover Arrow is used up when it removes
  a stand; otherwise a Requiem Arrow and one Remover Arrow would allow endless free rerolls.
- **Stand Arrow XP**: the arrow still needs level 30 and removes the XP needed for level 30. That is 825 XP in 1.7.10 (1395 in 1.19).
- **Attack strength** (the 1.9 attack cooldown) doesn't exist, so Highway To Hell without a sword or tool always deals 1 damage.
- The configs are Forge `.cfg` files now (see above). The empty client config was dropped.

### Other mods (Guerk)

None of these are required; each integration only runs if the mod is installed.

| Mod | What Guerk gets |
| --- | --- |
| Tinkers' Construct | Molten Guerk: ore melts into 2 ingots, Raw Guerk, nuggets, ingots and blocks melt and cast back. Guerk is a tool material (stone-like stats: harvest level 1, 131 durability) whose effect depends on the part: **head** = Food Leech, **handle** = Snack (sneak + right click eats a piece of the tool: 10 durability for 1 food, once per second), **binding/guard** = Constipation for 5 s on every hit. |
| IndustrialCraft 2 | Macerator: ore -> 2 Raw Guerk, ingot -> 1 Raw Guerk |
| Mekanism | Enrichment Chamber: ore -> 2 Raw Guerk; Crusher: ingot -> 1 Raw Guerk |
| Thermal Expansion | Pulverizer and Induction Smelter recipes, generated by Thermal Expansion itself from the ore dictionary |
| Ender IO | SAG Mill: ore -> 2 Raw Guerk (+15 % cobblestone), ingot -> 1 Raw Guerk |
| Applied Energistics 2 | Quartz Grindstone: ore -> 1 Raw Guerk + 90 % chance of a second, ingot -> 1 Raw Guerk |
| Railcraft | Rock Crusher: ore -> 2 Raw Guerk |

The Tinkers' material ID (default 218) can be changed in `config/guerk-common.cfg` if it clashes with another addon.

Some 1.19 bugs were fixed along the way:

- The stand is kept on death now (the 1.19 version copied the data from the old player onto itself).
- Magicians Red's fire resistance lasts as long as the stand is summoned. In 1.19 it ran out after 30 minutes,
  and drinking milk or dying removed it.
- The arrows are handled by the server. In 1.19 the client sent a packet when it used an arrow, and the server
  trusted it without checking whether the player held one.
- The ore vein config values are actually used now (1.19 always used the defaults).
- The Stand Arrow's XP cost is taken on the server (1.19 only removed it on the client, so it came back after relogging).
  It only kills players without a stand, as in the commented out code of the 1.19 item.
- All player data is synced with one packet instead of five.
- Killer Queen: if the bombed target dies, despawns or gets out of range, a new bomb can be placed right away.
  In 1.19 the bomb stayed marked as placed until it was detonated into nothing, which cost food.
  Losing the stand to the Remover Arrow now also defuses its bombs; in 1.19 they and Sheer Heart Attack stayed forever.
- Time Stop (The World) holds entities in place. 1.19 teleported them to where they were every tick, so they kept
  creeping along at about half speed and slowly fell out of the air.
- King Crimson's time skip lands 1.5 blocks behind the attacker even if it looks down. In 1.19 the distance shrank
  with the attacker's view angle, down to landing inside it.
- Green Day only ends its own poison when going back up (1.19 removed any poison), and a deeper fall makes the poison
  stronger right away instead of after the weaker one ran out.
- Constipation is a harmful effect (1.19 registered it as beneficial, which matters for mods that cure bad effects),
  and it also stops eating cake.
