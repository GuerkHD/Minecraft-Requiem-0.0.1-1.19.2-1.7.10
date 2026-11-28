package net.guerkhd.gastrolithicguerk.networking.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.guerkhd.gastrolithicguerk.compat.TinkersCompat;

/**
 * Sneak + right click with a Tinkers tool that has a Guerk handle. The server checks everything again.
 */
public class SnackC2SPacket implements IMessage
{
    public SnackC2SPacket() {    }

    @Override
    public void fromBytes(ByteBuf buf) {    }

    @Override
    public void toBytes(ByteBuf buf) {    }

    public static class Handler implements IMessageHandler<SnackC2SPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SnackC2SPacket message, MessageContext context)
        {
            TinkersCompat.snack(context.getServerHandler().playerEntity);
            return null;
        }
    }
}
