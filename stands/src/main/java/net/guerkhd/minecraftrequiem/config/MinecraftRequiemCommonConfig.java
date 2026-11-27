package net.guerkhd.minecraftrequiem.config;

import net.guerkhd.minecraftrequiem.util.ModExplosion;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class MinecraftRequiemCommonConfig
{
    private static final String COSTS = "stand_ability_costs";
    private static final String KILLER_QUEEN = "killer_queen_bombs";
    private static final String POTIONS = "potion_ids";

    private static final String[] BLOCK_INTERACTIONS = {"NONE", "BREAK", "DESTROY"};

    public static int THE_WORLD_COST = 6;
    public static int D4C_COST = 17;
    public static int MAGICIANS_RED_COST = 5;
    public static int C_MOON_COST = 7;
    public static int WEATHER_REPORT_COST = 6;
    public static int ECHOS_ACT_3_COST = 12;
    public static int KILLER_QUEEN_COST = 7;
    public static int KING_CRIMSON_COST = 9;
    public static int GREEN_DAY_COST = 12;

    public static ModExplosion.BlockInteraction BOMB_DESTRUCTION = ModExplosion.BlockInteraction.NONE;
    public static ModExplosion.BlockInteraction HEART_ATTACK_DESTRUCTION = ModExplosion.BlockInteraction.BREAK;

    // Minecraft 1.7.10 has no potion registry, only a fixed ID array. Every mod has to pick its own IDs.
    public static int TIME_STOP_ID = 70;
    public static int BOMB_ID = 71;
    public static int GREEN_DAY_ID = 72;
    public static int THREE_FREEZE_ID = 73;
    public static int EPITAPH_ID = 74;
    public static int LEVITATION_ID = 76;

    public static void load(File file)
    {
        Configuration config = new Configuration(file);
        config.load();

        config.setCategoryComment(COSTS, "Stand ability costs");
        THE_WORLD_COST = config.getInt("theWorldCost", COSTS, 6, 0, 20, "How much food the The World teleport costs.");
        D4C_COST = config.getInt("d4cCost", COSTS, 17, 0, 20, "How much food D4Cs dimensional teleport costs.");
        MAGICIANS_RED_COST = config.getInt("fireballCost", COSTS, 5, 0, 20, "How much food Magicians Reds fireball costs.");
        C_MOON_COST = config.getInt("cMoonCost", COSTS, 7, 0, 20, "How much food C-Moons gravity reversal costs.");
        WEATHER_REPORT_COST = config.getInt("weatherReportCost", COSTS, 6, 0, 20, "How much food Weather Reports weather control costs.");
        ECHOS_ACT_3_COST = config.getInt("act3Cost", COSTS, 12, 0, 20, "How much food Echos weight increase costs.");
        KILLER_QUEEN_COST = config.getInt("bombCost", COSTS, 7, 0, 20, "How much food Killer Queens bomb costs.");
        KING_CRIMSON_COST = config.getInt("epitaphCost", COSTS, 9, 0, 20, "How much food King Crimsons precognition costs.");
        GREEN_DAY_COST = config.getInt("greenDayCost", COSTS, 12, 0, 20, "How much food Green Days fungus costs.");

        config.setCategoryComment(KILLER_QUEEN, "Killer Queens bombs blockinteraction");
        BOMB_DESTRUCTION = blockInteraction(config.getString("firstBombBlockDamage", KILLER_QUEEN, "NONE",
                "If Killer Queens bomb does block damage. NONE = no block damage, BREAK = blocks drop as items, DESTROY = blocks drop like TNT.",
                BLOCK_INTERACTIONS), ModExplosion.BlockInteraction.NONE);
        HEART_ATTACK_DESTRUCTION = blockInteraction(config.getString("secondBombBlockDamage", KILLER_QUEEN, "BREAK",
                "If Killer Queens Sheer Heart Attack does block damage. NONE = no block damage, BREAK = blocks drop as items, DESTROY = blocks drop like TNT.",
                BLOCK_INTERACTIONS), ModExplosion.BlockInteraction.BREAK);

        config.setCategoryComment(POTIONS, "Potion IDs for the custom effects. Must be below 128 and identical on client and server.\n"
                + "If an ID is already taken by another mod, the next free ID is used and a warning is logged.");
        TIME_STOP_ID = config.getInt("timeStop", POTIONS, 70, 24, 127, "");
        BOMB_ID = config.getInt("bomb", POTIONS, 71, 24, 127, "");
        GREEN_DAY_ID = config.getInt("greenDay", POTIONS, 72, 24, 127, "");
        THREE_FREEZE_ID = config.getInt("threeFreeze", POTIONS, 73, 24, 127, "");
        EPITAPH_ID = config.getInt("epitaph", POTIONS, 74, 24, 127, "");
        LEVITATION_ID = config.getInt("levitation", POTIONS, 76, 24, 127,
                "Levitation does not exist in 1.7.10, so C-Moon brings its own.");

        if(config.hasChanged()) config.save();
    }

    private static ModExplosion.BlockInteraction blockInteraction(String value, ModExplosion.BlockInteraction fallback)
    {
        try
        {
            return ModExplosion.BlockInteraction.valueOf(value.trim().toUpperCase());
        }
        catch(IllegalArgumentException e)
        {
            return fallback;
        }
    }
}
