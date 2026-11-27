package net.guerkhd.minecraftrequiem.stand;

import net.guerkhd.minecraftrequiem.entity.EntityStand;
import net.guerkhd.minecraftrequiem.sound.ModSounds;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

/**
 * Server side stand handling: summoning (1.19 StandC2SPacket), the arrows (ArrowC2SPacket / RemoverC2SPacket)
 * and keeping the stand entity alive.
 */
public class StandManager
{
    /**
     * Summon key: summons or unsummons the stand.
     */
    public static void toggleStand(EntityPlayerMP player)
    {
        WorldServer level = player.getServerForPlayer();
        PlayerStand stand = PlayerStand.get(player);
        if(stand == null) return;

        if(stand.getStandUser() && !stand.getStandActive())
        {
            playSound(level, player, ModSounds.STAND_SUMMON, 1f);

            stand.activateStand();
            addStandEffects(player, stand);
            stand.sync();

            ensureStandEntity(player, stand);
        }
        else if(stand.getStandUser() && stand.getStandActive())
        {
            playSound(level, player, ModSounds.STAND_UNSUMMON, 1f);

            deactivate(level, player, stand, false);
        }
        else player.addChatMessage(new ChatComponentText("Skill issue."));
    }

    /**
     * Death and leaving the End clear all effects, but the stand stays summoned (see ModEvents.onPlayerCloned).
     */
    public static void onRespawn(EntityPlayerMP player)
    {
        PlayerStand stand = PlayerStand.get(player);
        if(stand != null && stand.getStandUser() && stand.getStandActive()) addStandEffects(player, stand);
    }

    private static void addStandEffects(EntityPlayerMP player, PlayerStand stand)
    {
        if(stand.getStandType() == StandType.MAGICIANS_RED) player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 36000, 0, true));
    }

    /**
     * Stand Arrow / Requiem Arrow: gives a random stand to a player who doesn't have one yet.
     */
    public static void giveStand(EntityPlayerMP player)
    {
        WorldServer level = player.getServerForPlayer();
        PlayerStand stand = PlayerStand.get(player);

        if(stand != null && !stand.getStandUser())
        {
            playSound(level, player, ModSounds.WITHER_DEATH, 0.5f);

            stand.makeStandUser();
            stand.setStandID(level.rand.nextInt(StandType.STAND_COUNT));
            stand.sync();

            StandType standType = stand.getStandType();
            ChatComponentText message = new ChatComponentText(standType.getAwakeningName());
            message.getChatStyle().setBold(true).setColor(standType.getColor());
            player.addChatMessage(message);
        }
    }

    /**
     * Stand Remover Arrow.
     */
    public static void removeStand(EntityPlayerMP player)
    {
        WorldServer level = player.getServerForPlayer();
        PlayerStand stand = PlayerStand.get(player);

        if(stand != null && stand.getStandUser())
        {
            playSound(level, player, ModSounds.WITHER_SPAWN, 0.5f);

            if(stand.getStandActive()) deactivate(level, player, stand, true);

            stand.undoStandUser();
            stand.sync();

            ChatComponentTranslation message = new ChatComponentTranslation("message.minecraftrequiem.stand_remover_messeage");
            message.getChatStyle().setItalic(true).setColor(EnumChatFormatting.GRAY);
            player.addChatMessage(message);
        }
    }

    private static void deactivate(WorldServer level, EntityPlayerMP player, PlayerStand stand, boolean alsoClearRain)
    {
        if(stand.getStandType() == StandType.MAGICIANS_RED) player.removePotionEffect(Potion.fireResistance.id);
        if(stand.getStandType() == StandType.WEATHER_REPORT && (level.isThundering() || (alsoClearRain && level.isRaining()))) clearWeather(level);

        stand.deactivateStand();
        stand.sync();
        removeStandEntity(stand);
    }

    /**
     * Spawns the stand entity if the player's stand is active but the entity is missing
     * (just summoned, after login, respawn, dimension change, ...).
     */
    public static void ensureStandEntity(EntityPlayerMP player, PlayerStand stand)
    {
        boolean wanted = stand.getStandUser() && stand.getStandActive() && stand.getStandType().hasStandEntity();
        EntityStand entity = stand.getStandEntity();

        if(!wanted)
        {
            removeStandEntity(stand);
            return;
        }

        if(entity == null || entity.isDead || entity.worldObj != player.worldObj)
        {
            removeStandEntity(stand);

            EntityStand standEntity = new EntityStand(player.worldObj, player, stand.getStandType());
            if(player.worldObj.spawnEntityInWorld(standEntity)) stand.setStandEntity(standEntity);
        }
    }

    private static void removeStandEntity(PlayerStand stand)
    {
        if(stand.getStandEntity() != null)
        {
            stand.getStandEntity().setDead();
            stand.setStandEntity(null);
        }
    }

    public static void playSound(World level, EntityPlayerMP player, String sound, float volume)
    {
        level.playSoundEffect(player.posX, player.posY, player.posZ, sound, volume, level.rand.nextFloat() * 0.1f + 0.9f);
    }

    /**
     * 1.19 gameMode.isSurvival(): survival or adventure.
     */
    public static boolean isSurvival(EntityPlayerMP player)
    {
        return player.theItemInWorldManager.getGameType().isSurvivalOrAdventure();
    }

    /**
     * FoodStats.setFoodLevel is client-only in 1.7.10, so go through the NBT data instead (keeps saturation as is).
     */
    public static void setFoodLevel(EntityPlayerMP player, int foodLevel)
    {
        FoodStats foodStats = player.getFoodStats();
        NBTTagCompound nbt = new NBTTagCompound();
        foodStats.writeNBT(nbt);
        nbt.setInteger("foodLevel", Math.max(0, Math.min(20, foodLevel)));
        foodStats.readNBT(nbt);
    }

    /**
     * 1.19 setWeatherParameters(clearTime, weatherTime, raining, thundering).
     */
    public static void setWeather(WorldServer level, int weatherTime, boolean raining, boolean thundering)
    {
        WorldInfo info = level.getWorldInfo();
        info.setRainTime(weatherTime);
        info.setThunderTime(weatherTime);
        info.setRaining(raining);
        info.setThundering(thundering);
    }

    public static void clearWeather(WorldServer level)
    {
        setWeather(level, 0, false, false);
    }
}
