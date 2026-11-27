package net.guerkhd.minecraftrequiem.compat;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.block.ModBlocks;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Tinkers' Construct:
 * - Molten Guerk: melting ore (2 ingots), Raw Guerk, nuggets, ingots and blocks in the smeltery and casting them back.
 *   Tinkers generates all of these from the ore dictionary once a fluid type called "Guerk" exists.
 * - Guerk as a tool material (stats like the stone tier of the vanilla Guerk tools) that can be cast into tool parts.
 * - Tools with a Guerk head have Food Leech, like the Guerk tools (handled in ModEvents through {@link #isGuerkTool}).
 */
public class TinkersCompat
{
    /** Name of the Tinkers fluid type. Must match the ore dictionary suffix (ingotGuerk, oreGuerk, ...). */
    private static final String FLUID_TYPE = "Guerk";
    private static final String FLUID_NAME = "guerk.molten";
    private static final int MELTING_TEMPERATURE = 500;
    private static final int COLOR = 0xBE29EC;

    private static Fluid moltenGuerk;
    /** False if another mod registered the fluid first; its textures are then that mod's business. */
    private static boolean ownFluid;

    static void preInit()
    {
        Fluid fluid = new Fluid(FLUID_NAME)
                .setUnlocalizedName(MinecraftRequiem.MOD_ID + ".molten_guerk")
                .setLuminosity(12)
                .setDensity(3000)
                .setViscosity(6000)
                .setTemperature(1000);

        // Another mod might already have registered a molten Guerk; share it instead.
        ownFluid = FluidRegistry.registerFluid(fluid);
        moltenGuerk = ownFluid ? fluid : FluidRegistry.getFluid(FLUID_NAME);
    }

    static void init() throws Exception
    {
        // Same call the Tinkers IMC handler makes, but under the name the ore dictionary recipes need.
        Class.forName("tconstruct.library.crafting.FluidType")
                .getMethod("registerFluidType", String.class, Block.class, int.class, int.class, Fluid.class, boolean.class)
                .invoke(null, FLUID_TYPE, ModBlocks.GUERK_BLOCK, 0, MELTING_TEMPERATURE, moltenGuerk, true);

        int materialId = MinecraftRequiemCommonConfig.TINKERS_MATERIAL_ID;

        NBTTagCompound material = new NBTTagCompound();
        material.setInteger("Id", materialId);
        material.setString("Name", FLUID_TYPE);
        material.setString("localizationString", "material." + MinecraftRequiem.MOD_ID + ".guerk");
        material.setInteger("HarvestLevel", 1);
        material.setInteger("Durability", 131);
        material.setInteger("MiningSpeed", 400);
        material.setInteger("Attack", 1);
        material.setFloat("HandleModifier", 1.0f);
        material.setInteger("Reinforced", 0);
        material.setString("Style", EnumChatFormatting.LIGHT_PURPLE.toString());
        material.setInteger("Color", COLOR);
        FMLInterModComms.sendMessage(ModCompat.TINKERS, "addMaterial", material);

        NBTTagCompound casting = new FluidStack(moltenGuerk, 1).writeToNBT(new NBTTagCompound());
        casting.setInteger("MaterialId", materialId);
        FMLInterModComms.sendMessage(ModCompat.TINKERS, "addPartCastingMaterial", casting);
    }

    /**
     * True for Tinkers' Construct tools with a Guerk head.
     */
    public static boolean isGuerkTool(ItemStack stack)
    {
        if(moltenGuerk == null || stack == null || !stack.hasTagCompound() || !stack.getTagCompound().hasKey("InfiTool")) return false;
        return stack.getTagCompound().getCompoundTag("InfiTool").getInteger("Head") == MinecraftRequiemCommonConfig.TINKERS_MATERIAL_ID;
    }

    @SideOnly(Side.CLIENT)
    public static void registerFluidIcons(TextureMap map)
    {
        // Called again on every resource reload, which clears the atlas, so the icons are registered every time.
        if(!ownFluid) return;

        moltenGuerk.setIcons(map.registerIcon(MinecraftRequiem.MOD_ID + ":molten_guerk_still"),
                map.registerIcon(MinecraftRequiem.MOD_ID + ":molten_guerk_flow"));
    }
}
