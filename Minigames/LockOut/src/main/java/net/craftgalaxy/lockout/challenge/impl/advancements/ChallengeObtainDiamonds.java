package net.craftgalaxy.lockout.challenge.impl.advancements;

import net.craftgalaxy.lockout.challenge.types.PlayerAdvancementChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;

public class ChallengeObtainDiamonds extends PlayerAdvancementChallenge {

	public ChallengeObtainDiamonds(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public String getDisplayMessage() {
		return "Find diamonds!";
	}

	@Override
	public String getNamespaceKey() {
		return "story/mine_diamond";
	}
}
