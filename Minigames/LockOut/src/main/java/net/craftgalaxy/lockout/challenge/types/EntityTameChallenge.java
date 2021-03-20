package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.entity.EntityTameEvent;

public abstract class EntityTameChallenge extends AbstractChallenge<EntityTameEvent> {

	public EntityTameChallenge(LockOut lockOut) {
		super(lockOut);
	}
}
