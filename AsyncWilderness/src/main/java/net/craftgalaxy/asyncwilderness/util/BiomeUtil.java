package net.craftgalaxy.asyncwilderness.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Set;

public class BiomeUtil {

	private static final Set<Biome> INVALID_BIOMES = ImmutableSet.of(
			Biome.OCEAN,
			Biome.COLD_OCEAN,
			Biome.DEEP_COLD_OCEAN,
			Biome.DEEP_FROZEN_OCEAN,
			Biome.DEEP_LUKEWARM_OCEAN,
			Biome.DEEP_OCEAN,
			Biome.DEEP_WARM_OCEAN,
			Biome.FROZEN_OCEAN,
			Biome.LUKEWARM_OCEAN,
			Biome.WARM_OCEAN
	);

	public static boolean isInvalidBiome(Block block) {
		return BiomeUtil.INVALID_BIOMES.contains(block.getBiome());
	}
}
