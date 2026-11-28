package net.guerkhd.guerk.compat;

import cpw.mods.fml.common.Loader;
import net.guerkhd.guerk.Guerk;
import net.guerkhd.guerk.block.ModBlocks;
import net.guerkhd.guerk.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Support for other mods. None of them is required: every integration only runs if its mod is installed,
 * and all of them go through IMC messages or reflection, so the mod also builds and runs without them.
 *
 * The ore dictionary does most of the work: oreGuerk, ingotGuerk, nuggetGuerk, blockGuerk and dustGuerk (Raw Guerk).
 * Thermal Expansion needs nothing else; its Pulverizer and Induction Smelter generate Guerk recipes on their own.
 */
public class ModCompat
{
    public static final String TINKERS = "TConstruct";
    public static final String IC2 = "IC2";
    public static final String MEKANISM = "Mekanism";
    public static final String ENDER_IO = "EnderIO";
    public static final String AE2 = "appliedenergistics2";
    public static final String RAILCRAFT = "Railcraft";

    /** For the @Mod annotation: load after these mods, so their recipe managers exist in init. */
    public static final String LOAD_AFTER = "after:" + TINKERS + ";after:" + IC2 + ";after:" + MEKANISM + ";after:ThermalExpansion;after:"
            + ENDER_IO + ";after:" + AE2 + ";after:" + RAILCRAFT;

    public static void preInit()
    {
        if(Loader.isModLoaded(TINKERS)) TinkersCompat.preInit();
    }

    public static void init()
    {
        if(Loader.isModLoaded(TINKERS)) run(TINKERS, TinkersCompat::init);
        if(Loader.isModLoaded(IC2)) run(IC2, IC2Compat::init);
        if(Loader.isModLoaded(MEKANISM)) run(MEKANISM, MekanismCompat::init);
        if(Loader.isModLoaded(ENDER_IO)) run(ENDER_IO, EnderIOCompat::init);
        if(Loader.isModLoaded(AE2)) run(AE2, AE2Compat::init);
        if(Loader.isModLoaded(RAILCRAFT)) run(RAILCRAFT, RailcraftCompat::init);
    }

    /**
     * A broken integration (e.g. after an API change in the other mod) must not take the game down.
     */
    private static void run(String modId, Integration integration)
    {
        try
        {
            integration.run();
            Guerk.LOGGER.info("Loaded {} integration.", modId);
        }
        catch(Throwable t)
        {
            Guerk.LOGGER.error("Could not load the " + modId + " integration.", t);
        }
    }

    private interface Integration
    {
        void run() throws Exception;
    }

    static ItemStack ore()
    {
        return new ItemStack(ModBlocks.GUERK_ORE);
    }

    static ItemStack ingot()
    {
        return new ItemStack(ModItems.GUERK_INGOT);
    }

    static ItemStack rawGuerk(int amount)
    {
        return new ItemStack(ModItems.RAW_GUERK, amount);
    }

    static NBTTagCompound nbt(ItemStack stack)
    {
        return stack.writeToNBT(new NBTTagCompound());
    }
}
