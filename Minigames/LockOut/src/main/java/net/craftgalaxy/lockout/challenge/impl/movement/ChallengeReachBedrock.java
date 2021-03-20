package net.craftgalaxy.lockout.challenge.impl.movement;

import net.craftgalaxy.lockout.challenge.types.PlayerMovementChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;

public class ChallengeReachBedrock extends PlayerMovementChallenge {

	public ChallengeReachBedrock(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handleEvent(PlayerMoveEvent e) {
		if (e.getTo().getBlockY() < 6) {
			Block block = e.getTo().clone().subtract(0.0D, 1.0D, 0.0D).getBlock();
			if (block.getType() == Material.BEDROCK) {
				this.lockOut.completeChallenge(e.getPlayer(), this);
				return true;
			}
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Reach bedrock level!";
	}
}
