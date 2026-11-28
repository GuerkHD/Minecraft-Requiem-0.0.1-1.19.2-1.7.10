package net.guerkhd.minecraftrequiem.networking;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.networking.packet.AbilityC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.SnackC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandDataSyncS2CPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandFeedbackS2CPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandSettingsS2CPacket;
import net.minecraft.entity.player.EntityPlayerMP;

public class ModMessages
{
    private static SimpleNetworkWrapper INSTANCE;

    private static int packetID = 0;

    private static int id()
    {
        return packetID++;
    }

    public static void register()
    {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MinecraftRequiem.MOD_ID);

        INSTANCE.registerMessage(StandC2SPacket.Handler.class, StandC2SPacket.class, id(), Side.SERVER);
        INSTANCE.registerMessage(AbilityC2SPacket.Handler.class, AbilityC2SPacket.class, id(), Side.SERVER);
        INSTANCE.registerMessage(SnackC2SPacket.Handler.class, SnackC2SPacket.class, id(), Side.SERVER);

        INSTANCE.registerMessage(StandDataSyncS2CPacket.Handler.class, StandDataSyncS2CPacket.class, id(), Side.CLIENT);
        INSTANCE.registerMessage(StandSettingsS2CPacket.Handler.class, StandSettingsS2CPacket.class, id(), Side.CLIENT);
        INSTANCE.registerMessage(StandFeedbackS2CPacket.Handler.class, StandFeedbackS2CPacket.class, id(), Side.CLIENT);
    }

    public static void sendToServer(IMessage message)
    {
        INSTANCE.sendToServer(message);
    }

    public static void sendToPlayer(IMessage message, EntityPlayerMP player)
    {
        INSTANCE.sendTo(message, player);
    }
}
