package net.guerkhd.minecraftrequiem.effect;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;

/**
 * Base class for the mod's effects (1.19: MobEffect).
 *
 * Unlike 1.19, adding or removing potion effects from inside {@link Potion#performEffect} crashes 1.7.10
 * (ConcurrentModificationException), so the per-tick logic lives in {@link #livingTick}, which is called from
 * LivingUpdateEvent on both sides.
 */
public class ModPotion extends Potion
{
    private final ResourceLocation icon;
    private static Field floatingTickCount;

    public ModPotion(int id, boolean isBadEffect, int color, String name)
    {
        super(id, isBadEffect, color);
        setPotionName("effect." + MinecraftRequiem.MOD_ID + "." + name);
        this.icon = new ResourceLocation(MinecraftRequiem.MOD_ID, "textures/mob_effect/" + name + ".png");
    }

    /**
     * Called every tick on both sides for every living entity that has this effect.
     */
    public void livingTick(EntityLivingBase livingEntity, int amplifier) {    }

    /**
     * Keeps the server from kicking a player who hangs in the air because of an effect ("kicked for floating too long",
     * after 4 seconds unless allow-flight is on). Vanilla 1.9+ makes the same exception for Levitation.
     */
    protected static void allowFloating(EntityLivingBase livingEntity)
    {
        if(!(livingEntity instanceof EntityPlayerMP) || ((EntityPlayerMP) livingEntity).playerNetServerHandler == null) return;

        try
        {
            if(floatingTickCount == null) floatingTickCount = ReflectionHelper.findField(NetHandlerPlayServer.class, "floatingTickCount", "field_147365_f");
            floatingTickCount.setInt(((EntityPlayerMP) livingEntity).playerNetServerHandler, 0);
        }
        catch(Exception e)
        {
            // Not worth crashing over; at worst the old kick stays.
        }
    }

    protected boolean hasIcon()
    {
        return true;
    }

    @Override
    public boolean isReady(int duration, int amplifier)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasStatusIcon()
    {
        // The vanilla icon sheet is not used, see renderInventoryEffect.
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
    {
        if(!hasIcon()) return;

        mc.getTextureManager().bindTexture(icon);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        // Same spot and size as vanilla effect icons.
        double left = x + 6, top = y + 7, size = 18;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(left, top + size, 0, 0, 1);
        tessellator.addVertexWithUV(left + size, top + size, 0, 1, 1);
        tessellator.addVertexWithUV(left + size, top, 0, 1, 0);
        tessellator.addVertexWithUV(left, top, 0, 0, 0);
        tessellator.draw();
    }
}
