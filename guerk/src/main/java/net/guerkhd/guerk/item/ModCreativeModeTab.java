package net.guerkhd.guerk.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModCreativeModeTab
{
    public static final CreativeTabs GUERK_TAB = new CreativeTabs("guerktab")
    {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return ModItems.GUERK_INGOT;
        }
    };
}
