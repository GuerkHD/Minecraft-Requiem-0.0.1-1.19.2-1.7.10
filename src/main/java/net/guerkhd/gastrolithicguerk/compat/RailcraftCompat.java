package net.guerkhd.gastrolithicguerk.compat;

import net.minecraft.item.ItemStack;

import java.lang.reflect.Method;

/**
 * Railcraft: Rock Crusher recipe. Railcraft has no IMC for this, so its API is called through reflection.
 */
class RailcraftCompat
{
    static void init() throws Exception
    {
        Object rockCrusher = Class.forName("mods.railcraft.api.crafting.RailcraftCraftingManager").getField("rockCrusher").get(null);
        if(rockCrusher == null) throw new IllegalStateException("Railcraft rock crusher recipes are not available");

        Method createNewRecipe = Class.forName("mods.railcraft.api.crafting.IRockCrusherCraftingManager")
                .getMethod("createNewRecipe", ItemStack.class, boolean.class, boolean.class);
        Method addOutput = Class.forName("mods.railcraft.api.crafting.IRockCrusherRecipe").getMethod("addOutput", ItemStack.class, float.class);

        Object recipe = createNewRecipe.invoke(rockCrusher, ModCompat.ore(), true, false);
        addOutput.invoke(recipe, ModCompat.rawGuerk(2), 1.0f);
    }
}
