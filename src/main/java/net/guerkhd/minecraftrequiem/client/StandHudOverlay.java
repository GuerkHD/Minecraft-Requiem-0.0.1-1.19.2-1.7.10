package net.guerkhd.minecraftrequiem.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.minecraftrequiem.stand.StandType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

/**
 * Shows the name of the active stand in the top left corner.
 */
@SideOnly(Side.CLIENT)
public class StandHudOverlay
{
    public static void render(ScaledResolution resolution)
    {
        if(!ClientStandData.getStandActive()) return;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRenderer;

        int x = resolution.getScaledWidth() / 30;
        int y = resolution.getScaledHeight() / 18;

        StandType standType = StandType.byId(ClientStandData.getStandID());
        // The color code has to come first, it resets bold.
        font.drawString(standType.getColor() + "" + EnumChatFormatting.BOLD + standType.getHudName(), x, y, 0);
    }
}
