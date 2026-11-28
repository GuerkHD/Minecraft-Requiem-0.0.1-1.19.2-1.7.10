This mod adds two things.

1. A new ore which ties in to the food system, by being edible, making other things edible and introducing tools, realated to hunger and food.
2. The possibility to optain and use a small selection of stand abilitys from the popular animanga series JoJo's Bizarre Adventure.

These both tie in together, because the stands use food and hunger as their main resources for their abilities. The new ore and it's corresponding items help with that. 

I created this mod as a fun little add-on to a modpack I'm going to play with friends and to start to learning modding in general. Don't take it to serious.

## Minecraft 1.7.10: Minecraft Requiem (stands)

This branch is the stand part of the 1.19.2 version (`1.0.2-1.19.2`), ported to Minecraft 1.7.10 / Forge 10.13.4.1614:
the stands, the arrows and Gold Infused Obsidian, which is only used for the arrow tips (mod ID `minecraftrequiem`).
Gastrolithic Guerk (the Guerk ore, items and tools) is its own mod on the branch
`claude/gastrolithic-guerk-1.7.10`. Each mod works without the other; together they behave like
the 1.19 mod.

### Building

```
./gradlew build
```

The mod jar ends up in `build/libs/minecraftrequiem-0.2.0-1.7.10.jar` (the `-dev` jar is for development only).
The build uses [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle), so a current Gradle works.
Minecraft 1.7.10 needs Java 8; Gradle downloads a Java 8 JDK by itself if none is installed.
`./gradlew runClient` starts a development client with the mod.

### Textures

The stand skins and Sheer Heart Attack come from `tools/stand_textures.py` (needs Pillow).

### Differences to the 1.19.2 version

Some things don't exist in 1.7.10 and had to be replaced:

- **Levitation** (C-Moon) was added in 1.9, so the mod brings its own Levitation effect with the same behaviour,
  including its own icon and the exception from the server's "kicked for floating too long" check.
- **Glowing** (Highway To Hell marks the closest entity) was added in 1.9; the closest entity is marked with purple particles instead.
- **Turtles** don't exist, so Sheer Heart Attack has its own model now: a small blue-green tank with a white skull on the front and an orange-brown knob on the back, like in the series.
- **Stands** are their own entity now (1.19 used an invisible zombie with NoAI, which 1.7.10 can't do), and instead of
  dyed leather armor every stand has its own skin (`textures/entity/stand/`). D4C, Killer Queen and Magicians Red have
  extra model parts (ears, cat ears, beak).
  The stand isn't saved with the world anymore; it gets summoned again automatically after joining, respawning or changing dimension.
- **Sounds**: Warden emerge (D4C) became the nether portal sound, Beacon activate (Weather Report) became thunder.
- **Potion IDs**: 1.7.10 has no effect registry, so the stand effects use the IDs 70-74 and 76. They can be
  changed in the config file if another mod uses them (client and server must match).
- **Fire resistant items** don't exist in 1.7.10, so the arrows can burn in lava now.
- **Stand Remover Arrow**: it had no recipe in 1.19, and dying no longer removes the stand (see below), so it can be crafted now:
  Requiem Arrow + Milk Bucket (shapeless, the empty bucket goes back into the inventory). The Remover Arrow is used up when it removes
  a stand; otherwise a Requiem Arrow and one Remover Arrow would allow endless free rerolls.
- **Stand Arrow XP**: the arrow still needs level 30 and removes the XP needed for level 30. That is 825 XP in 1.7.10 (1395 in 1.19).
- **Attack strength** (the 1.9 attack cooldown) doesn't exist, so Highway To Hell without a sword or tool always deals 1 damage.
- The config is a Forge `.cfg` file now: `config/minecraftrequiem-common.cfg` (stand costs, Killer Queen, potion IDs).
  The empty client config was dropped.

### Fixed 1.19 bugs

- The stand is kept on death now (the 1.19 version copied the data from the old player onto itself).
- Magicians Red's fire resistance lasts as long as the stand is summoned. In 1.19 it ran out after 30 minutes,
  and drinking milk or dying removed it.
- The arrows are handled by the server. In 1.19 the client sent a packet when it used an arrow, and the server
  trusted it without checking whether the player held one.
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
