package net.guerkhd.minecraftrequiem.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.minecraftrequiem.block.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * The 1.19 data pack recipes for the stand arrows.
 */
public class ModRecipes
{
    public static void register()
    {
        ItemStack enchantedGoldenApple = new ItemStack(Items.golden_apple, 1, 1);

        GameRegistry.addRecipe(new ItemStack(ModBlocks.GOLD_INFUSED_OBSIDIAN), "###", "#X#", "###", '#', Items.gold_ingot, 'X', Blocks.obsidian);

        GameRegistry.addRecipe(new ItemStack(ModItems.STAND_ARROW_TIP), " X ", "X#X", "XXX", 'X', ModBlocks.GOLD_INFUSED_OBSIDIAN, '#', Items.nether_star);
        GameRegistry.addRecipe(new ItemStack(ModItems.REQUIEM_ARROW_TIP), " X ", "X#X", "XYX", 'X', ModBlocks.GOLD_INFUSED_OBSIDIAN, '#', Items.nether_star, 'Y', enchantedGoldenApple.copy());
        GameRegistry.addRecipe(new ItemStack(ModItems.STAND_ARROW), "X", "#", "Y", 'X', ModItems.STAND_ARROW_TIP, '#', Items.stick, 'Y', Items.feather);
        GameRegistry.addRecipe(new ItemStack(ModItems.REQUIEM_ARROW), "X", "#", "Y", 'X', ModItems.REQUIEM_ARROW_TIP, '#', Items.stick, 'Y', Items.feather);
    }
}
