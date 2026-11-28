package net.guerkhd.gastrolithicguerk.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.gastrolithicguerk.block.ModBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * The 1.19 data pack recipes. Blasting does not exist in 1.7.10, so only the furnace recipes remain.
 */
public class ModRecipes
{
    public static void register()
    {
        GameRegistry.addRecipe(new ItemStack(ModBlocks.GUERK_BLOCK), "###", "###", "###", '#', ModItems.GUERK_INGOT);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.GUERK_INGOT, 9), ModBlocks.GUERK_BLOCK);
        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_INGOT), "###", "###", "###", '#', ModItems.GUERK_NUGGET);
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.GUERK_NUGGET, 9), ModItems.GUERK_INGOT);

        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_SWORD), "X", "X", "#", 'X', ModItems.GUERK_INGOT, '#', Items.stick);
        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_PICKAXE), "XXX", " # ", " # ", 'X', ModItems.GUERK_INGOT, '#', Items.stick);
        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_AXE), "XX", "X#", " #", 'X', ModItems.GUERK_INGOT, '#', Items.stick);
        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_SHOVEL), "X", "#", "#", 'X', ModItems.GUERK_INGOT, '#', Items.stick);
        GameRegistry.addRecipe(new ItemStack(ModItems.GUERK_HOE), "XX", " #", " #", 'X', ModItems.GUERK_INGOT, '#', Items.stick);

        GameRegistry.addSmelting(ModItems.RAW_GUERK, new ItemStack(ModItems.GUERK_INGOT), 0.7f);
        GameRegistry.addSmelting(ModBlocks.GUERK_ORE, new ItemStack(ModItems.GUERK_INGOT), 0.7f);
    }
}
