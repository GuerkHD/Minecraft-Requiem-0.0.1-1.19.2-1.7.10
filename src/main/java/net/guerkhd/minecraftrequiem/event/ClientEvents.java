package net.guerkhd.minecraftrequiem.event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.guerkhd.minecraftrequiem.client.StandHudOverlay;
import net.guerkhd.minecraftrequiem.compat.TinkersCompat;
import net.guerkhd.minecraftrequiem.item.ModItems;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.AbilityC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.SnackC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandC2SPacket;
import net.guerkhd.minecraftrequiem.stand.StandType;
import net.guerkhd.minecraftrequiem.util.KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientEvents
{
    /** FML event bus (input and ticks). */
    public static class ClientFmlEvents
    {
        @SubscribeEvent
        public void onKeyInput(InputEvent.KeyInputEvent event)
        {
            if(KeyBinding.SUMMONING_KEY.isPressed())
            {
                ModMessages.sendToServer(new StandC2SPacket());
            }
            else if(KeyBinding.ABILITY_KEY.isPressed() && ClientStandData.getStandActive())
            {
                ModMessages.sendToServer(new AbilityC2SPacket());
            }
        }

        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent event)
        {
            // C-Moon: climb walls.
            if(event.phase == TickEvent.Phase.END && event.player == Minecraft.getMinecraft().thePlayer
                    && StandType.byId(ClientStandData.getStandID()) == StandType.C_MOON && ClientStandData.getStandActive()
                    && event.player.isCollidedHorizontally)
            {
                event.player.motionY = 0.2;
            }
        }
    }

    /** Forge event bus (rendering and tooltips). */
    public static class ClientForgeEvents
    {
        @SubscribeEvent
        public void onRenderOverlay(RenderGameOverlayEvent.Post event)
        {
            if(event.type == RenderGameOverlayEvent.ElementType.ALL) StandHudOverlay.render(event.resolution);
        }

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
                    String.valueOf(ModItems.GUERK_FOOD_LEECH), StatCollector.translateToLocal("attribute.name.minecraftrequiem.foodLeech"));
        }

        /**
         * Tinkers tools have their own tooltip, so the Guerk effects go at the end, in the same blue.
         */
        private static void addTinkersLines(ItemStack stack, List<String> tooltips)
        {
            List<String> lines = new ArrayList<String>();
            if(TinkersCompat.hasGuerkHead(stack)) lines.add(foodLeechLine());
            if(TinkersCompat.hasGuerkHandle(stack)) lines.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("tooltip.minecraftrequiem.snack"));
            if(TinkersCompat.hasGuerkAccessory(stack)) lines.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("tooltip.minecraftrequiem.constipation_on_hit"));
            if(lines.isEmpty()) return;

            tooltips.add("");
            tooltips.addAll(lines);
        }
    }
}
