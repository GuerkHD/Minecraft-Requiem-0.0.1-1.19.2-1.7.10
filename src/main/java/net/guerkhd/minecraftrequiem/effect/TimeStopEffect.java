package net.guerkhd.minecraftrequiem.effect;

import net.minecraft.entity.EntityLivingBase;

public class TimeStopEffect extends ModPotion
{
    public TimeStopEffect(int id, boolean isBadEffect, int color)
    {
        super(id, isBadEffect, color, "time_stop");
    }

    @Override
    public void livingTick(EntityLivingBase plivingEntity, int pAmplifier)
    {
        if(!plivingEntity.worldObj.isRemote)
        {
            plivingEntity.setPositionAndUpdate(plivingEntity.posX, plivingEntity.posY, plivingEntity.posZ);
            plivingEntity.motionX = 0;
            plivingEntity.motionY = 0;
            plivingEntity.motionZ = 0;
        }
    }
}
