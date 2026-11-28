package net.guerkhd.gastrolithicguerk.block;

import net.guerkhd.gastrolithicguerk.GastrolithicGuerk;
import net.guerkhd.gastrolithicguerk.food.FoodProperties;
import net.guerkhd.gastrolithicguerk.item.ModCreativeModeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlock extends Block
{
    private final FoodProperties food;

    /**
     * @param harvestLevel 1 = stone pickaxe, 2 = iron, 3 = diamond (1.19: needs_*_tool tags)
     */
    public ModBlock(String name, float strength, int harvestLevel, FoodProperties food)
    {
        super(Material.rock);
        this.food = food;

        setBlockName(GastrolithicGuerk.MOD_ID + "." + name);
        setBlockTextureName(GastrolithicGuerk.MOD_ID + ":" + name);
        setCreativeTab(ModCreativeModeTab.GUERK_TAB);
        // 1.19 strength(f) sets hardness and blast resistance to f; setHardness does both here as well.
        setHardness(strength);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", harvestLevel);
    }

    public FoodProperties getFood()
    {
        return food;
    }
}
