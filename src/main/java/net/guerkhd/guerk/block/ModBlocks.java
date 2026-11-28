package net.guerkhd.guerk.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.guerk.effect.ModEffects;
import net.guerkhd.guerk.food.FoodProperties;
import net.minecraft.block.Block;
import net.minecraft.potion.Potion;
import net.minecraftforge.oredict.OreDictionary;

public class ModBlocks
{
    public static Block GUERK_BLOCK;
    public static Block GUERK_ORE;

    public static void register()
    {
        GUERK_BLOCK = registerBlock("guerk_block", new ModBlock("guerk_block", 6f, 1, Foods.GUERK_BLOCK));
        // Like the metal ores in 1.7.10 (iron, gold, copper, ...) the ore drops itself; crushers turn it into Raw Guerk.
        GUERK_ORE = registerBlock("guerk_ore", new ModBlock("guerk_ore", 6f, 1, Foods.GUERK_ORE));

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
