package net.guerkhd.gastrolithicguerk.effect;

/**
 * Eating is blocked in ModEvents while this effect is active.
 */
public class ConstipationEffect extends ModPotion
{
    public ConstipationEffect(int id, boolean isBadEffect, int color)
    {
        super(id, isBadEffect, color, "constipation");
    }
}
