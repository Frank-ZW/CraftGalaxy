package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.EntityDeathChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.EntityDeathEvent;

public class ChallengeKillSlime extends EntityDeathChallenge {

	public ChallengeKillSlime(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handleEvent(EntityDeathEvent e) {
		if (e.getEntity() instanceof Slime) {
			Player killer = e.getEntity().getKiller();
			if (killer != null) {
				this.lockOut.completeChallenge(killer, this);
			}
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Kill a Slime!";
	}
}
