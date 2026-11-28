package net.guerkhd.gastrolithicguerk.event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.gastrolithicguerk.GastrolithicGuerk;
import net.guerkhd.gastrolithicguerk.compat.TinkersCompat;
import net.guerkhd.gastrolithicguerk.item.ModItems;
import net.guerkhd.gastrolithicguerk.networking.ModMessages;
import net.guerkhd.gastrolithicguerk.networking.packet.SnackC2SPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientEvents
{
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event)
    {
        // Block texture atlas: textures for the molten Guerk fluid (Tinkers' Construct).
        if(event.map.getTextureType() == 0) TinkersCompat.registerFluidIcons(event.map);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        // Tinkers tool with a Guerk handle: sneak + right click eats a piece of it (see TinkersCompat.snack).
        EntityPlayer player = event.entityPlayer;
        if(!player.worldObj.isRemote || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;
        if(!TinkersCompat.canSnack(player, player.getHeldItem())) return;

        event.useBlock = Event.Result.DENY;
        event.useItem = Event.Result.DENY;
        event.setCanceled(true);

        if(TinkersCompat.snackCooldownOver(player))
        {
            ModMessages.sendToServer(new SnackC2SPacket());
            player.swingItem();
        }
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event)
    {
        List<String> tooltips = event.toolTip;
        ItemStack stack = event.itemStack;

        if(TinkersCompat.isTinkersTool(stack))
        {
            addTinkersLines(stack, tooltips);
            return;
        }

        if(!ModItems.hasFoodLeech(stack)) return;

        String line = foodLeechLine();

        // Put it right after the last attribute line (they are blue).
        int lastAttribute = -1;
        for(int i = 0; i < tooltips.size(); i++)
        {
            if(tooltips.get(i).startsWith(EnumChatFormatting.BLUE.toString())) lastAttribute = i;
        }

        if(lastAttribute >= 0)
        {
            tooltips.add(lastAttribute + 1, line);
        }
        else
        {
            // No attributes yet (the hoe): start the attribute block like vanilla, with an empty line.
            int index = Math.min(1, tooltips.size());
            tooltips.add(index, line);
            tooltips.add(index, "");
        }
    }

    /**
     * Same format as the vanilla attribute lines, e.g. "+5 Attack Damage".
     */
    private static String foodLeechLine()
    {
        return EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus.0",
                String.valueOf(ModItems.GUERK_FOOD_LEECH), StatCollector.translateToLocal("attribute.name." + GastrolithicGuerk.MOD_ID + ".foodLeech"));
    }

    /**
     * Tinkers tools have their own tooltip, so the Guerk effects go at the end, in the same blue.
     */
    private static void addTinkersLines(ItemStack stack, List<String> tooltips)
    {
        List<String> lines = new ArrayList<String>();
        if(TinkersCompat.hasGuerkHead(stack)) lines.add(foodLeechLine());
        if(TinkersCompat.hasGuerkHandle(stack)) lines.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("tooltip." + GastrolithicGuerk.MOD_ID + ".snack"));
        if(TinkersCompat.hasGuerkAccessory(stack)) lines.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("tooltip." + GastrolithicGuerk.MOD_ID + ".constipation_on_hit"));
        if(lines.isEmpty()) return;

        tooltips.add("");
        tooltips.addAll(lines);
    }
}
