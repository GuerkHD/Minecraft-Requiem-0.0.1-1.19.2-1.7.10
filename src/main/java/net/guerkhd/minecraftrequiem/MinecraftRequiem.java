package net.guerkhd.minecraftrequiem;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.minecraftrequiem.block.ModBlocks;
import net.guerkhd.minecraftrequiem.compat.ModCompat;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.entity.ModEntities;
import net.guerkhd.minecraftrequiem.event.ModEvents;
import net.guerkhd.minecraftrequiem.event.ModTickEvents;
import net.guerkhd.minecraftrequiem.event.RemoverArrowFurnace;
import net.guerkhd.minecraftrequiem.item.ModItems;
import net.guerkhd.minecraftrequiem.item.ModRecipes;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.proxy.CommonProxy;
import net.guerkhd.minecraftrequiem.world.GuerkOreGenerator;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = MinecraftRequiem.MOD_ID, name = MinecraftRequiem.MOD_NAME, version = MinecraftRequiem.VERSION, dependencies = ModCompat.LOAD_AFTER)
public class MinecraftRequiem
{
    public static final String MOD_ID = "minecraftrequiem";
    public static final String MOD_NAME = "Minecraft Requiem";
    public static final String VERSION = "0.1.0-1.7.10";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static MinecraftRequiem instance;

    @SidedProxy(clientSide = "net.guerkhd.minecraftrequiem.proxy.ClientProxy",
                serverSide = "net.guerkhd.minecraftrequiem.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftRequiemCommonConfig.load(new File(event.getModConfigurationDirectory(), "minecraftrequiem-common.cfg"));

        // Potions first: food items and blocks reference them.
        ModEffects.register();
        ModBlocks.register();
        ModItems.register();
        ModEntities.register();
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
        FMLCommonHandler.instance().bus().register(new ModTickEvents());
        FMLCommonHandler.instance().bus().register(new RemoverArrowFurnace());

        proxy.init(event);
    }
}
