package net.guerkhd.minecraftrequiem.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.guerkhd.minecraftrequiem.client.StandHudOverlay;
import net.guerkhd.minecraftrequiem.item.ModItems;
import net.guerkhd.minecraftrequiem.networking.ModMessages;
import net.guerkhd.minecraftrequiem.networking.packet.AbilityC2SPacket;
import net.guerkhd.minecraftrequiem.networking.packet.StandC2SPacket;
import net.guerkhd.minecraftrequiem.stand.StandType;
import net.guerkhd.minecraftrequiem.util.KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

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
        public void onTooltip(ItemTooltipEvent event)
        {
            List<String> tooltips = event.toolTip;

            if(event.itemStack != null && ModItems.isGuerkTool(event.itemStack.getItem()))
            {
                tooltips.add(getIntex(tooltips.size()), EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocal("message.minecraftrequiem.food_leech"));
            }
        }

        private static int getIntex(int size)
        {
            if(size > 5) size = 5;
            return size;
        }
    }
}
