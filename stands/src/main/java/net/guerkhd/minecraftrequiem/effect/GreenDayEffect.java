package net.guerkhd.minecraftrequiem.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Poisons players who went below the height they had when Green Day hit them; the amplifier grows with the depth
 * (ModTickEvents). The poison is renewed in short pieces, so it stops soon after going back up.
 */
public class GreenDayEffect extends ModPotion
{
    private static final int POISON_DURATION = 30;

    public GreenDayEffect(int id, boolean isBadEffect, int color)
    {
        super(id, isBadEffect, color, "green_day");
    }

    @Override
    public void livingTick(EntityLivingBase plivingEntity, int pAmplifier)
    {
        if(!plivingEntity.worldObj.isRemote)
        {
            PotionEffect poison = plivingEntity.getActivePotionEffect(Potion.poison);

            if(pAmplifier > 0)
            {
                // Also renew right away when going deeper, not only after the weaker poison ran out (1.19).
                if(poison == null || poison.getAmplifier() < pAmplifier - 1)
                {
                    plivingEntity.addPotionEffect(new PotionEffect(Potion.poison.id, POISON_DURATION, pAmplifier - 1));
                }
            }
            // Back up: end Green Day's poison, but not a longer one from somewhere else (1.19 removed any poison).
            else if(poison != null && poison.getDuration() <= POISON_DURATION)
            {
                plivingEntity.removePotionEffect(Potion.poison.id);
            }
        }
    }
}
