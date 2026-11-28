package net.guerkhd.minecraftrequiem.entity;

import net.guerkhd.minecraftrequiem.stand.PlayerStand;
import net.guerkhd.minecraftrequiem.stand.StandType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * The visible stand: a body with the stand's own skin (RenderStand) that floats behind its user.
 *
 * 1.19 used an invisible zombie in dyed leather armor with NoAI, NoGravity, Silent and Invulnerable. None of these
 * flags exist in 1.7.10, so this is a small entity of its own that does nothing but follow its user.
 */
public class EntityStand extends EntityLiving
{
    private static final int DATA_OWNER = 20;
    private static final int DATA_CHILD = 21;
    /** Which stand it is, so the client can pick the skin. */
    private static final int DATA_TYPE = 22;

    public EntityStand(World level)
    {
        super(level);
        setSize(0.6F, 1.8F);
        this.isImmuneToFire = true;
        this.noClip = false;
    }

    public EntityStand(World level, EntityPlayer owner, StandType standType)
    {
        this(level);

        setOwnerName(owner.getCommandSenderName());
        setPosition(owner.posX - 0.5, owner.boundingBox.minY + 0.5, owner.posZ - 0.5);
        rotationYaw = owner.rotationYaw;
        rotationYawHead = owner.rotationYawHead;
        renderYawOffset = owner.renderYawOffset;

        dataWatcher.updateObject(DATA_TYPE, standType.ordinal());
        if(standType == StandType.ECHOS) setChild(true);
        setCanPickUpLoot(false);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(DATA_OWNER, "");
        dataWatcher.addObject(DATA_CHILD, (byte) 0);
        dataWatcher.addObject(DATA_TYPE, StandType.UNKNOWN.ordinal());
    }

    public String getOwnerName()
    {
        return dataWatcher.getWatchableObjectString(DATA_OWNER);
    }

    public void setOwnerName(String name)
    {
        dataWatcher.updateObject(DATA_OWNER, name);
    }

    public StandType getStandType()
    {
        return StandType.byId(dataWatcher.getWatchableObjectInt(DATA_TYPE));
    }

    public void setChild(boolean child)
    {
        dataWatcher.updateObject(DATA_CHILD, (byte) (child ? 1 : 0));
        setSize(child ? 0.3F : 0.6F, child ? 0.9F : 1.8F);
    }

    @Override
    public boolean isChild()
    {
        return dataWatcher.getWatchableObjectByte(DATA_CHILD) == 1;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        // No AI turns the body, so it faces the same way as its user (rotationYaw is copied from them).
        renderYawOffset = rotationYaw;

        if(!worldObj.isRemote) followOwner();
    }

    private void followOwner()
    {
        EntityPlayer owner = worldObj.getPlayerEntityByName(getOwnerName());
        PlayerStand stand = owner != null ? PlayerStand.get(owner) : null;

        if(owner == null || stand == null || !stand.getStandActive() || stand.getStandEntity() != this || getDistanceSqToEntity(owner) > 50 * 50)
        {
            setDead();
            return;
        }

        extinguish();
        motionX = motionY = motionZ = 0;

        Vec3 move = behindMove(owner, 0.5);
        moveEntity(move.xCoord, move.yCoord, move.zCoord);

        rotationYaw = owner.rotationYaw;
        rotationPitch = owner.rotationPitch;
        rotationYawHead = owner.rotationYawHead;
        renderYawOffset = owner.renderYawOffset;
    }

    /**
     * Movement towards the spot 1.5 blocks behind the user.
     * Only the direction the user faces counts, not how far up or down they look: 1.19 used the look vector, which
     * pulled the stand into the user (and in front of the camera) when looking down.
     */
    private Vec3 behindMove(EntityPlayer target, double yOffset)
    {
        float yaw = target.rotationYaw * (float) Math.PI / 180F;
        double x = target.posX + MathHelper.sin(yaw) * 1.5;
        double y = target.boundingBox.minY + yOffset;
        double z = target.posZ - MathHelper.cos(yaw) * 1.5;

        return Vec3.createVectorHelper(x - posX, y - posY, z - posZ);
    }

    // --- Everything below turns this into a harmless prop (1.19: NoAI, NoGravity, Silent, Invulnerable). ---

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
    public boolean canBeCollidedWith()
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

    /**
     * Not saved with the chunk; StandManager spawns a new one when needed.
     */
    @Override
    public boolean writeToNBTOptional(NBTTagCompound nbt)
    {
        return false;
    }
}
