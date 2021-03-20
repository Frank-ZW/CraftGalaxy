package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class PlayerMovementChallenge extends AbstractChallenge<PlayerMoveEvent> {

	public PlayerMovementChallenge(LockOut lockOut) {
		super(lockOut);
	}
}
