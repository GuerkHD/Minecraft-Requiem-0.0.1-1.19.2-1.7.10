package net.guerkhd.minecraftrequiem.item.Custom;

import net.guerkhd.minecraftrequiem.stand.StandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Made from a Requiem Arrow in a furnace with a Milk Bucket as fuel (see RemoverArrowFurnace).
 * Used up when it removes a stand; otherwise a Requiem Arrow and one Remover Arrow would allow endless free rerolls.
 */
public class StandRemoveArrowItem extends Item
{
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World level, EntityPlayer player)
    {
        if(!level.isRemote && player instanceof EntityPlayerMP && StandManager.removeStand((EntityPlayerMP) player)
                && !player.capabilities.isCreativeMode)
        {
            --stack.stackSize;
        }
        return stack;
    }
}
