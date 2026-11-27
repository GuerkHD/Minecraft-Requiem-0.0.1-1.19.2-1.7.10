package net.guerkhd.minecraftrequiem.sound;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;

/**
 * 1.7.10 has no sound registry: sounds are referenced by name and declared in assets/minecraftrequiem/sounds.json.
 */
public class ModSounds
{
    public static final String ZA_WARUDO = sound("za_warudo");
    public static final String C_MOON = sound("c_moon");
    public static final String STAND_SUMMON = sound("stand_summon");
    public static final String STAND_UNSUMMON = sound("stand_unsummon");
    public static final String KING_CRIMSON = sound("king_crimson");
    public static final String KILLER_QUEEN = sound("killer_queen");
    public static final String THREE_FREEZE = sound("three_freeze");
    public static final String GREEN_DAY = sound("green_day");

    // Vanilla 1.7.10 sounds. Some 1.19 sounds don't exist yet and were replaced by the closest fit.
    public static final String PLAYER_BURP = "random.burp";
    public static final String D4C = "portal.trigger";             // 1.19: Warden emerge
    public static final String CHICKEN_DEATH = "mob.chicken.hurt";
    public static final String WEATHER_REPORT = "ambient.weather.thunder"; // 1.19: Beacon activate
    public static final String WITHER_DEATH = "mob.wither.death";
    public static final String WITHER_SPAWN = "mob.wither.spawn";
    public static final String CREEPER_PRIMED = "creeper.primed";
    public static final String CHORUS_FRUIT_TELEPORT = "mob.endermen.portal";

    private static String sound(String name)
    {
        return MinecraftRequiem.MOD_ID + ":" + name;
    }
}
