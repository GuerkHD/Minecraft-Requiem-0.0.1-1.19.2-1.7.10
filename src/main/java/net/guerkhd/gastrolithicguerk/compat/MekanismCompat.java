package net.guerkhd.gastrolithicguerk.compat;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Mekanism: Enrichment Chamber (ore -> 2 Raw Guerk) and Crusher (ingot -> Raw Guerk), via IMC.
 * Mekanism only handles its own metals automatically, so the recipes have to be sent.
 */
class MekanismCompat
{
    static void init()
    {
        sendRecipe("EnrichmentChamberRecipe", ModCompat.ore(), ModCompat.rawGuerk(2));
        sendRecipe("CrusherRecipe", ModCompat.ingot(), ModCompat.rawGuerk(1));
    }

    private static void sendRecipe(String type, ItemStack input, ItemStack output)
    {
        NBTTagCompound recipe = new NBTTagCompound();
        recipe.setTag("input", ModCompat.nbt(input));
        recipe.setTag("output", ModCompat.nbt(output));
        FMLInterModComms.sendMessage(ModCompat.MEKANISM, type, recipe);
    }
}
