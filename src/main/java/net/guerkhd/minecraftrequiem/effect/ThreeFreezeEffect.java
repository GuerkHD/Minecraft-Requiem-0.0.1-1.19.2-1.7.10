package net.guerkhd.minecraftrequiem.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class ThreeFreezeEffect extends ModPotion
{
    public ThreeFreezeEffect(int id, boolean isBadEffect, int color)
    {
        super(id, isBadEffect, color, "three_freeze");
    }

    @Override
    public void livingTick(EntityLivingBase plivingEntity, int pAmplifier)
    {
        if(!plivingEntity.worldObj.isRemote)
        {
            plivingEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 6, true));
        }
    }
}
