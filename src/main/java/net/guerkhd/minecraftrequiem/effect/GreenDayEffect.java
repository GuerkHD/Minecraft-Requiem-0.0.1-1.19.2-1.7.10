package net.guerkhd.minecraftrequiem.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class GreenDayEffect extends ModPotion
{
    public GreenDayEffect(int id, boolean isBadEffect, int color)
    {
        super(id, isBadEffect, color, "green_day");
    }

    @Override
    public void livingTick(EntityLivingBase plivingEntity, int pAmplifier)
    {
        if(!plivingEntity.worldObj.isRemote)
        {
            if(pAmplifier > 0 && !plivingEntity.isPotionActive(Potion.poison)) plivingEntity.addPotionEffect(new PotionEffect(Potion.poison.id, 30, pAmplifier - 1));
            else if(pAmplifier == 0 && plivingEntity.isPotionActive(Potion.poison)) plivingEntity.removePotionEffect(Potion.poison.id);
        }
    }
}
