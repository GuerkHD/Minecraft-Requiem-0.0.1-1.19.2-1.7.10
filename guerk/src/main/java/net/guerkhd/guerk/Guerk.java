package net.guerkhd.guerk;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.guerk.block.ModBlocks;
import net.guerkhd.guerk.compat.ModCompat;
import net.guerkhd.guerk.config.GuerkConfig;
import net.guerkhd.guerk.effect.ModEffects;
import net.guerkhd.guerk.event.ModEvents;
import net.guerkhd.guerk.item.ModItems;
import net.guerkhd.guerk.item.ModRecipes;
import net.guerkhd.guerk.networking.ModMessages;
import net.guerkhd.guerk.proxy.CommonProxy;
import net.guerkhd.guerk.world.GuerkOreGenerator;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Guerk: an edible ore with food themed tools, plus support for other mods' machines.
 */
@Mod(modid = Guerk.MOD_ID, name = Guerk.MOD_NAME, version = Guerk.VERSION, dependencies = ModCompat.LOAD_AFTER)
public class Guerk
{
    public static final String MOD_ID = "guerk";
    public static final String MOD_NAME = "Guerk";
    public static final String VERSION = "0.2.0-1.7.10";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static Guerk instance;

    @SidedProxy(clientSide = "net.guerkhd.guerk.proxy.ClientProxy",
                serverSide = "net.guerkhd.guerk.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GuerkConfig.load(new File(event.getModConfigurationDirectory(), MOD_ID + "-common.cfg"));

        // Potions first: the Block of Guerk's food references Constipation.
        ModEffects.register();
        ModBlocks.register();
        ModItems.register();
        ModMessages.register();

        ModCompat.preInit();

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        ModRecipes.register();
        ModCompat.init();
        GameRegistry.registerWorldGenerator(new GuerkOreGenerator(), 0);

        MinecraftForge.EVENT_BUS.register(new ModEvents());

        proxy.init(event);
    }
}
