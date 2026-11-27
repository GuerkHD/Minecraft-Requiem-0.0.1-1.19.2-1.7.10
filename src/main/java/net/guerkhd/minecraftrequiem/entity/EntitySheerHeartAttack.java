package net.guerkhd.minecraftrequiem.entity;

import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Killer Queen's second bomb. 1.19 used a baby turtle (NoAI, Silent, Invulnerable); turtles don't exist in 1.7.10.
 * It stays where it was placed and disappears once it no longer carries the Bomb effect.
 */
public class EntitySheerHeartAttack extends EntityLiving
{
    public static final String NAME = "Sheer Heart Attack";

    public EntitySheerHeartAttack(World level)
    {
        super(level);
        setSize(0.5F, 0.5F);
        this.isImmuneToFire = true;
        setCustomNameTag(NAME);
        setAlwaysRenderNameTag(true);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if(!worldObj.isRemote && !isPotionActive(ModEffects.BOMB)) setDead();
    }

    @Override
    protected boolean isAIEnabled()
    {
        return true;
    }

    @Override
    protected void updateAITasks() {    }

    @Override
    protected void updateEntityActionState() {    }

    @Override
    public void moveEntityWithHeading(float strafe, float forward) {    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }

    @Override
    public boolean isEntityInvulnerable()
    {
        return true;
    }

    @Override
    public boolean allowLeashing()
    {
        return false;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected void collideWithNearbyEntities() {    }

    @Override
    public void applyEntityCollision(Entity entity) {    }

    @Override
    protected void fall(float distance) {    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    protected String getHurtSound()
    {
        return null;
    }

    @Override
    protected String getDeathSound()
    {
        return null;
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {    }

    @Override
    protected boolean canDespawn()
    {
        return false;
    }
}
