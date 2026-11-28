package net.guerkhd.gastrolithicguerk.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModCreativeModeTab
{
    public static final CreativeTabs GUERK_TAB = new CreativeTabs("gastrolithicguerk")
    {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return ModItems.GUERK_INGOT;
        }
    };
}
