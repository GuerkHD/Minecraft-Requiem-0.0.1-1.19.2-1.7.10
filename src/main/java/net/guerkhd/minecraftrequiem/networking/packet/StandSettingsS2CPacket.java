package net.guerkhd.minecraftrequiem.networking.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.guerkhd.minecraftrequiem.stand.StandType;

/**
 * The server's settings that the client needs: the food costs for the HUD and whether only stand users see stands.
 * Sent when a player joins a world. The config file is loaded on both sides, but on a server only the server's counts.
 */
public class StandSettingsS2CPacket implements IMessage
{
    private int[] costs;
    private boolean onlyUsersSeeStands;

    public StandSettingsS2CPacket() {    }

    public static StandSettingsS2CPacket fromConfig()
    {
        StandSettingsS2CPacket packet = new StandSettingsS2CPacket();
        packet.costs = StandType.configCosts();
        packet.onlyUsersSeeStands = MinecraftRequiemCommonConfig.ONLY_STAND_USERS_SEE_STANDS;
        return packet;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        costs = new int[StandType.STAND_COUNT];
        int count = buf.readByte();
        for(int i = 0; i < count; i++)
        {
            int cost = buf.readByte();
            if(i < costs.length) costs[i] = cost;
        }
        onlyUsersSeeStands = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(costs.length);
        for(int cost : costs) buf.writeByte(cost); // the config allows 0 to 20
        buf.writeBoolean(onlyUsersSeeStands);
    }

    public static class Handler implements IMessageHandler<StandSettingsS2CPacket, IMessage>
    {
        @Override
        public IMessage onMessage(StandSettingsS2CPacket message, MessageContext context)
        {
            ClientStandData.setServerSettings(message.costs, message.onlyUsersSeeStands);
            return null;
        }
    }
}
