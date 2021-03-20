package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerEditBookEvent;

public abstract class PlayerEditBookChallenge extends AbstractChallenge<PlayerEditBookEvent> {

	public PlayerEditBookChallenge(LockOut lockOut) {
		super(lockOut);
	}
}
