package net.guerkhd.minecraftrequiem.client;

import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.guerkhd.minecraftrequiem.stand.StandType;

/**
 * What the client knows about its player's stand, plus the server settings the HUD and the stand renderer need.
 * No client-only classes in here: the packet handlers that fill it are loaded on the server too.
 */
public class ClientStandData
{
    private static boolean standUser;
    private static boolean standActive;
    private static int standID;
    private static boolean bomb;
    private static double maxY;

    // Until the server has sent its own: this installation's config.
    private static int[] costs = StandType.configCosts();
    private static boolean onlyUsersSeeStands = MinecraftRequiemCommonConfig.ONLY_STAND_USERS_SEE_STANDS;

    // System.currentTimeMillis() of the last awakening and the last failed ability, 0 = never.
    private static long awakenedAt;
    private static long abilityFailedAt;
    //ID 0 = The World
    //ID 1 = D4C
    //ID 2 = Magicians Red
    //ID 3 = C-Moon
    //ID 4 = Weather Report
    //ID 5 = Echos Act 3
    //ID 6 = Highway To Hell
    //ID 7 = Killer Queen
    //ID 8 = King Crimson
    //ID 9 = Green Day

    public static boolean getStandUser()
    {
        return standUser;
    }
    public static void setStandUser(boolean standUser)
    {
        ClientStandData.standUser = standUser;
    }

    public static boolean getStandActive()
    {
        return standActive;
    }
    public static void setStandActive(boolean standActive)
    {
        ClientStandData.standActive = standActive;
    }

    public static int getStandID()
    {
        return standID;
    }
    public static void setStandID(int standID)
    {
        ClientStandData.standID = standID;
    }

    public static boolean getBomb()
    {
        return bomb;
    }
    public static void setBomb(boolean bomb)
    {
        ClientStandData.bomb = bomb;
    }

    public static double getMaxY()
    {
        return maxY;
    }
    public static void setMaxY(double maxY)
    {
        ClientStandData.maxY = maxY;
    }

    public static int[] getCosts()
    {
        return costs;
    }

    public static boolean getOnlyUsersSeeStands()
    {
        return onlyUsersSeeStands;
    }

    public static void setServerSettings(int[] costs, boolean onlyUsersSeeStands)
    {
        ClientStandData.costs = costs;
        ClientStandData.onlyUsersSeeStands = onlyUsersSeeStands;
    }

    public static long getAwakenedAt()
    {
        return awakenedAt;
    }

    public static void awakened()
    {
        awakenedAt = System.currentTimeMillis();
    }

    public static long getAbilityFailedAt()
    {
        return abilityFailedAt;
    }

    public static void abilityFailed()
    {
        abilityFailedAt = System.currentTimeMillis();
    }
}
