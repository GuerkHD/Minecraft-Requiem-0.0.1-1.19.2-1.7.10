package net.guerkhd.guerk.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.guerk.Guerk;
import net.guerkhd.guerk.food.FoodProperties;
import net.guerkhd.guerk.item.Custom.GuerkFoodItem;
import net.guerkhd.guerk.item.Custom.ModAxeItem;
import net.guerkhd.guerk.item.Custom.ModPickaxeItem;
import net.guerkhd.guerk.compat.TinkersCompat;
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
        return stack != null && (isGuerkTool(stack.getItem()) || TinkersCompat.hasGuerkHead(stack));
    }

    private static Item register(String name, Item item)
    {
        item.setUnlocalizedName(Guerk.MOD_ID + "." + name)
                .setTextureName(Guerk.MOD_ID + ":" + name)
                .setCreativeTab(ModCreativeModeTab.GUERK_TAB);
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
