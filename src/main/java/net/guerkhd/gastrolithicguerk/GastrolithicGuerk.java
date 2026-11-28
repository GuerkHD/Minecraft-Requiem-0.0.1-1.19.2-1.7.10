package net.guerkhd.gastrolithicguerk;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.gastrolithicguerk.block.ModBlocks;
import net.guerkhd.gastrolithicguerk.compat.ModCompat;
import net.guerkhd.gastrolithicguerk.config.GastrolithicGuerkConfig;
import net.guerkhd.gastrolithicguerk.effect.ModEffects;
import net.guerkhd.gastrolithicguerk.event.ModEvents;
import net.guerkhd.gastrolithicguerk.item.ModItems;
import net.guerkhd.gastrolithicguerk.item.ModRecipes;
import net.guerkhd.gastrolithicguerk.networking.ModMessages;
import net.guerkhd.gastrolithicguerk.proxy.CommonProxy;
import net.guerkhd.gastrolithicguerk.world.GuerkOreGenerator;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Gastrolithic Guerk: an edible ore with food themed tools, plus support for other mods' machines.
 */
@Mod(modid = GastrolithicGuerk.MOD_ID, name = GastrolithicGuerk.MOD_NAME, version = GastrolithicGuerk.VERSION, dependencies = ModCompat.LOAD_AFTER)
public class GastrolithicGuerk
{
    public static final String MOD_ID = "gastrolithicguerk";
    public static final String MOD_NAME = "Gastrolithic Guerk";
    public static final String VERSION = "0.3.0-1.7.10";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static GastrolithicGuerk instance;

    @SidedProxy(clientSide = "net.guerkhd.gastrolithicguerk.proxy.ClientProxy",
                serverSide = "net.guerkhd.gastrolithicguerk.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GastrolithicGuerkConfig.load(new File(event.getModConfigurationDirectory(), MOD_ID + "-common.cfg"));

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
