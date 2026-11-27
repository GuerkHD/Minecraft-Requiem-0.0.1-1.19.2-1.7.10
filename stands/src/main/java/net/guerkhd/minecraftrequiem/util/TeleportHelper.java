package net.guerkhd.minecraftrequiem.util;

import cpw.mods.fml.common.FMLCommonHandler;
import net.guerkhd.minecraftrequiem.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class TeleportHelper
{
    /**
     * Server side version of the 1.19 Entity.pick(distance, 1f, false): where the player is looking, blocks only.
     */
    public static Vec3 pick(EntityLivingBase entity, double distance)
    {
        Vec3 view = entity.getLook(1f);
        double eyeY = entity.posY + entity.getEyeHeight();
        double endX = entity.posX + view.xCoord * distance;
        double endY = eyeY + view.yCoord * distance;
        double endZ = entity.posZ + view.zCoord * distance;

        MovingObjectPosition hit = entity.worldObj.rayTraceBlocks(
                Vec3.createVectorHelper(entity.posX, eyeY, entity.posZ),
                Vec3.createVectorHelper(endX, endY, endZ), false);

        if(hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) return hit.hitVec;
        return Vec3.createVectorHelper(endX, endY, endZ);
    }

    /**
     * Moves a player into another dimension without portals (1.19 ServerPlayer.teleportTo(level, ...)).
     * Mirrors ServerConfigurationManager.transferPlayerToDimension, which can't leave the End properly.
     */
    public static void teleportToDimension(EntityPlayerMP player, int dimension, double x, double y, double z)
    {
        if(player.ridingEntity != null) player.mountEntity(null);
        if(player.riddenByEntity != null) player.riddenByEntity.mountEntity(null);

        if(player.dimension == dimension)
        {
            player.setPositionAndUpdate(x, y, z);
            return;
        }

        MinecraftServer server = player.mcServer;
        ServerConfigurationManager manager = server.getConfigurationManager();
        int oldDimension = player.dimension;
        WorldServer oldWorld = server.worldServerForDimension(oldDimension);
        WorldServer newWorld = server.worldServerForDimension(dimension);

        player.dimension = dimension;
        player.playerNetServerHandler.sendPacket(new S07PacketRespawn(dimension, newWorld.difficultySetting,
                newWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()));
        oldWorld.removePlayerEntityDangerously(player);
        player.isDead = false;

        player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
        newWorld.spawnEntityInWorld(player);
        newWorld.updateEntityWithOptionalForce(player, false);
        player.setWorld(newWorld);

        manager.func_72375_a(player, oldWorld);
        player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
        player.theItemInWorldManager.setWorld(newWorld);
        manager.updateTimeAndWeatherForPlayer(player, newWorld);
        manager.syncPlayerInventory(player);

        for(Object o : player.getActivePotionEffects())
        {
            player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), (PotionEffect) o));
        }
        // Resend the XP bar, it gets lost on the client otherwise.
        player.addExperienceLevel(0);

        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDimension, dimension);
    }

    /**
     * Random teleport like eating a chorus fruit (1.19 ChorusFruitItem + LivingEntity.randomTeleport).
     */
    public static void chorusTeleport(WorldServer level, EntityLivingBase livingEntity)
    {
        double d0 = livingEntity.posX;
        double d1 = livingEntity.posY;
        double d2 = livingEntity.posZ;

        for(int i = 0; i < 16; ++i)
        {
            double d3 = livingEntity.posX + (livingEntity.getRNG().nextDouble() - 0.5) * 16.0;
            double d4 = MathHelper.clamp_double(livingEntity.posY + (double)(livingEntity.getRNG().nextInt(16) - 8), 0.0, (double)(level.getActualHeight() - 1));
            double d5 = livingEntity.posZ + (livingEntity.getRNG().nextDouble() - 0.5) * 16.0;

            if(livingEntity.ridingEntity != null) livingEntity.mountEntity(null);

            EnderTeleportEvent event = new EnderTeleportEvent(livingEntity, d3, d4, d5, 0);
            if(MinecraftForge.EVENT_BUS.post(event)) return;

            if(randomTeleport(level, livingEntity, event.targetX, event.targetY, event.targetZ))
            {
                level.playSoundEffect(d0, d1, d2, ModSounds.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                level.playSoundAtEntity(livingEntity, ModSounds.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                level.func_147487_a("portal", d0, d1 + livingEntity.height / 2, d2, 64, livingEntity.width / 2, livingEntity.height / 2, livingEntity.width / 2, 0.5);
                break;
            }
        }
    }

    private static boolean randomTeleport(WorldServer level, EntityLivingBase entity, double x, double y, double z)
    {
        double oldX = entity.posX;
        double oldY = entity.posY;
        double oldZ = entity.posZ;

        int blockX = MathHelper.floor_double(x);
        int blockY = MathHelper.floor_double(y);
        int blockZ = MathHelper.floor_double(z);
        boolean success = false;

        if(level.blockExists(blockX, blockY, blockZ))
        {
            boolean ground = false;
            while(!ground && blockY > 0)
            {
                Block below = level.getBlock(blockX, blockY - 1, blockZ);
                if(below.getMaterial().blocksMovement()) ground = true;
                else
                {
                    --y;
                    --blockY;
                }
            }

            if(ground)
            {
                entity.setPosition(x, y, z);
                if(level.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty() && !level.isAnyLiquid(entity.boundingBox)) success = true;
            }
        }

        if(!success)
        {
            entity.setPosition(oldX, oldY, oldZ);
            return false;
        }

        entity.setPositionAndUpdate(x, y, z);
        return true;
    }
}
