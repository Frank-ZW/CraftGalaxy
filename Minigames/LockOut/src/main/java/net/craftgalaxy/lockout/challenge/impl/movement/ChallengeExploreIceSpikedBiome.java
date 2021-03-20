package net.craftgalaxy.lockout.challenge.impl.movement;

import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.block.Biome;

public class ChallengeExploreIceSpikedBiome extends ChallengeExploreBiome {

	public ChallengeExploreIceSpikedBiome(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public String getDisplayMessage() {
		return "Explore an Ice Spike Biome!";
	}

	@Override
	public boolean isTargetBiome(Biome biome) {
		return biome == Biome.ICE_SPIKES;
	}
}
