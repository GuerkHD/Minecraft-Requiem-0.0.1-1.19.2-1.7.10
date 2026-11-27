package net.guerkhd.minecraftrequiem.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.guerkhd.minecraftrequiem.effect.ModEffects;
import net.guerkhd.minecraftrequiem.sound.ModSounds;
import net.guerkhd.minecraftrequiem.stand.PlayerStand;
import net.guerkhd.minecraftrequiem.stand.StandManager;
import net.guerkhd.minecraftrequiem.stand.StandType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.List;

/**
 * FML event bus handlers (ticks and the item pickup event live there in 1.7.10).
 */
public class ModTickEvents
{
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;

        // 1.19 ran this in both tick phases; once per tick is enough.
        if(event.phase != TickEvent.Phase.END || player.worldObj.isRemote || !(player instanceof EntityPlayerMP)) return;

        PlayerStand stand = PlayerStand.get(player);
        if(stand == null) return;

        StandManager.ensureStandEntity((EntityPlayerMP) player, stand);
        clearLostBomb(player, stand);
        refreshThreeFreeze(player, stand);
        greenDay(player, stand);
        glowClosest(player, stand);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if(event.player instanceof EntityPlayerMP) StandManager.onRespawn((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onItemPickup(PlayerEvent.ItemPickupEvent event)
    {
        EntityPlayer player = event.player;
        String name = player.getCommandSenderName();
        World level = player.worldObj;
        ItemStack stack = event.pickedUp.getEntityItem();

        if(!level.isRemote && name.equals("Knorke75") && stack != null && stack.getItem() == Items.diamond)
        {
            level.playSoundEffect(player.posX, player.posY, player.posZ, ModSounds.CREEPER_PRIMED, 8f, level.rand.nextFloat() * 0.1f + 0.9f);
        }
    }

    private static void greenDay(EntityPlayer player, PlayerStand stand)
    {
        PotionEffect effect = player.getActivePotionEffect(ModEffects.GREEN_DAY);
        if(effect == null) return;

        double down = stand.getMaxY() - player.posY;
        int amplifier = down > 0 ? (int) Math.min(5, Math.round(down)) : 0;

        // 1.19 replaced the effect every tick; only replacing it when the strength changes saves a lot of packets.
        if(effect.getAmplifier() != amplifier)
        {
            int duration = effect.getDuration();
            player.removePotionEffect(ModEffects.GREEN_DAY.id);
            player.addPotionEffect(new PotionEffect(ModEffects.GREEN_DAY.id, duration, amplifier, true));
        }
    }

    /**
     * Killer Queen: once no bomb is left in range (the target died, despawned or got too far away), a new one can be placed.
     * 1.19 kept the bomb marked as placed until it was detonated into nothing, which cost food.
     * Same 50 block range in which ModEvents keeps bombs alive and StandAbilities detonates them.
     */
    private static void clearLostBomb(EntityPlayer player, PlayerStand stand)
    {
        if(!stand.getBomb() || player.ticksExisted % 10 != 0) return;

        List<EntityLivingBase> list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, player.boundingBox.expand(50, 50, 50));
        for(EntityLivingBase ent : list)
        {
            if(ent.isPotionActive(ModEffects.BOMB)) return;
        }

        stand.setBomb(false);
        stand.sync();
    }

    private static void refreshThreeFreeze(EntityPlayer player, PlayerStand stand)
    {
        if(stand.getStandType() == StandType.ECHOS && stand.getStandActive())
        {
            List<EntityLivingBase> list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, player.boundingBox.expand(5, 5, 5));
            list.remove(player);

            for(EntityLivingBase ent : list)
            {
                if(ent.isPotionActive(ModEffects.THREE_FREEZE))
                {
                    ent.addPotionEffect(new PotionEffect(ModEffects.THREE_FREEZE.id, 20, 0));
                }
            }
        }
    }

    /**
     * 1.19 made the closest entity glow. The Glowing effect doesn't exist in 1.7.10, so it gets marked with particles instead.
     */
    private static void glowClosest(EntityPlayer player, PlayerStand stand)
    {
        if(stand.getStandType() == StandType.HIGHWAY_TO_HELL && stand.getStandActive() && player.ticksExisted % 2 == 0)
        {
            EntityLivingBase closest = getClosest(player, 20);
            if(closest == null) return;

            S2APacketParticles packet = new S2APacketParticles("witchMagic",
                    (float) closest.posX, (float) (closest.boundingBox.minY + closest.height / 2), (float) closest.posZ,
                    closest.width / 2, closest.height / 2, closest.width / 2, 0f, 3);

            for(Object o : player.worldObj.playerEntities)
            {
                EntityPlayerMP viewer = (EntityPlayerMP) o;
                if(viewer.getDistanceSqToEntity(closest) < 64 * 64) viewer.playerNetServerHandler.sendPacket(packet);
            }
        }
    }

    /**
     * The closest living entity around the user, ignoring the user and stands.
     */
    static EntityLivingBase getClosest(EntityLivingBase user, double range)
    {
        List<EntityLivingBase> list = user.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, user.boundingBox.expand(range, range, range));
        EntityLivingBase closest = null;

        for(EntityLivingBase livingEntity : list)
        {
            if(livingEntity == user || ModEvents.isStand(livingEntity)) continue;

            if(closest == null || livingEntity.getDistanceSqToEntity(user) < closest.getDistanceSqToEntity(user))
            {
                closest = livingEntity;
            }
        }

        return closest;
    }
}
