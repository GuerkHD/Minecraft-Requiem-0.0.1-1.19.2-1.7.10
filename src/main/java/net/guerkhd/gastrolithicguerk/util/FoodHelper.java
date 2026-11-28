package net.guerkhd.gastrolithicguerk.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;

public class FoodHelper
{
    /**
     * FoodStats.setFoodLevel is client-only in 1.7.10, so go through the NBT data instead (keeps saturation as is).
     */
    public static void setFoodLevel(EntityPlayerMP player, int foodLevel)
    {
        FoodStats foodStats = player.getFoodStats();
        NBTTagCompound nbt = new NBTTagCompound();
        foodStats.writeNBT(nbt);
        nbt.setInteger("foodLevel", Math.max(0, Math.min(20, foodLevel)));
        foodStats.readNBT(nbt);
    }
}
