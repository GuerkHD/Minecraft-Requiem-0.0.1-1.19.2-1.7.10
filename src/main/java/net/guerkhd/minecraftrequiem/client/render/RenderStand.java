package net.guerkhd.minecraftrequiem.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.client.ClientStandData;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.guerkhd.minecraftrequiem.entity.EntityStand;
import net.guerkhd.minecraftrequiem.stand.StandType;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;

/**
 * Draws each stand with its own skin and model (1.19: an invisible zombie in dyed leather armor),
 * half transparent and, like in the series, only for players who have a stand themselves.
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
            textures.put(standType, new ResourceLocation(MinecraftRequiem.MOD_ID, standType.getSkinPath()));
        }
    }

    /**
     * Server setting (sent on join): players without a stand don't see stands.
     */
    public static boolean visibleToMe()
    {
        return ClientStandData.getStandUser() || !ClientStandData.getOnlyUsersSeeStands();
    }

    /**
     * Client setting. Below 1 the stand is drawn after water and glass (EntityStand.shouldRenderInPass),
     * so they show through it and it doesn't cut holes into them.
     */
    public static float opacity()
    {
        return Math.max(0.1f, Math.min(1f, MinecraftRequiemCommonConfig.STAND_OPACITY));
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTicks)
    {
        if(!visibleToMe()) return;

        ModelStand model = models.get(((EntityStand) entity).getStandType());
        // Nothing to draw until the server has told the client which stand it is.
        if(model == null) return;

        mainModel = model;
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    public void doRenderShadowAndFire(Entity entity, double x, double y, double z, float yaw, float partialTicks)
    {
        // Drawn separately from the model: without this, a hidden stand would still give itself away by its shadow.
        if(visibleToMe()) super.doRenderShadowAndFire(entity, x, y, z, yaw, partialTicks);
    }

    @Override
    protected void renderModel(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch, float scale)
    {
        float opacity = opacity();
        if(opacity >= 1f)
        {
            super.renderModel(entity, limbSwing, limbSwingAmount, age, headYaw, headPitch, scale);
            return;
        }

        bindEntityTexture(entity);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1f, 1f, 1f, opacity);
        mainModel.render(entity, limbSwing, limbSwingAmount, age, headYaw, headPitch, scale);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return textures.get(((EntityStand) entity).getStandType());
    }
}
