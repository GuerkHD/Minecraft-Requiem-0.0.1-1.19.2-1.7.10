package net.guerkhd.minecraftrequiem.effect;

import net.minecraft.entity.EntityLivingBase;

import java.util.Map;
import java.util.WeakHashMap;

public class TimeStopEffect extends ModPotion
{
    /**
     * Where each frozen entity is held: x, y, z and the world time of the last frozen tick. Server only.
     * 1.19 teleported entities to their current position every tick, so they still crept along at about half speed.
     */
    private static final Map<EntityLivingBase, double[]> FROZEN = new WeakHashMap<EntityLivingBase, double[]>();

    public TimeStopEffect(int id, boolean isBadEffect, int color)
    {
        super(id, isBadEffect, color, "time_stop");
    }

    @Override
    public void livingTick(EntityLivingBase plivingEntity, int pAmplifier)
    {
        if(!plivingEntity.worldObj.isRemote)
        {
            long now = plivingEntity.worldObj.getTotalWorldTime();
            double[] frozen = FROZEN.get(plivingEntity);

            // A new freeze (not one going on since the last tick) starts where the entity is now.
            if(frozen == null || now - (long) frozen[3] > 1)
            {
                frozen = new double[] {plivingEntity.posX, plivingEntity.posY, plivingEntity.posZ, 0};
                FROZEN.put(plivingEntity, frozen);
            }
            frozen[3] = now;

            plivingEntity.setPositionAndUpdate(frozen[0], frozen[1], frozen[2]);
            plivingEntity.motionX = 0;
            plivingEntity.motionY = 0;
            plivingEntity.motionZ = 0;
            // Hanging in the air must not count as falling, nor as flying.
            plivingEntity.fallDistance = 0;
            allowFloating(plivingEntity);
        }
    }
}
