package net.craftgalaxy.lockout.challenge.impl.misc;

import net.craftgalaxy.lockout.challenge.types.EntityDamageChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class ChallengeFallDamageDeath extends EntityDamageChallenge {

	public ChallengeFallDamageDeath(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handleEvent(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return false;
		}

		Player player = (Player) e.getEntity();
		if (player.getHealth() - e.getDamage() <= 0 && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
			this.lockOut.completeChallenge(player, this);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getDisplayMessage() {
		return "Die from fall damage!";
	}
}
