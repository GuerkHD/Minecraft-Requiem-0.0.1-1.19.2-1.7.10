package net.guerkhd.minecraftrequiem.block;

import net.guerkhd.minecraftrequiem.food.FoodProperties;
import net.guerkhd.minecraftrequiem.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

/**
 * Drops Raw Guerk (affected by Fortune), drops itself with Silk Touch and gives 1-3 XP.
 * Replaces the 1.19 DropExperienceBlock + loot table.
 */
public class GuerkOreBlock extends ModBlock
{
    private final Random rand = new Random();

    public GuerkOreBlock(String name, float strength, int harvestLevel, FoodProperties food)
    {
        super(name, strength, harvestLevel, food);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return ModItems.RAW_GUERK;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        int bonus = random.nextInt(fortune + 2) - 1;
        if(bonus < 0) bonus = 0;
        return quantityDropped(random) * (bonus + 1);
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune)
    {
        return MathHelper.getRandomIntegerInRange(rand, 1, 3);
    }
}
