package net.guerkhd.minecraftrequiem.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * A skull sitting on the ground. Uses the head part of the skeleton texture.
 */
@SideOnly(Side.CLIENT)
public class ModelSheerHeartAttack extends ModelBase
{
    private final ModelRenderer skull;

    public ModelSheerHeartAttack()
    {
        textureWidth = 64;
        textureHeight = 32;

        skull = new ModelRenderer(this, 0, 0);
        skull.addBox(-4F, -8F, -4F, 8, 8, 8, 0F);
        // Living entity models are drawn with y = 24 at the ground.
        skull.setRotationPoint(0F, 24F, 0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        skull.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entity)
    {
        skull.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
    }
}
