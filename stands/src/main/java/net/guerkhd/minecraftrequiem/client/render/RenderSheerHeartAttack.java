package net.guerkhd.minecraftrequiem.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSheerHeartAttack extends RenderLiving
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private static final float SCALE = 0.6F;

    public RenderSheerHeartAttack()
    {
        super(new ModelSheerHeartAttack(), 0.25F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTicks)
    {
        GL11.glScalef(SCALE, SCALE, SCALE);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return TEXTURE;
    }
}
