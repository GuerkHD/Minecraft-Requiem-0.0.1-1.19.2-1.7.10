package net.guerkhd.minecraftrequiem.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModCreativeModeTab
{
    public static final CreativeTabs MINECRAFTREQUIEM_TAB = new CreativeTabs("minecraftrequiemtab")
    {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return ModItems.GUERK_INGOT;
        }
    };
}
