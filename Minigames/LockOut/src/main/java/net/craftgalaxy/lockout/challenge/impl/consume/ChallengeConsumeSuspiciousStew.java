package net.craftgalaxy.lockout.challenge.impl.consume;

import net.craftgalaxy.lockout.challenge.types.PlayerConsumeChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ChallengeConsumeSuspiciousStew extends PlayerConsumeChallenge {

	public ChallengeConsumeSuspiciousStew(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(PlayerItemConsumeEvent e) {
		if (e.getItem().getType() == Material.SUSPICIOUS_STEW) {
			this.lockOut.completeChallenge(e.getPlayer(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Eat a suspicious stew!";
	}
}
