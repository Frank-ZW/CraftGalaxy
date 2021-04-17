package net.craftgalaxy.lockout.challenge.impl.misc;

import net.craftgalaxy.lockout.challenge.types.EntityPotionEffectChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;

public class ChallengeLevitationEffect extends EntityPotionEffectChallenge {

	public ChallengeLevitationEffect(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(EntityPotionEffectEvent e) {
		if (e.getEntity() instanceof Player && e.getNewEffect() != null && e.getNewEffect().getType() == PotionEffectType.LEVITATION) {
			this.lockOut.completeChallenge((Player) e.getEntity(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Levitate off of the ground!";
	}
}
