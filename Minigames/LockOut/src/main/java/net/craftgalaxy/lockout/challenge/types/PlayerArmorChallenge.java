package net.craftgalaxy.lockout.challenge.types;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;

public abstract class PlayerArmorChallenge extends AbstractChallenge<PlayerArmorChangeEvent> {

	public PlayerArmorChallenge(LockOut lockOut) {
		super(lockOut, PlayerArmorChangeEvent.class);
	}
}
