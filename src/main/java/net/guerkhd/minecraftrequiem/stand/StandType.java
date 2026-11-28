package net.guerkhd.minecraftrequiem.stand;

import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.minecraft.util.EnumChatFormatting;

/**
 * The stand IDs, shared by server and client (the 1.19 version had one copy of this enum per packet).
 * The ordinal is the stand ID that is saved and synced, so the order must not change.
 */
public enum StandType
{
    //                 HUD name          color
    THE_WORLD(        "The World",      EnumChatFormatting.GOLD),
    D4C(              "D4C",            EnumChatFormatting.BLUE),
    MAGICIANS_RED(    "Magicians Red",  EnumChatFormatting.RED),
    C_MOON(           "C-Moon",         EnumChatFormatting.DARK_GREEN),
    WEATHER_REPORT(   "Weather Report", EnumChatFormatting.WHITE),
    ECHOS(            "Echos Act 3",    EnumChatFormatting.GREEN),
    HIGHWAY_TO_HELL(  "Highway To Hell",EnumChatFormatting.DARK_PURPLE), // no stand entity
    KILLER_QUEEN(     "Killer Queen",   EnumChatFormatting.LIGHT_PURPLE),
    KING_CRIMSON(     "King Crimson",   EnumChatFormatting.DARK_RED),
    GREEN_DAY(        "Green Day",      EnumChatFormatting.DARK_GREEN),
    UNKNOWN(          "unknown",        EnumChatFormatting.GRAY);

    public static final int STAND_COUNT = 10;

    private final String hudName;
    private final EnumChatFormatting color;

    StandType(String hudName, EnumChatFormatting color)
    {
        this.hudName = hudName;
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

    public EnumChatFormatting getColor()
    {
        return color;
    }

    /**
     * The stand's skin, in the 64x64 player skin layout.
     */
    public String getSkinPath()
    {
        return "textures/entity/stand/" + name().toLowerCase() + ".png";
    }

    /**
     * The food costs from the config, indexed by stand ID. The server sends its table to every client that joins
     * (StandSettingsS2CPacket), so the HUD shows the server's costs and not the client's own config.
     */
    public static int[] configCosts()
    {
        int[] costs = new int[STAND_COUNT];
        costs[THE_WORLD.ordinal()] = MinecraftRequiemCommonConfig.THE_WORLD_COST;
        costs[D4C.ordinal()] = MinecraftRequiemCommonConfig.D4C_COST;
        costs[MAGICIANS_RED.ordinal()] = MinecraftRequiemCommonConfig.MAGICIANS_RED_COST;
        costs[C_MOON.ordinal()] = MinecraftRequiemCommonConfig.C_MOON_COST;
        costs[WEATHER_REPORT.ordinal()] = MinecraftRequiemCommonConfig.WEATHER_REPORT_COST;
        costs[ECHOS.ordinal()] = MinecraftRequiemCommonConfig.ECHOS_ACT_3_COST;
        costs[HIGHWAY_TO_HELL.ordinal()] = 0; // costs health, not food
        costs[KILLER_QUEEN.ordinal()] = MinecraftRequiemCommonConfig.KILLER_QUEEN_COST;
        costs[KING_CRIMSON.ordinal()] = MinecraftRequiemCommonConfig.KING_CRIMSON_COST;
        costs[GREEN_DAY.ordinal()] = MinecraftRequiemCommonConfig.GREEN_DAY_COST;
        return costs;
    }

    /**
     * Food the ability key costs right now, 0 for none. D4C only crosses over outside the overworld;
     * in the overworld it teleports at The World's price (StandAbilities).
     */
    public int getCost(int[] costs, int dimension)
    {
        if(this == UNKNOWN || costs == null || costs.length < STAND_COUNT) return 0;
        if(this == D4C && dimension == 0) return costs[THE_WORLD.ordinal()];
        return costs[ordinal()];
    }

    /**
     * Highway To Hell has no visible stand.
     */
    public boolean hasStandEntity()
    {
        return this != HIGHWAY_TO_HELL && this != UNKNOWN;
    }
}
