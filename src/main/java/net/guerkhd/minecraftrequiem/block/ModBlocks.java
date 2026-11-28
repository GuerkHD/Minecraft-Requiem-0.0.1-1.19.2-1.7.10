package net.guerkhd.minecraftrequiem.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class ModBlocks
{
    /** Only used for the arrow tips. */
    public static Block GOLD_INFUSED_OBSIDIAN;

    public static void register()
    {
        GOLD_INFUSED_OBSIDIAN = new ModBlock("gold_infused_obsidian", 30f, 3);
        GameRegistry.registerBlock(GOLD_INFUSED_OBSIDIAN, "gold_infused_obsidian");
    }
}
