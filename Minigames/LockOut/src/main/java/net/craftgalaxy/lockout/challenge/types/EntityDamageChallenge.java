package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.entity.EntityDamageEvent;

public abstract class EntityDamageChallenge extends AbstractChallenge<EntityDamageEvent> {

	public EntityDamageChallenge(LockOut lockOut) {
		super(lockOut);
	}
}
