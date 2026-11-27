package net.guerkhd.minecraftrequiem.util;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * 1.7.10 cannot remove XP points (addExperience with a negative amount doesn't lower the level).
 */
public class ExperienceHelper
{
    /**
     * XP needed to go from one level to the next, same formula as EntityPlayer.xpBarCap in 1.7.10.
     */
    public static int getExperienceForNextLevel(int level)
    {
        return level >= 30 ? 62 + (level - 30) * 7 : (level >= 15 ? 17 + (level - 15) * 3 : 17);
    }

    /**
     * Total XP needed to reach a level from 0.
     */
    public static int getExperienceForLevel(int level)
    {
        int total = 0;
        for(int i = 0; i < level; i++) total += getExperienceForNextLevel(i);
        return total;
    }

    public static int getTotalExperience(EntityPlayerMP player)
    {
        return getExperienceForLevel(player.experienceLevel) + Math.round(player.experience * player.xpBarCap());
    }

    public static void removeExperiencePoints(EntityPlayerMP player, int points)
    {
        int remaining = Math.max(0, getTotalExperience(player) - points);

        int level = 0;
        while(remaining >= getExperienceForNextLevel(level))
        {
            remaining -= getExperienceForNextLevel(level);
            level++;
        }

        player.experienceLevel = level;
        player.experience = (float) remaining / getExperienceForNextLevel(level);
        player.experienceTotal = Math.max(0, player.experienceTotal - points);
        // Forces the XP bar to be sent to the client again.
        player.addExperienceLevel(0);
    }
}
