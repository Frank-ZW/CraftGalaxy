package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerBedEnterEvent;

public abstract class PlayerBedEnterChallenge extends AbstractChallenge<PlayerBedEnterEvent> {

	public PlayerBedEnterChallenge(LockOut lockOut) {
		super(lockOut, PlayerBedEnterEvent.class);
	}
}
