package net.craftgalaxy.lockout.challenge.impl.movement;

import net.craftgalaxy.lockout.challenge.types.PlayerMovementChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class ChallengeReachHeightLimit extends PlayerMovementChallenge {

	public ChallengeReachHeightLimit(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handleEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (e.getTo().getBlockY() >= player.getWorld().getMaxHeight()) {
			this.lockOut.completeChallenge(player, this);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getDisplayMessage() {
		return "Reach the height limit!";
	}
}
