package net.guerkhd.guerk.world;

import cpw.mods.fml.common.IWorldGenerator;
import net.guerkhd.guerk.block.ModBlocks;
import net.guerkhd.guerk.config.GuerkConfig;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

/**
 * Guerk Ore (1.19: placed feature + biome modifier).
 *
 * Generates in every dimension, replacing stone only, so it shows up wherever there is stone
 * (overworld and modded dimensions, but not the Nether or the End).
 *
 * 1.19 placed the veins in a triangle between y = -114 and y = 86 (peak at y = -14) in a world going down to y = -64.
 * Squeezed onto the 1.7.10 height range (y = -64 becomes y = 0) that is a triangle between y = -29 and y = 86
 * (peak at y = 28). Veins that land below the world are skipped, just like in 1.19.
 */
public class GuerkOreGenerator implements IWorldGenerator
{
    private static final int MIN_Y = -29;
    private static final int MAX_Y = 86;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        WorldGenMinable guerkOre = new WorldGenMinable(ModBlocks.GUERK_ORE, GuerkConfig.GUERK_ORE_VEIN_SIZE, Blocks.stone);

        int half = (MAX_Y - MIN_Y) / 2 + 1;
        int maxY = world.getActualHeight() - 1;

        for(int i = 0; i < GuerkConfig.GUERK_ORE_VEINS_PER_CHUNK; i++)
        {
            // +8: stay inside the area that is safe to generate in, so no neighbouring chunks get loaded.
            int x = chunkX * 16 + 8 + random.nextInt(16);
            int z = chunkZ * 16 + 8 + random.nextInt(16);
            int y = MIN_Y + random.nextInt(half) + random.nextInt(half);

            if(y < 1 || y > maxY) continue;

            guerkOre.generate(world, random, x, y, z);
        }
    }
}
