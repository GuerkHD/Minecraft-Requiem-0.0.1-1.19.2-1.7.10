package net.guerkhd.minecraftrequiem.networking.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.guerkhd.minecraftrequiem.stand.StandManager;

/**
 * Summon key pressed.
 */
public class StandC2SPacket implements IMessage
{
    public StandC2SPacket() {    }

    @Override
    public void fromBytes(ByteBuf buf) {    }

    @Override
    public void toBytes(ByteBuf buf) {    }

    public static class Handler implements IMessageHandler<StandC2SPacket, IMessage>
    {
        @Override
        public IMessage onMessage(StandC2SPacket message, MessageContext context)
        {
            // 1.7.10 handles packets on the server thread already, no enqueueWork needed.
            StandManager.toggleStand(context.getServerHandler().playerEntity);
            return null;
        }
    }
}
