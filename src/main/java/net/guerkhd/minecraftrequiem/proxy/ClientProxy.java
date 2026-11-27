package net.guerkhd.minecraftrequiem.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.guerkhd.minecraftrequiem.client.render.RenderSheerHeartAttack;
import net.guerkhd.minecraftrequiem.client.render.RenderStand;
import net.guerkhd.minecraftrequiem.entity.EntitySheerHeartAttack;
import net.guerkhd.minecraftrequiem.entity.EntityStand;
import net.guerkhd.minecraftrequiem.event.ClientEvents;
import net.guerkhd.minecraftrequiem.util.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);

        RenderingRegistry.registerEntityRenderingHandler(EntityStand.class, new RenderStand());
        RenderingRegistry.registerEntityRenderingHandler(EntitySheerHeartAttack.class, new RenderSheerHeartAttack());
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);

        ClientRegistry.registerKeyBinding(KeyBinding.SUMMONING_KEY);
        ClientRegistry.registerKeyBinding(KeyBinding.ABILITY_KEY);

        MinecraftForge.EVENT_BUS.register(new ClientEvents.ClientForgeEvents());
        FMLCommonHandler.instance().bus().register(new ClientEvents.ClientFmlEvents());
    }
}
