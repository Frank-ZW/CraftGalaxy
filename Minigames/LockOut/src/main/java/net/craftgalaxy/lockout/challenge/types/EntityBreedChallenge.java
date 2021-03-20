package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.entity.EntityBreedEvent;

public abstract class EntityBreedChallenge extends AbstractChallenge<EntityBreedEvent> {

	public EntityBreedChallenge(LockOut lockOut) {
		super(lockOut);
	}
}
