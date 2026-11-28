package net.guerkhd.minecraftrequiem.networking.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.guerkhd.minecraftrequiem.client.ClientStandData;

/**
 * Things the HUD shows for a moment instead of a chat message (StandHudOverlay).
 */
public class StandFeedbackS2CPacket implements IMessage
{
    public static final byte AWAKENED = 0;        // the Stand Arrow gave a stand: "go go go" (katakana) in the middle of the screen
    public static final byte ABILITY_FAILED = 1;  // not enough food: the cost in the HUD flashes red

    private byte kind;

    public StandFeedbackS2CPacket() {    }

    public StandFeedbackS2CPacket(byte kind)
    {
        this.kind = kind;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        kind = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(kind);
    }

    public static class Handler implements IMessageHandler<StandFeedbackS2CPacket, IMessage>
    {
        @Override
        public IMessage onMessage(StandFeedbackS2CPacket message, MessageContext context)
        {
            if(message.kind == AWAKENED) ClientStandData.awakened();
            else if(message.kind == ABILITY_FAILED) ClientStandData.abilityFailed();
            return null;
        }
    }
}
