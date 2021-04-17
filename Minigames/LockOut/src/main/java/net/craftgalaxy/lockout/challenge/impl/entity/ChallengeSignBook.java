package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.PlayerEditBookChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerEditBookEvent;

public class ChallengeSignBook extends PlayerEditBookChallenge {

	public ChallengeSignBook(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(PlayerEditBookEvent e) {
		if (e.isSigning()) {
			this.lockOut.completeChallenge(e.getPlayer(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Sign a book!";
	}
}
