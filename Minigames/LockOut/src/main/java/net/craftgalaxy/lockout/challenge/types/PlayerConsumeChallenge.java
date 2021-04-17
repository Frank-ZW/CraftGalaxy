package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public abstract class PlayerConsumeChallenge extends AbstractChallenge<PlayerItemConsumeEvent> {

	public PlayerConsumeChallenge(LockOut lockOut) {
		super(lockOut, PlayerItemConsumeEvent.class);
	}
}
