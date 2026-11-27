package net.guerkhd.minecraftrequiem.compat;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Applied Energistics 2: Quartz Grindstone, via IMC. Same numbers AE2 uses for iron and gold ore
 * (1 dust plus a 90 % chance of a second one).
 */
class AE2Compat
{
    static void init()
    {
        sendGrindable(ModCompat.ore(), ModCompat.rawGuerk(1), ModCompat.rawGuerk(1), 0.9f, 8);
        sendGrindable(ModCompat.ingot(), ModCompat.rawGuerk(1), null, 0, 4);
    }

    private static void sendGrindable(ItemStack input, ItemStack output, ItemStack optional, float chance, int turns)
    {
        NBTTagCompound recipe = new NBTTagCompound();
        recipe.setTag("in", ModCompat.nbt(input));
        recipe.setTag("out", ModCompat.nbt(output));
        if(optional != null)
        {
            recipe.setTag("optional", ModCompat.nbt(optional));
            recipe.setFloat("chance", chance);
        }
        recipe.setInteger("turns", turns);
        FMLInterModComms.sendMessage(ModCompat.AE2, "add-grindable", recipe);
    }
}
