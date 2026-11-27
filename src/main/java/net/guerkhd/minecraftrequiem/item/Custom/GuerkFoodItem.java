package net.guerkhd.minecraftrequiem.item.Custom;

import net.guerkhd.minecraftrequiem.food.FoodProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GuerkFoodItem extends ItemFood
{
    private final FoodProperties food;

    public GuerkFoodItem(FoodProperties food)
    {
        super(food.getNutrition(), food.getSaturationModifier(), false);
        this.food = food;
        if(food.canAlwaysEat()) setAlwaysEdible();
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return food.getEatDuration();
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World level, EntityPlayer player)
    {
        food.applyEffects(level, player);
    }
}
