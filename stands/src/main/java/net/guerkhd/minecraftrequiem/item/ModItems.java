package net.guerkhd.minecraftrequiem.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.item.Custom.RequiemArrowItem;
import net.guerkhd.minecraftrequiem.item.Custom.StandArrowItem;
import net.guerkhd.minecraftrequiem.item.Custom.StandRemoveArrowItem;
import net.minecraft.item.Item;

public class ModItems
{
    public static Item STAND_ARROW;
    public static Item REQUIEM_ARROW;
    public static Item STAND_ARROW_TIP;
    public static Item REQUIEM_ARROW_TIP;
    public static Item REMOVER_ARROW;

    public static void register()
    {
        STAND_ARROW = register("stand_arrow", new StandArrowItem().setMaxStackSize(1));
        REQUIEM_ARROW = register("requiem_arrow", new RequiemArrowItem().setMaxStackSize(1));
        STAND_ARROW_TIP = register("stand_arrow_tip", new Item().setMaxStackSize(1));
        REQUIEM_ARROW_TIP = register("requiem_arrow_tip", new Item().setMaxStackSize(1));
        REMOVER_ARROW = register("remover_arrow", new StandRemoveArrowItem().setMaxStackSize(1));
    }

    private static Item register(String name, Item item)
    {
        item.setUnlocalizedName(MinecraftRequiem.MOD_ID + "." + name)
                .setTextureName(MinecraftRequiem.MOD_ID + ":" + name)
                .setCreativeTab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB);
        GameRegistry.registerItem(item, name);
        return item;
    }
}
