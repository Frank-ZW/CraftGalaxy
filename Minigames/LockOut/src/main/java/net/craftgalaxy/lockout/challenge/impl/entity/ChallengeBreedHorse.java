package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.EntityType;

public class ChallengeBreedHorse extends ChallengeEntityBreed {

	public ChallengeBreedHorse(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public String getDisplayMessage() {
		return "Breed two horses!";
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.HORSE;
	}
}
