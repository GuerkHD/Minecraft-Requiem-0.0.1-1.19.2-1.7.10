package net.guerkhd.minecraftrequiem.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

/**
 * The stand body is invisible, only its dyed armor is drawn (like the invisible zombie in 1.19).
 */
@SideOnly(Side.CLIENT)
public class RenderStand extends RenderBiped
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/zombie/zombie.png");

    public RenderStand()
    {
        super(new ModelBiped(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity)
    {
        // RenderBiped has no texture of its own; it is still bound even though the body is invisible.
        return TEXTURE;
    }
}
