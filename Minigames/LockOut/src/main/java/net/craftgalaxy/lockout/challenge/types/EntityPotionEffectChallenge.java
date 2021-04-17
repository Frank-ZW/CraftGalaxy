package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public abstract class EntityPotionEffectChallenge extends AbstractChallenge<EntityPotionEffectEvent> {

	public EntityPotionEffectChallenge(LockOut lockOut) {
		super(lockOut, EntityPotionEffectEvent.class);
	}
}
