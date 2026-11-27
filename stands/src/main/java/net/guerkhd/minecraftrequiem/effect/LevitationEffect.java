package net.guerkhd.minecraftrequiem.effect;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Levitation was only added in 1.9, so C-Moon brings its own, moving exactly like the vanilla effect.
 * Runs on both sides: the server moves mobs, the client moves its own player.
 */
public class LevitationEffect extends ModPotion
{
    private static final double GRAVITY = 0.08D;
    private static final double DRAG = 0.98D;

    public LevitationEffect(int id, boolean isBadEffect, int color)
    {
        super(id, isBadEffect, color, "levitation");
    }

    @Override
    public void livingTick(EntityLivingBase plivingEntity, int pAmplifier)
    {
        allowFloating(plivingEntity);

        // Like vanilla: no effect in water and lava, and flying players don't fall anyway.
        if(plivingEntity.isInWater() || plivingEntity.handleLavaMovement()) return;
        if(plivingEntity instanceof EntityPlayer && ((EntityPlayer) plivingEntity).capabilities.isFlying) return;

        // Vanilla (1.9+) replaces gravity with motionY += (0.05 * (amplifier + 1) - motionY) * 0.2 after the entity moved.
        // This runs before the move instead, and 1.7.10 still subtracts gravity afterwards. So work back to the speed
        // vanilla would continue from, take the vanilla step from there, and leave gravity to the end of the tick.
        // (Just adding the formula on top of gravity made C-Moon lift about a fifth as fast as in 1.19.)
        double afterMove = plivingEntity.motionY / DRAG + GRAVITY;
        plivingEntity.motionY = (afterMove + (0.05D * (pAmplifier + 1) - afterMove) * 0.2D) * DRAG;
        plivingEntity.fallDistance = 0;
    }
}
