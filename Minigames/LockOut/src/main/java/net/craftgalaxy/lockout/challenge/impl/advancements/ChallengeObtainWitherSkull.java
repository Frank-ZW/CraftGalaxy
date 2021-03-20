package net.craftgalaxy.lockout.challenge.impl.advancements;

import net.craftgalaxy.lockout.challenge.types.PlayerAdvancementChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;

public class ChallengeObtainWitherSkull extends PlayerAdvancementChallenge {

	public ChallengeObtainWitherSkull(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public String getDisplayMessage() {
		return "Obtain a Wither Skeleton skull!";
	}

	@Override
	public String getNamespaceKey() {
		return "nether/get_wither_skull";
	}
}
