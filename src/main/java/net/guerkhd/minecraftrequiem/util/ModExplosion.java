package net.guerkhd.minecraftrequiem.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Explosions with the three 1.19 block interaction modes.
 */
public class ModExplosion
{
    public enum BlockInteraction
    {
        /** No block damage. */
        NONE,
        /** Blocks are broken and always dropped. */
        BREAK,
        /** Blocks are destroyed and only some drop (TNT behaviour). */
        DESTROY
    }

    public static void explode(WorldServer level, Entity source, double x, double y, double z, float power, BlockInteraction interaction)
    {
        if(interaction != BlockInteraction.BREAK)
        {
            level.newExplosion(source, x, y, z, power, false, interaction == BlockInteraction.DESTROY);
            return;
        }

        // Same as WorldServer.newExplosion, but every block drops.
        Explosion explosion = new Explosion(level, source, x, y, z, power);
        explosion.isFlaming = false;
        explosion.isSmoking = true;
        if(ForgeEventFactory.onExplosionStart(level, explosion)) return;

        explosion.doExplosionA();

        for(Object o : explosion.affectedBlockPositions)
        {
            ChunkPosition pos = (ChunkPosition) o;
            Block block = level.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);

            if(block.getMaterial() != Material.air)
            {
                if(block.canDropFromExplosion(explosion))
                {
                    int meta = level.getBlockMetadata(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
                    block.dropBlockAsItemWithChance(level, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, meta, 1.0F, 0);
                }
                block.onBlockExploded(level, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, explosion);
            }
        }

        // Sound, entity damage was already done in doExplosionA; the blocks are air now and get skipped.
        explosion.doExplosionB(false);

        for(Object o : level.playerEntities)
        {
            EntityPlayer player = (EntityPlayer) o;
            if(player.getDistanceSq(x, y, z) < 4096.0D)
            {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S27PacketExplosion(x, y, z, power,
                        explosion.affectedBlockPositions, (Vec3) explosion.func_77277_b().get(player)));
            }
        }
    }
}
