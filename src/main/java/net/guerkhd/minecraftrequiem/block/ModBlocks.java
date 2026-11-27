package net.guerkhd.minecraftrequiem.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.food.FoodProperties;
import net.minecraft.block.Block;
import net.minecraft.potion.Potion;
import net.minecraftforge.oredict.OreDictionary;

public class ModBlocks
{
    public static Block GUERK_BLOCK;
    public static Block GUERK_ORE;
    public static Block GOLD_INFUSED_OBSIDIAN;

    public static void register()
    {
        GUERK_BLOCK = registerBlock("guerk_block", new ModBlock("guerk_block", 6f, 1, Foods.GUERK_BLOCK));
        GUERK_ORE = registerBlock("guerk_ore", new GuerkOreBlock("guerk_ore", 6f, 1, Foods.GUERK_ORE));
        GOLD_INFUSED_OBSIDIAN = registerBlock("gold_infused_obsidian", new ModBlock("gold_infused_obsidian", 30f, 3, null));

        // 1.19 forge tags -> ore dictionary
        OreDictionary.registerOre("blockGuerk", GUERK_BLOCK);
        OreDictionary.registerOre("oreGuerk", GUERK_ORE);
    }

    private static Block registerBlock(String name, Block block)
    {
        GameRegistry.registerBlock(block, EdibleBlockItem.class, name);
        return block;
    }

    public static class Foods
    {
        public static final FoodProperties GUERK_BLOCK = new FoodProperties.Builder()
                .nutrition(20)
                .saturationMod(1.8f)
                .effect(() -> ModEffects.CONSTIPATION, 300, 0, 0.8f)
                .build();

        public static final FoodProperties GUERK_ORE = new FoodProperties.Builder()
                .nutrition(2)
                .saturationMod(0.6f)
                .effect(() -> Potion.moveSlowdown, 100, 1, 1)
                .build();
    }
}
