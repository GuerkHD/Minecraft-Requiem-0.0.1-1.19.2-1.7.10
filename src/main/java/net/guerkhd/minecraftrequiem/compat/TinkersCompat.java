package net.guerkhd.minecraftrequiem.compat;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.block.ModBlocks;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.minecraft.block.Block;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.food.FoodProperties;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Tinkers' Construct:
 * - Molten Guerk: melting ore (2 ingots), Raw Guerk, nuggets, ingots and blocks in the smeltery and casting them back.
 *   Tinkers generates all of these from the ore dictionary once a fluid type called "Guerk" exists.
 * - Guerk as a tool material (stats like the stone tier of the vanilla Guerk tools) that can be cast into tool parts.
 * - Guerk parts give tools the material's abilities: head = Food Leech, handle = Snack, binding/guard = Constipation on hit.
 */
public class TinkersCompat
{
    /** Name of the Tinkers fluid type. Must match the ore dictionary suffix (ingotGuerk, oreGuerk, ...). */
    private static final String FLUID_TYPE = "Guerk";
    private static final String FLUID_NAME = "guerk.molten";
    private static final int MELTING_TEMPERATURE = 500;
    /** Tool part tint: #BE29EC from the Guerk palette (the five Guerk Ingot colors, see tools/guerk_palette.py). */
    /** Tints the Tinkers tool parts. #BE29EC from the Guerk palette (the Guerk Ingot colors, see tools/guerk_palette.py). */
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

    /** Guerk head: Food Leech (see ModEvents). */
    public static boolean hasGuerkHead(ItemStack stack)
    {
        return hasGuerkPart(stack, "Head");
    }

    /** Guerk handle: Snack, sneak + right click eats a piece of the tool. */
    public static boolean hasGuerkHandle(ItemStack stack)
    {
        return hasGuerkPart(stack, "Handle");
    }

    /** Guerk middle part (binding or guard): hit targets get Constipation. */
    public static boolean hasGuerkAccessory(ItemStack stack)
    {
        return hasGuerkPart(stack, "Accessory");
    }

    public static boolean isTinkersTool(ItemStack stack)
    {
        return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("InfiTool");
    }

    private static boolean hasGuerkPart(ItemStack stack, String part)
    {
        if(moltenGuerk == null || !isTinkersTool(stack)) return false;
        NBTTagCompound tool = stack.getTagCompound().getCompoundTag("InfiTool");
        return tool.hasKey(part) && tool.getInteger(part) == MinecraftRequiemCommonConfig.TINKERS_MATERIAL_ID;
    }

    // --- Snack ---

    private static final int SNACK_DURABILITY = 10;
    private static final int SNACK_COOLDOWN = 20;
    /** Same as a Guerk Nugget. */
    private static final FoodProperties SNACK = new FoodProperties.Builder().nutrition(1).saturationMod(0.3f).build();

    // One map per side: in singleplayer the client and server player are equal (Entity.equals compares the entity ID),
    // so a shared map would let the client's cooldown block the server.
    private static final Map<EntityPlayer, Long> lastSnackClient = new WeakHashMap<EntityPlayer, Long>();
    private static final Map<EntityPlayer, Long> lastSnackServer = new WeakHashMap<EntityPlayer, Long>();

    /**
     * Whether sneak + right click with this stack should be a snack (cooldown not included).
     * Checked on both sides: the client cancels the normal right click action, the server checks it again before eating.
     */
    public static boolean canSnack(EntityPlayer player, ItemStack stack)
    {
        return player.isSneaking() && hasGuerkHandle(stack)
                && !stack.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken")
                && player.canEat(SNACK.canAlwaysEat())
                && !player.isPotionActive(ModEffects.CONSTIPATION);
    }

    /**
     * True (and starts the cooldown) if the player may snack now.
     */
    public static boolean snackCooldownOver(EntityPlayer player)
    {
        Map<EntityPlayer, Long> lastSnack = player.worldObj.isRemote ? lastSnackClient : lastSnackServer;
        long now = player.worldObj.getTotalWorldTime();
        Long last = lastSnack.get(player);
        if(last != null && now - last < SNACK_COOLDOWN && now >= last) return false;
        lastSnack.put(player, now);
        return true;
    }

    /**
     * Server side: eat a piece of the held tool.
     */
    public static void snack(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItem();
        if(!canSnack(player, stack) || !snackCooldownOver(player)) return;

        try
        {
            // Tinkers keeps the durability in its own NBT data, so its own method has to do the damage (and the breaking).
            Class.forName("tconstruct.library.tools.AbilityHelper")
                    .getMethod("damageTool", ItemStack.class, int.class, EntityLivingBase.class, boolean.class)
                    .invoke(null, stack, SNACK_DURABILITY, player, false);
        }
        catch(Exception e)
        {
            MinecraftRequiem.LOGGER.error("Could not damage the Tinkers tool for a snack.", e);
            return;
        }

        player.getFoodStats().addStats(SNACK.getNutrition(), SNACK.getSaturationModifier());
        player.worldObj.playSoundAtEntity(player, "random.burp", 0.5F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);
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
