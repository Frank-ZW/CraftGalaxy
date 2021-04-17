package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerShearEntityEvent;

public abstract class PlayerShearChallenge extends AbstractChallenge<PlayerShearEntityEvent> {

	public PlayerShearChallenge(LockOut lockOut) {
		super(lockOut, PlayerShearEntityEvent.class);
	}
}
