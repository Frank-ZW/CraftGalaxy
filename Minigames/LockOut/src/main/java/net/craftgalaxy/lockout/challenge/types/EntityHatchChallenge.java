package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerEggThrowEvent;

public abstract class EntityHatchChallenge extends AbstractChallenge<PlayerEggThrowEvent> {

	public EntityHatchChallenge(LockOut lockOut) {
		super(lockOut, PlayerEggThrowEvent.class);
	}
}
