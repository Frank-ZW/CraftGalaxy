package net.craftgalaxy.lockout.challenge.impl.misc;

import net.craftgalaxy.lockout.challenge.types.PlayerBedEnterChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class ChallengeEnterBed extends PlayerBedEnterChallenge {

	public ChallengeEnterBed(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handleEvent(PlayerBedEnterEvent e) {
		if (e.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
			this.lockOut.completeChallenge(e.getPlayer(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Lie down in a bed!";
	}

}
