package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public abstract class PlayerEntityInteractChallenge extends AbstractChallenge<PlayerInteractEntityEvent> {

	public PlayerEntityInteractChallenge(LockOut lockOut) {
		super(lockOut, PlayerInteractEntityEvent.class);
	}
}
