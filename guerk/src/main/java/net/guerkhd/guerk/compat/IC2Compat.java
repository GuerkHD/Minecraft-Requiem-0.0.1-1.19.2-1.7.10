package net.guerkhd.guerk.compat;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * IndustrialCraft 2: Macerator recipes. IC2 has no IMC for this, so its API is called through reflection.
 */
class IC2Compat
{
    static void init() throws Exception
    {
        Object macerator = Class.forName("ic2.api.recipe.Recipes").getField("macerator").get(null);
        if(macerator == null) throw new IllegalStateException("IC2 macerator recipes are not available");

        Class<?> recipeInput = Class.forName("ic2.api.recipe.IRecipeInput");
        Constructor<?> oreDictInput = Class.forName("ic2.api.recipe.RecipeInputOreDict").getConstructor(String.class, int.class);
        Method addRecipe = Class.forName("ic2.api.recipe.IMachineRecipeManager").getMethod("addRecipe", recipeInput, NBTTagCompound.class, ItemStack[].class);

        addRecipe.invoke(macerator, oreDictInput.newInstance("oreGuerk", 1), null, new ItemStack[]{ModCompat.rawGuerk(2)});
        addRecipe.invoke(macerator, oreDictInput.newInstance("ingotGuerk", 1), null, new ItemStack[]{ModCompat.rawGuerk(1)});
    }
}
