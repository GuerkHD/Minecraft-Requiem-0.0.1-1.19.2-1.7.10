package net.guerkhd.gastrolithicguerk.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.guerkhd.gastrolithicguerk.event.ClientEvents;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);

        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }
}
