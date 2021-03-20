package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.EntityMountChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.EntityType;

public class ChallengeMountLlama extends EntityMountChallenge {

	public ChallengeMountLlama(LockOut lockOut) {
		super(lockOut, EntityType.LLAMA);
	}

	@Override
	public String getDisplayMessage() {
		return "Ride a Llama!";
	}
}
