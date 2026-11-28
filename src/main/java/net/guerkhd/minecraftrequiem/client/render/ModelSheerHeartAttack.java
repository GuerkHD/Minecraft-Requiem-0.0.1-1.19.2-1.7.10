package net.guerkhd.minecraftrequiem.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Sheer Heart Attack as in the series: a small tank with a skull on the front, a dome, tracks and a knob on the back.
 * Living entity models are drawn with y = 24 at the ground; the front faces -z.
 */
@SideOnly(Side.CLIENT)
public class ModelSheerHeartAttack extends ModelBase
{
    private final ModelRenderer hull;
    private final ModelRenderer dome;
    private final ModelRenderer skull;
    private final ModelRenderer leftTrack;
    private final ModelRenderer rightTrack;
    private final ModelRenderer knob;

    public ModelSheerHeartAttack()
    {
        textureWidth = 64;
        textureHeight = 32;

        hull = part(0, 0, -3, 18, -3, 6, 4, 7, false);
        dome = part(0, 11, -2, 16, -2, 4, 2, 5, false);
        skull = part(26, 0, -2, 17, -4, 4, 4, 1, false);
        leftTrack = part(26, 8, 3, 20, -4, 2, 4, 8, false);
        rightTrack = part(26, 8, -5, 20, -4, 2, 4, 8, true);
        knob = part(50, 0, 1.5F, 13, 1, 1, 3, 1, false);
    }

    private ModelRenderer part(int u, int v, float x, float y, float z, int w, int h, int d, boolean mirror)
    {
        ModelRenderer part = new ModelRenderer(this, u, v);
        part.mirror = mirror;
        part.addBox(x, y, z, w, h, d);
        return part;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        hull.render(scale);
        dome.render(scale);
        skull.render(scale);
        leftTrack.render(scale);
        rightTrack.render(scale);
        knob.render(scale);
    }
}
