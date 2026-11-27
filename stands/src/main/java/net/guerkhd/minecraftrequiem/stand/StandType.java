package net.guerkhd.minecraftrequiem.stand;

import net.minecraft.util.EnumChatFormatting;

/**
 * The stand IDs, shared by server and client (the 1.19 version had one copy of this enum per packet).
 * The ordinal is the stand ID that is saved and synced, so the order must not change.
 */
public enum StandType
{
    //                 HUD name          name in chat when awakened      color                          armor color (1.19 dye colors)
    THE_WORLD(        "The World",      "The World",                    EnumChatFormatting.GOLD,         0xFED83D), // yellow
    D4C(              "D4C",            "Dirty Deeds Done Dirt Cheap",  EnumChatFormatting.BLUE,         0x3AB3DA), // light blue
    MAGICIANS_RED(    "Magicians Red",  "Magicians Red",                EnumChatFormatting.RED,          0xF9801D), // orange
    C_MOON(           "C-Moon",         "C-Moon",                       EnumChatFormatting.DARK_GREEN,   0x5E7C16), // green
    WEATHER_REPORT(   "Weather Report", "Weather Report",               EnumChatFormatting.WHITE,        0xF9FFFE), // white
    ECHOS(            "Echos Act 3",    "Echos Act 3",                  EnumChatFormatting.GREEN,        0x80C71F), // lime
    HIGHWAY_TO_HELL(  "Highway To Hell","Highway To Hell",              EnumChatFormatting.DARK_PURPLE,  0x474F52), // gray (no stand entity)
    KILLER_QUEEN(     "Killer Queen",   "Killer Queen",                 EnumChatFormatting.LIGHT_PURPLE, 0xF38BAA), // pink
    KING_CRIMSON(     "King Crimson",   "King Crimson",                 EnumChatFormatting.DARK_RED,     0xB02E26), // red
    GREEN_DAY(        "Green Day",      "Green Day",                    EnumChatFormatting.DARK_GREEN,   0x5E7C16), // green
    UNKNOWN(          "unknown",        "unknown",                      EnumChatFormatting.GRAY,         0x474F52); // gray

    public static final int STAND_COUNT = 10;

    private final String hudName;
    private final String awakeningName;
    private final EnumChatFormatting color;
    private final int armorColor;

    StandType(String hudName, String awakeningName, EnumChatFormatting color, int armorColor)
    {
        this.hudName = hudName;
        this.awakeningName = awakeningName;
        this.color = color;
        this.armorColor = armorColor;
    }

    public static StandType byId(int id)
    {
        return id >= 0 && id < STAND_COUNT ? values()[id] : UNKNOWN;
    }

    public String getHudName()
    {
        return hudName;
    }

    public String getAwakeningName()
    {
        return awakeningName;
    }

    public EnumChatFormatting getColor()
    {
        return color;
    }

    public int getArmorColor()
    {
        return armorColor;
    }

    /**
     * Highway To Hell has no visible stand.
     */
    public boolean hasStandEntity()
    {
        return this != HIGHWAY_TO_HELL && this != UNKNOWN;
    }
}
