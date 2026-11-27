This mod adds two things.

1. A new ore which ties in to the food system, by being edible, making other things edible and introducing tools, realated to hunger and food.
2. The possibility to optain and use a small selection of stand abilitys from the popular animanga series JoJo's Bizarre Adventure.

These both tie in together, because the stands use food and hunger as their main resources for their abilities. The new ore and it's corresponding items help with that. 

I created this mod as a fun little add-on to a modpack I'm going to play with friends and to start to learning modding in general. Don't take it to serious.

## Minecraft 1.7.10

This branch is a port of the 1.19.2 version (`1.0.2-1.19.2`) to Minecraft 1.7.10 / Forge 10.13.4.1614.

### Building

```
./gradlew build
```

The mod jar ends up in `build/libs/minecraftrequien-1.0.2-1.7.10.jar` (the `-dev` jar is for development only).
The build uses [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle), so a current Gradle works.
Minecraft 1.7.10 needs Java 8; Gradle downloads a Java 8 JDK by itself if none is installed.

`./gradlew runClient` starts a development client with the mod.

### Differences to the 1.19.2 version

Some things don't exist in 1.7.10 and had to be replaced:

- **Deepslate** doesn't exist, so the Deepslate Guerk Ore was removed.
- **Guerk Ore** generates in every dimension that has stone (1.19: overworld biomes only).
  The ore height range was squeezed from the 1.19 world height onto the 1.7.10 one.
- **Blasting** doesn't exist, only the furnace recipes are left. Item tags became ore dictionary entries
  (`oreGuerk`, `ingotGuerk`, `nuggetGuerk`, `blockGuerk`).
- **Enchanted golden apple**: 1.7.10 still has the vanilla recipe (8 gold blocks + apple), so the recipe from
  Edible Blocks of Gold and with it the Edible Block of Gold were removed.
- **Food Leech** is shown like the vanilla attributes in the tooltip: `+1 Food Leech` below `+5 Attack Damage`.
- **Levitation** (C-Moon) was added in 1.9, so the mod brings its own Levitation effect with the same behaviour.
- **Glowing** (Highway To Hell marks the closest entity) was added in 1.9; the closest entity is marked with purple particles instead.
- **Turtles** don't exist, so Sheer Heart Attack is a small skull with its name above it.
- **Stands** are their own invisible entity now (1.19 used an invisible zombie with NoAI, which 1.7.10 can't do).
  The stand isn't saved with the world anymore; it gets summoned again automatically after joining, respawning or changing dimension.
- **Sounds**: Warden emerge (D4C) became the nether portal sound, Beacon activate (Weather Report) became thunder.
- **Potion IDs**: 1.7.10 has no effect registry, so the custom effects use IDs 70-76. They can be changed in
  `config/minecraftrequiem-common.cfg` if another mod uses them (client and server must match).
- **Fire resistant items** don't exist in 1.7.10, so the arrows can burn in lava now.
- **Stand Arrow XP**: the arrow still needs level 30 and removes the XP needed for level 30. That is 825 XP in 1.7.10 (1395 in 1.19).
- The config is a Forge `.cfg` file now: `config/minecraftrequiem-common.cfg`. The empty client config was dropped.

Some 1.19 bugs were fixed along the way:

- The stand is kept on death now (the 1.19 version copied the data from the old player onto itself).
- The ore vein config values are actually used now (1.19 always used the defaults).
- The Stand Arrow's XP cost is taken on the server (1.19 only removed it on the client, so it came back after relogging).
  It only kills players without a stand, as in the commented out code of the 1.19 item.
- All player data is synced with one packet instead of five.
