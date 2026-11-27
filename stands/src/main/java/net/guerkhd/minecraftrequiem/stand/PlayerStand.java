package net.guerkhd.minecraftrequiem.stand;

import net.guerkhd.minecraftrequiem.entity.EntityStand;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.StandDataSyncS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Per-player stand data. 1.7.10 has no capabilities, so this is an IExtendedEntityProperties
 * (replaces PlayerStand + PlayerStandProvider).
 */
public class PlayerStand implements IExtendedEntityProperties
{
    public static final String PROPERTY_NAME = "MinecraftRequiemStand";

    private final EntityPlayer player;

    private boolean standUser = false;
    private boolean standActive = false;
    private int standID;
    private boolean bomb;
    private double maxY;
    //ID 0 = The World
    //ID 1 = D4C
    //ID 2 = Magicians Red
    //ID 3 = C-Moon
    //ID 4 = Weather Report
    //ID 5 = Echos Act 3
    //ID 6 = Highway To Hell
    //ID 7 = Killer Queen
    //ID 8 = King Crimson
    //ID 9 = Green Day

    /** The summoned stand. Not saved; it gets respawned when missing. */
    private EntityStand standEntity;

    public PlayerStand(EntityPlayer player)
    {
        this.player = player;
    }

    public static void register(EntityPlayer player)
    {
        player.registerExtendedProperties(PROPERTY_NAME, new PlayerStand(player));
    }

    public static PlayerStand get(EntityPlayer player)
    {
        return (PlayerStand) player.getExtendedProperties(PROPERTY_NAME);
    }

    public boolean getStandUser()
    {
        return standUser;
    }
    public void makeStandUser()
    {
        this.standUser = true;
    }
    public void undoStandUser()
    {
        this.standUser = false;
    }

    public boolean getStandActive()
    {
        return standActive;
    }
    public void activateStand()
    {
        this.standActive = true;
    }
    public void deactivateStand()
    {
        this.standActive = false;
    }

    public int getStandID()
    {
        return standID;
    }
    public void setStandID(int standID)
    {
        this.standID = standID;
    }

    public StandType getStandType()
    {
        return StandType.byId(standID);
    }

    public boolean getBomb()
    {
        return bomb;
    }
    public void setBomb(boolean bomb)
    {
        this.bomb = bomb;
    }

    public double getMaxY()
    {
        return maxY;
    }
    public void setMaxY(double maxY)
    {
        this.maxY = maxY;
    }

    public EntityStand getStandEntity()
    {
        return standEntity;
    }
    public void setStandEntity(EntityStand standEntity)
    {
        this.standEntity = standEntity;
    }

    public void copyFrom(PlayerStand source)
    {
        this.standUser = source.standUser;
        this.standActive = source.standActive;
        this.standID = source.standID;
        this.bomb = source.bomb;
        this.maxY = source.maxY;
    }

    /**
     * Sends everything to the client (replaces the five 1.19 *DataSyncS2CPacket classes).
     */
    public void sync()
    {
        if(player instanceof EntityPlayerMP)
        {
            ModMessages.sendToPlayer(new StandDataSyncS2CPacket(standUser, standActive, standID, bomb, maxY), (EntityPlayerMP) player);
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("standUser", standUser);
        nbt.setBoolean("standActive", standActive);
        nbt.setInteger("standID", standID);
        nbt.setBoolean("bomb", bomb);
        nbt.setDouble("maxY", maxY);
        compound.setTag(PROPERTY_NAME, nbt);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        NBTTagCompound nbt = compound.getCompoundTag(PROPERTY_NAME);
        standUser = nbt.getBoolean("standUser");
        standActive = nbt.getBoolean("standActive");
        standID = nbt.getInteger("standID");
        bomb = nbt.getBoolean("bomb");
        maxY = nbt.getDouble("maxY");
    }

    @Override
    public void init(Entity entity, World world) {    }
}
