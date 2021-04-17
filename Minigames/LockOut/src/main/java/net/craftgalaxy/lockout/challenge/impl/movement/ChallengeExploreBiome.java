package net.craftgalaxy.lockout.challenge.impl.movement;

import net.craftgalaxy.lockout.challenge.types.PlayerMovementChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.block.Biome;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class ChallengeExploreBiome extends PlayerMovementChallenge {

	public ChallengeExploreBiome(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(PlayerMoveEvent e) {
		if (this.isTargetBiome(e.getTo().getBlock().getBiome())) {
			this.lockOut.completeChallenge(e.getPlayer(), this);
			return true;
		} else {
			return false;
		}
	}

	public abstract boolean isTargetBiome(Biome biome);
}
