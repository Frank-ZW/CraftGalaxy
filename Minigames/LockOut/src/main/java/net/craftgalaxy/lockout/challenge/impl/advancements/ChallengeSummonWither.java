package net.craftgalaxy.lockout.challenge.impl.advancements;

import net.craftgalaxy.lockout.challenge.types.PlayerAdvancementChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;

public class ChallengeSummonWither extends PlayerAdvancementChallenge {

	public ChallengeSummonWither(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public String getNamespaceKey() {
		return "nether/summon_wither";
	}

	@Override
	public String getDisplayMessage() {
		return "Summon the Wither!";
	}
}
