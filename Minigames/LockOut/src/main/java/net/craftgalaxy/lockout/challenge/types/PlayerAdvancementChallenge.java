package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public abstract class PlayerAdvancementChallenge extends AbstractChallenge<PlayerAdvancementDoneEvent> {

	public PlayerAdvancementChallenge(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handleEvent(PlayerAdvancementDoneEvent e) {
		if (e.getAdvancement().getKey().getKey().equals(this.getNamespaceKey())) {
			this.lockOut.completeChallenge(e.getPlayer(), this);
			return true;
		}

		return false;
	}

	public abstract String getNamespaceKey();
}
