package net.guerkhd.guerk.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Small stand-in for the 1.19 FoodProperties, so items and edible blocks can share one definition.
 */
public class FoodProperties
{
    private final int nutrition;
    private final float saturationMod;
    private final boolean fast;
    private final boolean alwaysEat;
    private final List<EffectEntry> effects;

    private FoodProperties(Builder builder)
    {
        this.nutrition = builder.nutrition;
        this.saturationMod = builder.saturationMod;
        this.fast = builder.fast;
        this.alwaysEat = builder.alwaysEat;
        this.effects = builder.effects;
    }

    public int getNutrition()
    {
        return nutrition;
    }

    public float getSaturationModifier()
    {
        return saturationMod;
    }

    public boolean canAlwaysEat()
    {
        return alwaysEat;
    }

    public int getEatDuration()
    {
        return fast ? 16 : 32;
    }

    public void applyEffects(World level, EntityPlayer player)
    {
        if(level.isRemote) return;

        for(EffectEntry entry : effects)
        {
            if(level.rand.nextFloat() < entry.probability)
            {
                player.addPotionEffect(new PotionEffect(entry.potion.get().id, entry.duration, entry.amplifier));
            }
        }
    }

    /**
     * Potions are looked up lazily: food is defined in static fields, before all potions exist.
     */
    public interface PotionSupplier
    {
        Potion get();
    }

    private static class EffectEntry
    {
        private final PotionSupplier potion;
        private final int duration;
        private final int amplifier;
        private final float probability;

        private EffectEntry(PotionSupplier potion, int duration, int amplifier, float probability)
        {
            this.potion = potion;
            this.duration = duration;
            this.amplifier = amplifier;
            this.probability = probability;
        }
    }

    public static class Builder
    {
        private int nutrition;
        private float saturationMod;
        private boolean fast;
        private boolean alwaysEat;
        private final List<EffectEntry> effects = new ArrayList<EffectEntry>();

        public Builder nutrition(int nutrition)
        {
            this.nutrition = nutrition;
            return this;
        }

        public Builder saturationMod(float saturationMod)
        {
            this.saturationMod = saturationMod;
            return this;
        }

        public Builder fast()
        {
            this.fast = true;
            return this;
        }

        public Builder alwaysEat()
        {
            this.alwaysEat = true;
            return this;
        }

        public Builder effect(PotionSupplier potion, int duration, int amplifier, float probability)
        {
            this.effects.add(new EffectEntry(potion, duration, amplifier, probability));
            return this;
        }

        public FoodProperties build()
        {
            return new FoodProperties(this);
        }
    }
}
