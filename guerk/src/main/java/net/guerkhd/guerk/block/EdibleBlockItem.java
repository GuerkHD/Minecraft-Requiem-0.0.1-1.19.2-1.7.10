package net.guerkhd.guerk.block;

import net.guerkhd.guerk.food.FoodProperties;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Block item that can also be eaten (right click into the air), like the 1.19 BlockItem with food properties.
 * Right clicking a block still places it.
 */
public class EdibleBlockItem extends ItemBlock
{
    private final FoodProperties food;

    public EdibleBlockItem(Block block)
    {
        super(block);
        this.food = block instanceof ModBlock ? ((ModBlock) block).getFood() : null;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return food != null ? EnumAction.eat : EnumAction.none;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return food != null ? food.getEatDuration() : 0;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World level, EntityPlayer player)
    {
        if(food != null && player.canEat(food.canAlwaysEat()))
        {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World level, EntityPlayer player)
    {
        if(food == null) return stack;

        --stack.stackSize;
        player.getFoodStats().addStats(food.getNutrition(), food.getSaturationModifier());
        level.playSoundAtEntity(player, "random.burp", 0.5F, level.rand.nextFloat() * 0.1F + 0.9F);
        food.applyEffects(level, player);
        return stack;
    }
}
