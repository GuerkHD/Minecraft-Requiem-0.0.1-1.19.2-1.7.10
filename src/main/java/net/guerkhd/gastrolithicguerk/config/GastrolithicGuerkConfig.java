package net.guerkhd.gastrolithicguerk.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class GastrolithicGuerkConfig
{
    private static final String ORE = "guerk_ore_spawn";
    private static final String POTIONS = "potion_ids";
    private static final String COMPAT = "mod_compatibility";

    public static int GUERK_ORE_VEINS_PER_CHUNK = 5;
    public static int GUERK_ORE_VEIN_SIZE = 5;

    // Minecraft 1.7.10 has no potion registry, only a fixed ID array. Every mod has to pick its own IDs.
    public static int CONSTIPATION_ID = 75;

    public static int TINKERS_MATERIAL_ID = 218;

    public static void load(File file)
    {
        Configuration config = new Configuration(file);
        config.load();

        config.setCategoryComment(ORE, "Guerk Ore spawn");
        GUERK_ORE_VEINS_PER_CHUNK = config.getInt("veinsPerChunk", ORE, 5, 1, 15, "How many Guerk Ore veins spawn per chunk.");
        GUERK_ORE_VEIN_SIZE = config.getInt("veinSize", ORE, 5, 1, 15, "How many Guerk Ore blocks spawn per vein.");

        config.setCategoryComment(POTIONS, "Potion IDs for the custom effects. Must be below 128 and identical on client and server.\n"
                + "If an ID is already taken by another mod, the next free ID is used and a warning is logged.");
        CONSTIPATION_ID = config.getInt("constipation", POTIONS, 75, 24, 127, "");

        config.setCategoryComment(COMPAT, "Settings for other mods");
        TINKERS_MATERIAL_ID = config.getInt("tinkersMaterialId", COMPAT, 218, 0, 32767,
                "Tinkers' Construct material ID for Guerk. Must be unique among all Tinkers materials and identical on client and server.");

        if(config.hasChanged()) config.save();
    }
}
