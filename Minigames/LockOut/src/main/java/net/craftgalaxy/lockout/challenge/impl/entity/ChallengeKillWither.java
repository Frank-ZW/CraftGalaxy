package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.EntityDeathChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.EntityDeathEvent;

public class ChallengeKillWither extends EntityDeathChallenge {

	public ChallengeKillWither(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(EntityDeathEvent e) {
		if (e.getEntity() instanceof Wither) {
			Player killer = e.getEntity().getKiller();
			if (killer != null) {
				this.lockOut.completeChallenge(killer, this);
				return true;
			}
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Kill the Wither!";
	}
}
