package net.guerkhd.minecraftrequiem.stand;

import net.minecraft.util.EnumChatFormatting;

/**
 * The stand IDs, shared by server and client (the 1.19 version had one copy of this enum per packet).
 * The ordinal is the stand ID that is saved and synced, so the order must not change.
 */
public enum StandType
{
    //                 HUD name          name in chat when awakened      color
    THE_WORLD(        "The World",      "The World",                    EnumChatFormatting.GOLD),
    D4C(              "D4C",            "Dirty Deeds Done Dirt Cheap",  EnumChatFormatting.BLUE),
    MAGICIANS_RED(    "Magicians Red",  "Magicians Red",                EnumChatFormatting.RED),
    C_MOON(           "C-Moon",         "C-Moon",                       EnumChatFormatting.DARK_GREEN),
    WEATHER_REPORT(   "Weather Report", "Weather Report",               EnumChatFormatting.WHITE),
    ECHOS(            "Echos Act 3",    "Echos Act 3",                  EnumChatFormatting.GREEN),
    HIGHWAY_TO_HELL(  "Highway To Hell","Highway To Hell",              EnumChatFormatting.DARK_PURPLE), // no stand entity
    KILLER_QUEEN(     "Killer Queen",   "Killer Queen",                 EnumChatFormatting.LIGHT_PURPLE),
    KING_CRIMSON(     "King Crimson",   "King Crimson",                 EnumChatFormatting.DARK_RED),
    GREEN_DAY(        "Green Day",      "Green Day",                    EnumChatFormatting.DARK_GREEN),
    UNKNOWN(          "unknown",        "unknown",                      EnumChatFormatting.GRAY);

    public static final int STAND_COUNT = 10;

    private final String hudName;
    private final String awakeningName;
    private final EnumChatFormatting color;

    StandType(String hudName, String awakeningName, EnumChatFormatting color)
    {
        this.hudName = hudName;
        this.awakeningName = awakeningName;
        this.color = color;
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

    /**
     * File name of the stand's skin: textures/entity/stand/<name>.png
     */
    public String getTextureName()
    {
        return name().toLowerCase();
    }

    /**
     * Highway To Hell has no visible stand.
     */
    public boolean hasStandEntity()
    {
        return this != HIGHWAY_TO_HELL && this != UNKNOWN;
    }
}
