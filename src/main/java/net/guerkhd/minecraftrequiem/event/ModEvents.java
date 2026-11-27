package net.guerkhd.minecraftrequiem.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.effect.ModPotion;
import net.guerkhd.minecraftrequiem.entity.EntityStand;
import net.guerkhd.minecraftrequiem.item.ModItems;
import net.guerkhd.minecraftrequiem.sound.ModSounds;
import net.guerkhd.minecraftrequiem.stand.PlayerStand;
import net.guerkhd.minecraftrequiem.stand.StandManager;
import net.guerkhd.minecraftrequiem.stand.StandType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

import java.util.List;

/**
 * Forge event bus handlers. Tick based handlers are in {@link ModTickEvents}, client only ones in {@link ClientEvents}.
 */
public class ModEvents
{
    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event)
    {
        if(event.entity instanceof EntityPlayer && PlayerStand.get((EntityPlayer) event.entity) == null)
        {
            PlayerStand.register((EntityPlayer) event.entity);
        }
    }

    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event)
    {
        // Keeps the stand on death and when coming back from the End.
        PlayerStand oldStore = PlayerStand.get(event.original);
        PlayerStand newStore = PlayerStand.get(event.entityPlayer);

        if(oldStore != null && newStore != null) newStore.copyFrom(oldStore);
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event)
    {
        if(!event.world.isRemote && event.entity instanceof EntityPlayerMP)
        {
            PlayerStand stand = PlayerStand.get((EntityPlayerMP) event.entity);
            if(stand != null) stand.sync();
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        EntityLivingBase entity = event.entityLiving;
        Entity source = event.source.getEntity();
        float amount = event.ammount;

        if(!entity.worldObj.isRemote)
        {
            if(source instanceof EntityLivingBase)
            {
                ItemStack held = ((EntityLivingBase) source).getHeldItem();
                if(held != null && ModItems.isGuerkTool(held.getItem())) foodLeech((EntityLivingBase) source, entity, ModItems.GUERK_FOOD_LEECH);
            }

            threeFreeze(entity, source);
            reflectDmg(entity, amount);
            applyBomb(entity, source);
            timeSkip(entity, source, event);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase entity = event.entityLiving;

        // Effect ticks (see ModPotion for why they aren't in Potion.performEffect).
        for(ModPotion potion : ModEffects.all())
        {
            PotionEffect effect = entity.getActivePotionEffect(potion);
            if(effect != null) potion.livingTick(entity, effect.getAmplifier());
        }

        if(!entity.worldObj.isRemote) refreshBomb(entity);
    }

    @SubscribeEvent
    public void onPlayerEatStart(PlayerUseItemEvent.Start event)
    {
        if(isConstipatedEating(event.entityPlayer, event.item)) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlayerEatTick(PlayerUseItemEvent.Tick event)
    {
        if(isConstipatedEating(event.entityPlayer, event.item)) event.setCanceled(true);
    }

    private static boolean isConstipatedEating(EntityPlayer player, ItemStack stack)
    {
        return stack != null && player.isPotionActive(ModEffects.CONSTIPATION) && stack.getItemUseAction() == EnumAction.eat;
    }

    private static void refreshBomb(EntityLivingBase entity)
    {
        if(entity.isPotionActive(ModEffects.BOMB))
        {
            List<EntityPlayer> list = entity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, entity.boundingBox.expand(50, 50, 50));

            for(EntityPlayer player : list)
            {
                if(getBomb(player)) entity.addPotionEffect(new PotionEffect(ModEffects.BOMB.id, 20, 0));
            }
        }
    }

    private static void threeFreeze(EntityLivingBase entity, Entity source)
    {
        if(!(source instanceof EntityPlayerMP) || isStand(entity)) return;
        EntityPlayerMP player = (EntityPlayerMP) source;
        if(getStandType(player) != StandType.ECHOS || !standIsActive(player)) return;

        int food = player.getFoodStats().getFoodLevel();

        if(food >= MinecraftRequiemCommonConfig.ECHOS_ACT_3_COST)
        {
            entity.addPotionEffect(new PotionEffect(ModEffects.THREE_FREEZE.id, 20, 0));
            if(StandManager.isSurvival(player)) StandManager.setFoodLevel(player, food - MinecraftRequiemCommonConfig.ECHOS_ACT_3_COST);
            StandManager.playSound(entity.worldObj, player, ModSounds.THREE_FREEZE, 1f);
        }
        else
        {
            StandManager.playSound(entity.worldObj, player, ModSounds.PLAYER_BURP, 1f);
        }
    }

    private static void reflectDmg(EntityLivingBase entity, float amount)
    {
        if(entity instanceof EntityPlayer && getStandType((EntityPlayer) entity) == StandType.HIGHWAY_TO_HELL && standIsActive((EntityPlayer) entity))
        {
            EntityLivingBase closest = ModTickEvents.getClosest(entity, 20);
            if(closest != null) closest.attackEntityFrom(DamageSource.magic, amount);
        }
    }

    private static void applyBomb(EntityLivingBase entity, Entity source)
    {
        if(source instanceof EntityPlayerMP && !isStand(entity))
        {
            EntityPlayerMP player = (EntityPlayerMP) source;
            PlayerStand stand = PlayerStand.get(player);

            if(stand != null && stand.getStandType() == StandType.KILLER_QUEEN && stand.getStandActive() && !stand.getBomb())
            {
                entity.addPotionEffect(new PotionEffect(ModEffects.BOMB.id, 20, 0));

                stand.setBomb(true);
                stand.sync();
            }
        }
    }

    private static void timeSkip(EntityLivingBase target, Entity source, LivingHurtEvent event)
    {
        if(source instanceof EntityLivingBase && target instanceof EntityPlayerMP && target.isPotionActive(ModEffects.EPITAPH))
        {
            EntityLivingBase entity = (EntityLivingBase) source;
            EntityPlayerMP player = (EntityPlayerMP) target;

            Vec3 behind = behindTP(entity, 0);
            player.playerNetServerHandler.setPlayerLocation(behind.xCoord, behind.yCoord, behind.zCoord, entity.rotationYaw, entity.rotationPitch);
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 40, 1));
            event.setCanceled(true);
            player.removePotionEffect(ModEffects.EPITAPH.id);

            StandManager.playSound(target.worldObj, player, ModSounds.KING_CRIMSON, 1f);
        }
    }

    private static void foodLeech(EntityLivingBase attacker, EntityLivingBase target, int food)
    {
        if(attacker instanceof EntityPlayerMP)
        {
            EntityPlayerMP attackerP = (EntityPlayerMP) attacker;
            int level = attackerP.getFoodStats().getFoodLevel();
            if(level + food <= 20) StandManager.setFoodLevel(attackerP, level + food);
        }
        if(target instanceof EntityPlayerMP)
        {
            EntityPlayerMP targetP = (EntityPlayerMP) target;
            int level = targetP.getFoodStats().getFoodLevel();
            if(level - food >= 0) StandManager.setFoodLevel(targetP, level - food);
        }
    }

    /**
     * The spot 1.5 blocks behind an entity.
     */
    private static Vec3 behindTP(EntityLivingBase target, double yOffset)
    {
        Vec3 view = target.getLook(1f);
        return Vec3.createVectorHelper(target.posX - view.xCoord * 1.5, target.boundingBox.minY + yOffset, target.posZ - view.zCoord * 1.5);
    }

    static StandType getStandType(EntityPlayer player)
    {
        PlayerStand stand = PlayerStand.get(player);
        return stand != null ? stand.getStandType() : StandType.UNKNOWN;
    }

    static boolean standIsActive(EntityPlayer player)
    {
        PlayerStand stand = PlayerStand.get(player);
        return stand != null && stand.getStandActive();
    }

    static boolean getBomb(EntityPlayer player)
    {
        PlayerStand stand = PlayerStand.get(player);
        return stand != null && stand.getBomb();
    }

    static boolean isStand(Entity entity)
    {
        return entity instanceof EntityStand;
    }
}
