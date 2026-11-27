package net.guerkhd.minecraftrequiem.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSheerHeartAttack extends RenderLiving
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MinecraftRequiem.MOD_ID, "textures/entity/sheer_heart_attack.png");

    public RenderSheerHeartAttack()
    {
        super(new ModelSheerHeartAttack(), 0.3F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return TEXTURE;
    }
}
