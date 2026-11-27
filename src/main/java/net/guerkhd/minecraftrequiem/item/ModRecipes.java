package net.guerkhd.minecraftrequiem.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.minecraftrequiem.block.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * The 1.19 data pack recipes. Blasting does not exist in 1.7.10, so only the furnace recipes remain.
 * The enchanted golden apple recipe and the Edible Block of Gold were dropped: 1.7.10 still has the cheaper vanilla
 * recipe (8 gold blocks + apple).
 */
public class ModRecipes
{
    public static void register()
    {
        ItemStack enchantedGoldenApple = new ItemStack(Items.golden_apple, 1, 1);

        GameRegistry.addRecipe(new ItemStack(ModBlocks.GUERK_BLOCK), "###", "###", "###", '#', ModItems.GUERK_INGOT);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.GUERK_INGOT, 9), ModBlocks.GUERK_BLOCK);
        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_INGOT), "###", "###", "###", '#', ModItems.GUERK_NUGGET);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.GUERK_NUGGET, 9), ModItems.GUERK_INGOT);

        GameRegistry.addRecipe(new ItemStack(ModBlocks.GOLD_INFUSED_OBSIDIAN), "###", "#X#", "###", '#', Items.gold_ingot, 'X', Blocks.obsidian);

        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_SWORD), "X", "X", "#", 'X', ModItems.GUERK_INGOT, '#', Items.stick);
        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_PICKAXE), "XXX", " # ", " # ", 'X', ModItems.GUERK_INGOT, '#', Items.stick);
        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_AXE), "XX", "X#", " #", 'X', ModItems.GUERK_INGOT, '#', Items.stick);
        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_SHOVEL), "X", "#", "#", 'X', ModItems.GUERK_INGOT, '#', Items.stick);
        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_HOE), "XX", " #", " #", 'X', ModItems.GUERK_INGOT, '#', Items.stick);

        GameRegistry.addRecipe(new ItemStack(ModItems.STAND_ARROW_TIP), " X ", "X#X", "XXX", 'X', ModBlocks.GOLD_INFUSED_OBSIDIAN, '#', Items.nether_star);
        GameRegistry.addRecipe(new ItemStack(ModItems.REQUIEM_ARROW_TIP), " X ", "X#X", "XYX", 'X', ModBlocks.GOLD_INFUSED_OBSIDIAN, '#', Items.nether_star, 'Y', enchantedGoldenApple.copy());
        GameRegistry.addRecipe(new ItemStack(ModItems.STAND_ARROW), "X", "#", "Y", 'X', ModItems.STAND_ARROW_TIP, '#', Items.stick, 'Y', Items.feather);
        GameRegistry.addRecipe(new ItemStack(ModItems.REQUIEM_ARROW), "X", "#", "Y", 'X', ModItems.REQUIEM_ARROW_TIP, '#', Items.stick, 'Y', Items.feather);

        GameRegistry.addSmelting(ModItems.RAW_GUERK, new ItemStack(ModItems.GUERK_INGOT), 0.7f);
        GameRegistry.addSmelting(ModBlocks.GUERK_ORE, new ItemStack(ModItems.GUERK_INGOT), 0.7f);
    }
}
