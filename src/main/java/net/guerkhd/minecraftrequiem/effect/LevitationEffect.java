package net.guerkhd.minecraftrequiem.effect;

import net.minecraft.entity.EntityLivingBase;

/**
 * Levitation was only added in 1.9, so C-Moon brings its own. Same formula as the vanilla effect.
 * Runs on both sides: the server moves mobs, the client moves its own player.
 */
public class LevitationEffect extends ModPotion
{
    public LevitationEffect(int id, boolean isBadEffect, int color)
    {
        super(id, isBadEffect, color, "levitation");
    }

    @Override
    public void livingTick(EntityLivingBase plivingEntity, int pAmplifier)
    {
        plivingEntity.motionY += (0.05D * (pAmplifier + 1) - plivingEntity.motionY) * 0.2D;
        plivingEntity.fallDistance = 0;
    }

    @Override
    protected boolean hasIcon()
    {
        // C-Moon applied levitation without an icon in 1.19.
        return false;
    }
}
