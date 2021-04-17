package net.craftgalaxy.lockout.challenge.impl.misc;

import net.craftgalaxy.lockout.challenge.types.EntityDamageChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class ChallengeWitherDamage extends EntityDamageChallenge {

	public ChallengeWitherDamage(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.WITHER) {
			this.lockOut.completeChallenge((Player) e.getEntity(), this);
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Get withered!";
	}
}
