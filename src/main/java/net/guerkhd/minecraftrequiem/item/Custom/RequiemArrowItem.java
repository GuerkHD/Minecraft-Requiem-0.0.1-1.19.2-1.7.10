package net.guerkhd.minecraftrequiem.item.Custom;

import net.guerkhd.minecraftrequiem.stand.StandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RequiemArrowItem extends Item
{
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World level, EntityPlayer player)
    {
        if(!level.isRemote && player instanceof EntityPlayerMP) StandManager.giveStand((EntityPlayerMP) player);
        return stack;
    }
}
