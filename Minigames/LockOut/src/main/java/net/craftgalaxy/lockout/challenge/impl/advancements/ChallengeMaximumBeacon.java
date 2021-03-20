package net.craftgalaxy.lockout.challenge.impl.advancements;

import net.craftgalaxy.lockout.challenge.types.PlayerAdvancementChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;

public class ChallengeMaximumBeacon extends PlayerAdvancementChallenge {

	public ChallengeMaximumBeacon(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public String getDisplayMessage() {
		return "Build a maxed out beacon!";
	}

	@Override
	public String getNamespaceKey() {
		return "nether/create_full_beacon";
	}
}
