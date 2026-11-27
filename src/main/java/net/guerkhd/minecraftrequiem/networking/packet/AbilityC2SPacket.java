package net.guerkhd.minecraftrequiem.networking.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.guerkhd.minecraftrequiem.stand.StandAbilities;

/**
 * Ability key pressed.
 */
public class AbilityC2SPacket implements IMessage
{
    public AbilityC2SPacket() {    }

    @Override
    public void fromBytes(ByteBuf buf) {    }

    @Override
    public void toBytes(ByteBuf buf) {    }

    public static class Handler implements IMessageHandler<AbilityC2SPacket, IMessage>
    {
        @Override
        public IMessage onMessage(AbilityC2SPacket message, MessageContext context)
        {
            StandAbilities.useAbility(context.getServerHandler().playerEntity);
            return null;
        }
    }
}
