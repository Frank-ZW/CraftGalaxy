package net.craftgalaxy.lockout.challenge.impl.movement;

import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.block.Biome;

public class ChallengeExploreMushroomBiome extends ChallengeExploreBiome {

	public ChallengeExploreMushroomBiome(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public String getDisplayMessage() {
		return "Explore a Mooshroom biome!";
	}

	@Override
	public boolean isTargetBiome(Biome biome) {
		return biome == Biome.MUSHROOM_FIELDS || biome == Biome.MUSHROOM_FIELD_SHORE;
	}
}
