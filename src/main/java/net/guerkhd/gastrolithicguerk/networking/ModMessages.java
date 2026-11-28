package net.guerkhd.gastrolithicguerk.networking;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.guerkhd.gastrolithicguerk.GastrolithicGuerk;
import net.guerkhd.gastrolithicguerk.networking.packet.SnackC2SPacket;

public class ModMessages
{
    private static SimpleNetworkWrapper INSTANCE;

    public static void register()
    {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(GastrolithicGuerk.MOD_ID);
        INSTANCE.registerMessage(SnackC2SPacket.Handler.class, SnackC2SPacket.class, 0, Side.SERVER);
    }

    public static void sendToServer(IMessage message)
    {
        INSTANCE.sendToServer(message);
    }
}
