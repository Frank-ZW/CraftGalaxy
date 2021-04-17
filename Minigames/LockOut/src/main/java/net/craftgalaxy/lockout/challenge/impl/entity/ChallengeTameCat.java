package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.EntityTameChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTameEvent;

public class ChallengeTameCat extends EntityTameChallenge {

	public ChallengeTameCat(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(EntityTameEvent e) {
		if (e.getEntity() instanceof Cat && e.getOwner() instanceof Player) {
			this.lockOut.completeChallenge((Player) e.getOwner(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Tame a cat!";
	}
}
