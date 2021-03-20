package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.entity.EntityDeathEvent;

public abstract class EntityDeathChallenge extends AbstractChallenge<EntityDeathEvent> {

	public EntityDeathChallenge(LockOut lockOut) {
		super(lockOut);
	}
}
