package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.entity.CreatureSpawnEvent;

public abstract class EntitySpawnChallenge extends AbstractChallenge<CreatureSpawnEvent> {

	public EntitySpawnChallenge(LockOut lockOut) {
		super(lockOut);
	}
}
