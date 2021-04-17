package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.EntityHatchChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerEggThrowEvent;

public class ChallengeHatchChicken extends EntityHatchChallenge {

	public ChallengeHatchChicken(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(PlayerEggThrowEvent e) {
		if (e.isHatching() && e.getHatchingType() == EntityType.CHICKEN) {
			this.lockOut.completeChallenge(e.getPlayer(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return ChatColor.GREEN + "Hatch a chicken from an egg!";
	}
}
