package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.PlayerShearChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class ChallengeShearSheep extends PlayerShearChallenge {

	public ChallengeShearSheep(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(PlayerShearEntityEvent e) {
		if (e.getEntity() instanceof Sheep) {
			this.lockOut.completeChallenge(e.getPlayer(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Shear a sheep!";
	}
}
