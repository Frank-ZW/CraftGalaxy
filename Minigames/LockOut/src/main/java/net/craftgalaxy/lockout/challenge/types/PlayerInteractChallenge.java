package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class PlayerInteractChallenge extends AbstractChallenge<PlayerInteractEvent> {

	public PlayerInteractChallenge(LockOut lockOut) {
		super(lockOut, PlayerInteractEvent.class);
	}
}
