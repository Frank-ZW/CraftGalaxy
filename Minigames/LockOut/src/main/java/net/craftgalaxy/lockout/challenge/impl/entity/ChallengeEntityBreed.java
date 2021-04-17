package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.EntityBreedChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityBreedEvent;

public abstract class ChallengeEntityBreed extends EntityBreedChallenge {

	public ChallengeEntityBreed(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(EntityBreedEvent e) {
		if (e.getBreeder() instanceof Player && e.getEntityType() == this.getEntityType()) {
			this.lockOut.completeChallenge((Player) e.getBreeder(), this);
			return true;
		}

		return false;
	}

	public abstract EntityType getEntityType();
}
