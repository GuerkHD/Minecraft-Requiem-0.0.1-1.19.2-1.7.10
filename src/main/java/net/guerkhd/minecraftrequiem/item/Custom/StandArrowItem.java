package net.guerkhd.minecraftrequiem.item.Custom;

import net.guerkhd.minecraftrequiem.stand.PlayerStand;
import net.guerkhd.minecraftrequiem.stand.StandManager;
import net.guerkhd.minecraftrequiem.util.ExperienceHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class StandArrowItem extends Item
{
    public static final int REQUIRED_LEVEL = 30;

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World level, EntityPlayer player)
    {
        if(!level.isRemote && player instanceof EntityPlayerMP)
        {
            PlayerStand stand = PlayerStand.get(player);

            if(stand != null && !stand.getStandUser())
            {
                if(player.experienceLevel >= REQUIRED_LEVEL)
                {
                    // 1.19 removed 1395 XP points, the amount needed for level 30. The 1.7.10 curve needs 825 for that.
                    ExperienceHelper.removeExperiencePoints((EntityPlayerMP) player, ExperienceHelper.getExperienceForLevel(REQUIRED_LEVEL));
                    StandManager.giveStand((EntityPlayerMP) player);
                }
                else player.attackEntityFrom(DamageSource.magic, Float.MAX_VALUE);
            }
        }
        return stack;
    }
}
