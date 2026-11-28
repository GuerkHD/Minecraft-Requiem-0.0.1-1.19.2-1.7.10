package net.guerkhd.gastrolithicguerk.event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.guerkhd.gastrolithicguerk.compat.TinkersCompat;
import net.guerkhd.gastrolithicguerk.effect.ModEffects;
import net.guerkhd.gastrolithicguerk.item.ModItems;
import net.guerkhd.gastrolithicguerk.util.FoodHelper;
import net.minecraft.block.BlockCake;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

/**
 * Forge event bus handlers. Client only ones are in {@link ClientEvents}.
 */
public class ModEvents
{
    /** Tinkers tools with a Guerk binding or guard: Constipation for this many ticks per hit. */
    private static final int CONSTIPATION_ON_HIT = 100;

    /**
     * HIGH: runs before other mods' handlers can cancel the hit, e.g. King Crimson's time skip in Minecraft Requiem.
     * Before the split this ran first in the shared handler, so the order stays the same.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingHurt(LivingHurtEvent event)
    {
        EntityLivingBase entity = event.entityLiving;
        Entity source = event.source.getEntity();

        if(!entity.worldObj.isRemote && source instanceof EntityLivingBase)
        {
            ItemStack held = ((EntityLivingBase) source).getHeldItem();
            if(ModItems.hasFoodLeech(held)) foodLeech((EntityLivingBase) source, entity, ModItems.GUERK_FOOD_LEECH);
            // Stands can't be hurt at all, so they never get here.
            if(TinkersCompat.hasGuerkAccessory(held))
            {
                entity.addPotionEffect(new PotionEffect(ModEffects.CONSTIPATION.id, CONSTIPATION_ON_HIT, 0));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        // Tinkers tools with a Guerk handle: sneak + right click is a snack instead of the tool's own action.
        // The client sends the snack itself (ClientEvents); here the server only blocks the block interaction as well.
        if(!event.entityPlayer.worldObj.isRemote && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
                && TinkersCompat.canSnack(event.entityPlayer, event.entityPlayer.getHeldItem()))
        {
            event.useBlock = Event.Result.DENY;
            event.useItem = Event.Result.DENY;
            event.setCanceled(true);
        }

        // Constipation: cake is eaten by clicking the block, so it isn't covered by the item events below. Both sides,
        // so the client doesn't even try.
        if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && event.entityPlayer.isPotionActive(ModEffects.CONSTIPATION)
                && event.world.getBlock(event.x, event.y, event.z) instanceof BlockCake)
        {
            event.useBlock = Event.Result.DENY;
            event.setCanceled(true);
        }
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

    private static void foodLeech(EntityLivingBase attacker, EntityLivingBase target, int food)
    {
        if(attacker instanceof EntityPlayerMP)
        {
            EntityPlayerMP attackerP = (EntityPlayerMP) attacker;
            int level = attackerP.getFoodStats().getFoodLevel();
            if(level + food <= 20) FoodHelper.setFoodLevel(attackerP, level + food);
        }
        if(target instanceof EntityPlayerMP)
        {
            EntityPlayerMP targetP = (EntityPlayerMP) target;
            int level = targetP.getFoodStats().getFoodLevel();
            if(level - food >= 0) FoodHelper.setFoodLevel(targetP, level - food);
        }
    }
}
