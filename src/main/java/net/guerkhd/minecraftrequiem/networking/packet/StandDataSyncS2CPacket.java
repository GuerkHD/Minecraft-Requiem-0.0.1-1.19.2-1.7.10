package net.guerkhd.minecraftrequiem.networking.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.guerkhd.minecraftrequiem.client.ClientStandData;

/**
 * Sends the player's stand data to their client. Replaces the five single-value sync packets of the 1.19 version.
 */
public class StandDataSyncS2CPacket implements IMessage
{
    private boolean standUser;
    private boolean standActive;
    private int standID;
    private boolean bomb;
    private double maxY;

    public StandDataSyncS2CPacket() {    }

    public StandDataSyncS2CPacket(boolean standUser, boolean standActive, int standID, boolean bomb, double maxY)
    {
        this.standUser = standUser;
        this.standActive = standActive;
        this.standID = standID;
        this.bomb = bomb;
        this.maxY = maxY;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        standUser = buf.readBoolean();
        standActive = buf.readBoolean();
        standID = buf.readInt();
        bomb = buf.readBoolean();
        maxY = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(standUser);
        buf.writeBoolean(standActive);
        buf.writeInt(standID);
        buf.writeBoolean(bomb);
        buf.writeDouble(maxY);
    }

    public static class Handler implements IMessageHandler<StandDataSyncS2CPacket, IMessage>
    {
        @Override
        public IMessage onMessage(StandDataSyncS2CPacket message, MessageContext context)
        {
            ClientStandData.setStandUser(message.standUser);
            ClientStandData.setStandActive(message.standActive);
            ClientStandData.setStandID(message.standID);
            ClientStandData.setBomb(message.bomb);
            ClientStandData.setMaxY(message.maxY);
            return null;
        }
    }
}
