package net.guerkhd.minecraftrequiem.block;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.item.ModCreativeModeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlock extends Block
{
    /**
     * @param harvestLevel 1 = stone pickaxe, 2 = iron, 3 = diamond (1.19: needs_*_tool tags)
     */
    public ModBlock(String name, float strength, int harvestLevel)
    {
        super(Material.rock);

        setBlockName(MinecraftRequiem.MOD_ID + "." + name);
        setBlockTextureName(MinecraftRequiem.MOD_ID + ":" + name);
        setCreativeTab(ModCreativeModeTab.MINECRAFTREQUIEM_TAB);
        // 1.19 strength(f) sets hardness and blast resistance to f; setHardness does both here as well.
        setHardness(strength);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", harvestLevel);
    }
}
