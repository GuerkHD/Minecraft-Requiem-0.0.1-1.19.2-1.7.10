package net.guerkhd.minecraftrequiem.world;

import cpw.mods.fml.common.IWorldGenerator;
import net.guerkhd.minecraftrequiem.block.ModBlocks;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

/**
 * Guerk Ore in the overworld (1.19: placed feature + biome modifier).
 *
 * 1.19 placed the veins in a triangle between y = -114 and y = 86 (peak at y = -14) in a world going down to y = -64,
 * with the deepslate variant below y = 0. Squeezed onto the 1.7.10 height range (y = -64 becomes y = 0) that is a
 * triangle between y = -29 and y = 86 (peak at y = 28), with the deepslate variant below y = 36.
 * Veins that land below the world are skipped, just like in 1.19.
 */
public class GuerkOreGenerator implements IWorldGenerator
{
    private static final int MIN_Y = -29;
    private static final int MAX_Y = 86;
    private static final int DEEPSLATE_BELOW_Y = 36;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        if(world.provider.dimensionId != 0) return;

        int size = MinecraftRequiemCommonConfig.GUERK_ORE_VEIN_SIZE;
        WorldGenMinable stoneOre = new WorldGenMinable(ModBlocks.GUERK_ORE, size, Blocks.stone);
        WorldGenMinable deepOre = new WorldGenMinable(ModBlocks.DEEPSLATE_GUERK_ORE, size, Blocks.stone);

        int half = (MAX_Y - MIN_Y) / 2 + 1;

        for(int i = 0; i < MinecraftRequiemCommonConfig.GUERK_ORE_VEINS_PER_CHUNK; i++)
        {
            // +8: stay inside the area that is safe to generate in, so no neighbouring chunks get loaded.
            int x = chunkX * 16 + 8 + random.nextInt(16);
            int z = chunkZ * 16 + 8 + random.nextInt(16);
            int y = MIN_Y + random.nextInt(half) + random.nextInt(half);

            if(y < 1 || y > 255) continue;

            (y < DEEPSLATE_BELOW_Y ? deepOre : stoneOre).generate(world, random, x, y, z);
        }
    }
}
