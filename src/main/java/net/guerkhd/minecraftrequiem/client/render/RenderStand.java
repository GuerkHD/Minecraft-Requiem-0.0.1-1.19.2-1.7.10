package net.guerkhd.minecraftrequiem.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.entity.EntityStand;
import net.guerkhd.minecraftrequiem.stand.StandType;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import java.util.EnumMap;
import java.util.Map;

/**
 * Draws each stand with its own skin and model (1.19: an invisible zombie in dyed leather armor).
 */
@SideOnly(Side.CLIENT)
public class RenderStand extends RenderLiving
{
    private final Map<StandType, ModelStand> models = new EnumMap<StandType, ModelStand>(StandType.class);
    private final Map<StandType, ResourceLocation> textures = new EnumMap<StandType, ResourceLocation>(StandType.class);

    public RenderStand()
    {
        super(new ModelStand(StandType.UNKNOWN), 0.3F);

        for(StandType standType : StandType.values())
        {
            if(!standType.hasStandEntity()) continue;
            models.put(standType, new ModelStand(standType));
            textures.put(standType, new ResourceLocation(MinecraftRequiem.MOD_ID, "textures/entity/stand/" + standType.getTextureName() + ".png"));
        }
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks)
    {
        ModelStand model = models.get(((EntityStand) entity).getStandType());
        // Nothing to draw until the server has told the client which stand it is.
        if(model == null) return;

        mainModel = model;
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return textures.get(((EntityStand) entity).getStandType());
    }
}
