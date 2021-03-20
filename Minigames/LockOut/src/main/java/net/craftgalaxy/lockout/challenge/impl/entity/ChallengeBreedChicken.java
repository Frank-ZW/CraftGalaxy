package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.EntityType;

public class ChallengeBreedChicken extends ChallengeEntityBreed {

	public ChallengeBreedChicken(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public String getDisplayMessage() {
		return "Breed two chickens!";
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.CHICKEN;
	}
}
