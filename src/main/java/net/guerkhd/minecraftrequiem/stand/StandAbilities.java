package net.guerkhd.minecraftrequiem.stand;

import com.google.common.collect.Multimap;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.entity.EntitySheerHeartAttack;
import net.guerkhd.minecraftrequiem.entity.EntityStand;
import net.guerkhd.minecraftrequiem.sound.ModSounds;
import net.guerkhd.minecraftrequiem.util.ModExplosion;
import net.guerkhd.minecraftrequiem.util.TeleportHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;

import java.util.List;

/**
 * The ability key (1.19 AbilityC2SPacket). All abilities cost food.
 */
public class StandAbilities
{
    public static void useAbility(EntityPlayerMP player)
    {
        WorldServer level = player.getServerForPlayer();
        PlayerStand stand = PlayerStand.get(player);
        if(stand == null || !stand.getStandUser() || !stand.getStandActive()) return;

        StandType standType = stand.getStandType();
        int food = player.getFoodStats().getFoodLevel();
        int cost = 0;

        switch(standType)
        {
            case THE_WORLD:
                cost = MinecraftRequiemCommonConfig.THE_WORLD_COST;

                if(food >= cost)
                {
                    Vec3 theWorld = TeleportHelper.pick(player, 20);
                    player.setPositionAndUpdate(theWorld.xCoord, theWorld.yCoord, theWorld.zCoord);
                    standSound(level, player, standType, true);

                    for(EntityLivingBase ent : livingEntitiesAround(player, 10))
                    {
                        ent.addPotionEffect(new PotionEffect(ModEffects.TIME_STOP.id, 20, 0));
                    }
                }
                else
                {
                    cost = 0;
                    standSound(level, player, standType, false);
                }
                break;
            case D4C:
                cost = MinecraftRequiemCommonConfig.D4C_COST;

                if(food >= cost && player.dimension != 0)
                {
                    standSound(level, player, standType, true);

                    WorldServer overworld = player.mcServer.worldServerForDimension(0);
                    ChunkCoordinates spawn = overworld.getSpawnPoint();
                    int y = overworld.getTopSolidOrLiquidBlock(spawn.posX, spawn.posZ);
                    TeleportHelper.teleportToDimension(player, 0, spawn.posX + 0.5, y, spawn.posZ + 0.5);
                }
                else if(food >= MinecraftRequiemCommonConfig.THE_WORLD_COST)
                {
                    cost = MinecraftRequiemCommonConfig.THE_WORLD_COST;
                    TeleportHelper.chorusTeleport(level, player);
                }
                else
                {
                    cost = 0;
                    standSound(level, player, standType, false);
                }
                break;
            case MAGICIANS_RED:
                cost = MinecraftRequiemCommonConfig.MAGICIANS_RED_COST;

                if(food >= cost)
                {
                    standSound(level, player, standType, true);
                    shootFireball(level, player);
                }
                else
                {
                    cost = 0;
                    standSound(level, player, standType, false);
                }
                break;
            case C_MOON:
                cost = MinecraftRequiemCommonConfig.C_MOON_COST;

                if(food >= cost)
                {
                    standSound(level, player, standType, true);

                    for(EntityLivingBase ent : livingEntitiesAround(player, 15))
                    {
                        ent.addPotionEffect(new PotionEffect(ModEffects.LEVITATION.id, 40, 9));
                    }
                }
                else
                {
                    cost = 0;
                    standSound(level, player, standType, false);
                }
                break;
            case WEATHER_REPORT:
                cost = MinecraftRequiemCommonConfig.WEATHER_REPORT_COST;

                if(food >= cost)
                {
                    standSound(level, player, standType, true);

                    if(level.isThundering())
                    {
                        Vec3 blitz = TeleportHelper.pick(player, 30);
                        level.addWeatherEffect(new EntityLightningBolt(level, blitz.xCoord, blitz.yCoord, blitz.zCoord));
                    }

                    if(!level.isThundering()) StandManager.setWeather(level, 1200, true, true);
                }
                else
                {
                    cost = 0;
                    standSound(level, player, standType, false);
                }
                break;
            case ECHOS:
                // Act 3 works on hit, see ModEvents.
                break;
            case HIGHWAY_TO_HELL:
                // Hurts the user; the damage is then reflected onto the closest entity (ModEvents).
                ItemStack held = player.getHeldItem();

                if(held != null && (held.getItem() instanceof ItemSword || held.getItem() instanceof ItemTool))
                {
                    player.attackEntityFrom(DamageSource.causePlayerDamage(player), getAttackDamage(held) + 1f);
                }
                else
                {
                    player.attackEntityFrom(DamageSource.causePlayerDamage(player), 1f);
                }
                break;
            case KILLER_QUEEN:
                cost = MinecraftRequiemCommonConfig.KILLER_QUEEN_COST;

                if(player.isSneaking() && !stand.getBomb())
                {
                    spawnHeartAttack(level, player, stand);
                    cost = 0;
                }
                else
                {
                    cost = bomb(level, player, stand, food, cost);
                }
                break;
            case KING_CRIMSON:
                cost = MinecraftRequiemCommonConfig.KING_CRIMSON_COST;

                if(food >= cost)
                {
                    player.addPotionEffect(new PotionEffect(ModEffects.EPITAPH.id, 100, 0));
                }
                else
                {
                    cost = 0;
                    standSound(level, player, standType, false);
                }
                break;
            case GREEN_DAY:
                cost = MinecraftRequiemCommonConfig.GREEN_DAY_COST;

                if(food >= cost)
                {
                    List<EntityPlayerMP> list = level.getEntitiesWithinAABB(EntityPlayerMP.class, player.boundingBox.expand(15, 15, 15));
                    list.remove(player);

                    for(EntityPlayerMP play : list)
                    {
                        PlayerStand playStand = PlayerStand.get(play);
                        if(playStand != null)
                        {
                            playStand.setMaxY(play.posY);
                            playStand.sync();
                        }

                        play.addPotionEffect(new PotionEffect(ModEffects.GREEN_DAY.id, 500, 0));
                    }

                    List<EntityMob> mobList = level.getEntitiesWithinAABB(EntityMob.class, player.boundingBox.expand(15, 15, 15));
                    for(EntityMob mob : mobList) mob.addPotionEffect(new PotionEffect(Potion.poison.id, 500, 0));

                    standSound(level, player, standType, true);
                }
                else
                {
                    cost = 0;
                    standSound(level, player, standType, false);
                }
                break;
            default:
                break;
        }

        if(cost != 0 && StandManager.isSurvival(player)) StandManager.setFoodLevel(player, food - cost);
    }

    /**
     * Living entities around the player, without the player and without stands.
     */
    private static List<EntityLivingBase> livingEntitiesAround(EntityPlayerMP player, double range)
    {
        List<EntityLivingBase> list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, player.boundingBox.expand(range, range, range));
        list.remove(player);
        for(int i = list.size() - 1; i >= 0; i--)
        {
            if(list.get(i) instanceof EntityStand) list.remove(i);
        }
        return list;
    }

    private static void standSound(WorldServer level, EntityPlayerMP player, StandType standType, boolean success)
    {
        String sound = sound(standType, success);
        if(sound != null) StandManager.playSound(level, player, sound, 1f);
    }

    private static String sound(StandType standType, boolean success)
    {
        if(!success) return ModSounds.PLAYER_BURP;

        switch(standType)
        {
            case THE_WORLD: return ModSounds.ZA_WARUDO;
            case D4C: return ModSounds.D4C;
            case MAGICIANS_RED: return ModSounds.CHICKEN_DEATH;
            case C_MOON: return ModSounds.C_MOON;
            case WEATHER_REPORT: return ModSounds.WEATHER_REPORT;
            case KILLER_QUEEN: return ModSounds.KILLER_QUEEN;
            case GREEN_DAY: return ModSounds.GREEN_DAY;
            default: return null;
        }
    }

    private static void shootFireball(WorldServer level, EntityPlayerMP player)
    {
        Vec3 view = player.getLook(1f);

        EntityLargeFireball fireball = new EntityLargeFireball(level, player, view.xCoord, view.yCoord, view.zCoord);
        fireball.field_92057_e = 3; // explosion power
        fireball.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        // The 1.7.10 constructor adds random spread; aim exactly like 1.19 instead.
        fireball.accelerationX = view.xCoord * 0.1;
        fireball.accelerationY = view.yCoord * 0.1;
        fireball.accelerationZ = view.zCoord * 0.1;
        fireball.motionX = view.xCoord * 3;
        fireball.motionY = view.yCoord * 3;
        fireball.motionZ = view.zCoord * 3;
        fireball.velocityChanged = true;
        level.spawnEntityInWorld(fireball);
    }

    /**
     * Attack damage of a sword or tool (1.19 SwordItem.getDamage / DiggerItem.getAttackDamage).
     */
    private static float getAttackDamage(ItemStack stack)
    {
        float damage = 0;
        Multimap<String, AttributeModifier> modifiers = stack.getAttributeModifiers();

        for(AttributeModifier modifier : modifiers.get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName()))
        {
            if(modifier.getOperation() == 0) damage += (float) modifier.getAmount();
        }
        return damage;
    }

    private static int bomb(WorldServer level, EntityPlayerMP player, PlayerStand stand, int food, int cost)
    {
        standSound(level, player, StandType.KILLER_QUEEN, true);

        if(food >= cost)
        {
            List<EntityLivingBase> list = level.getEntitiesWithinAABB(EntityLivingBase.class, player.boundingBox.expand(50, 50, 50));

            for(EntityLivingBase ent : list)
            {
                if(ent.isPotionActive(ModEffects.BOMB))
                {
                    ent.removePotionEffect(ModEffects.BOMB.id);

                    if(ent instanceof EntitySheerHeartAttack)
                    {
                        ModExplosion.explode(level, player, ent.posX, ent.posY, ent.posZ, 3, MinecraftRequiemCommonConfig.HEART_ATTACK_DESTRUCTION);
                        ent.setDead();
                    }
                    else
                    {
                        ModExplosion.explode(level, player, ent.posX, ent.posY + 1, ent.posZ, 2, MinecraftRequiemCommonConfig.BOMB_DESTRUCTION);
                    }
                }
            }

            if(stand.getBomb())
            {
                stand.setBomb(false);
                stand.sync();
            }
        }
        else
        {
            cost = 0;
            standSound(level, player, StandType.KILLER_QUEEN, false);
        }

        return cost;
    }

    private static void spawnHeartAttack(WorldServer level, EntityPlayerMP player, PlayerStand stand)
    {
        Vec3 view = player.getLook(1f);
        EntitySheerHeartAttack heartAttack = new EntitySheerHeartAttack(level);
        // Face the user, so the skull is seen from the front.
        float yaw = player.rotationYaw + 180f;
        heartAttack.setLocationAndAngles(player.posX + view.xCoord * 2, player.posY, player.posZ + view.zCoord * 2, yaw, 0);
        heartAttack.rotationYawHead = yaw;
        heartAttack.renderYawOffset = yaw;
        heartAttack.addPotionEffect(new PotionEffect(ModEffects.BOMB.id, 20, 0));
        level.spawnEntityInWorld(heartAttack);

        stand.setBomb(true);
        stand.sync();
    }
}
