package net.guerkhd.minecraftrequiem.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.food.FoodProperties;
import net.guerkhd.minecraftrequiem.item.Custom.*;
import net.guerkhd.minecraftrequiem.compat.TinkersCompat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraftforge.oredict.OreDictionary;

public class ModItems
{
    /** Food taken from the target and given to the attacker on every hit with a Guerk tool. */
    public static final int GUERK_FOOD_LEECH = 1;

    public static Item GUERK_INGOT;
    public static Item RAW_GUERK;
    public static Item GUERK_NUGGET;

    public static Item STAND_ARROW;
    public static Item REQUIEM_ARROW;
    public static Item STAND_ARROW_TIP;
    public static Item REQUIEM_ARROW_TIP;
    public static Item REMOVER_ARROW;

    // Same stats as the vanilla stone tools, like Tiers.STONE in 1.19.
    public static Item GUERK_SWORD;
    public static Item GUERK_PICKAXE;
    public static Item GUERK_AXE;
    public static Item GUERK_SHOVEL;
    public static Item GUERK_HOE;

    public static void register()
    {
        GUERK_INGOT = register("guerk_ingot", new GuerkFoodItem(Foods.GUERK_INGOT));
        RAW_GUERK = register("raw_guerk", new GuerkFoodItem(Foods.RAW_GUERK));
        GUERK_NUGGET = register("guerk_nugget", new GuerkFoodItem(Foods.GUERK_NUGGET));

        STAND_ARROW = register("stand_arrow", new StandArrowItem().setMaxStackSize(1));
        REQUIEM_ARROW = register("requiem_arrow", new RequiemArrowItem().setMaxStackSize(1));
        STAND_ARROW_TIP = register("stand_arrow_tip", new Item().setMaxStackSize(1));
        REQUIEM_ARROW_TIP = register("requiem_arrow_tip", new Item().setMaxStackSize(1));
        REMOVER_ARROW = register("remover_arrow", new StandRemoveArrowItem().setMaxStackSize(1));

        GUERK_SWORD = register("guerk_sword", new ItemSword(Item.ToolMaterial.STONE));
        GUERK_PICKAXE = register("guerk_pickaxe", new ModPickaxeItem(Item.ToolMaterial.STONE));
        GUERK_AXE = register("guerk_axe", new ModAxeItem(Item.ToolMaterial.STONE));
        GUERK_SHOVEL = register("guerk_shovel", new ItemSpade(Item.ToolMaterial.STONE));
        GUERK_HOE = register("guerk_hoe", new ItemHoe(Item.ToolMaterial.STONE));

        // 1.19 forge tags -> ore dictionary
        OreDictionary.registerOre("ingotGuerk", GUERK_INGOT);
        OreDictionary.registerOre("nuggetGuerk", GUERK_NUGGET);
        // Raw Guerk is what crushers make out of the ore (2 per ore), so it takes the place of a dust.
        OreDictionary.registerOre("dustGuerk", RAW_GUERK);
    }

    public static boolean isGuerkTool(Item item)
    {
        return item != null && (item == GUERK_SWORD || item == GUERK_PICKAXE || item == GUERK_AXE || item == GUERK_SHOVEL || item == GUERK_HOE);
    }

    /**
     * Guerk tools and Tinkers' Construct tools with a Guerk head.
     */
    public static boolean hasFoodLeech(ItemStack stack)
    {
        return stack != null && (isGuerkTool(stack.getItem()) || TinkersCompat.isGuerkTool(stack));
    }

    private static Item register(String name, Item item)
    {
        item.setUnlocalizedName(MinecraftRequiem.MOD_ID + "." + name)
                .setTextureName(MinecraftRequiem.MOD_ID + ":" + name)
                .setCreativeTab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB);
        GameRegistry.registerItem(item, name);
        return item;
    }

    public static class Foods
    {
        public static final FoodProperties GUERK_INGOT = new FoodProperties.Builder()
                .nutrition(5)
                .saturationMod(1.2f)
                .build();
        public static final FoodProperties RAW_GUERK = new FoodProperties.Builder()
                .nutrition(2)
                .saturationMod(0.6f)
                .fast()
                .effect(() -> Potion.digSlowdown, 100, 0, 0.8f)
                .build();
        public static final FoodProperties GUERK_NUGGET = new FoodProperties.Builder()
                .nutrition(1)
                .saturationMod(0.3f)
                .fast()
                .build();
    }
}
