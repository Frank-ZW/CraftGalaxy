package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.spigotmc.event.entity.EntityMountEvent;

public abstract class EntityMountChallenge extends AbstractChallenge<EntityMountEvent> {

	private final EntityType entity;

	public EntityMountChallenge(LockOut lockOut, EntityType entity) {
		super(lockOut, EntityMountEvent.class);
		this.entity = entity;
	}

	@Override
	public boolean handle(EntityMountEvent e) {
		if (e.getMount().getType() == this.entity && e.getEntity() instanceof Player) {
			this.lockOut.completeChallenge((Player) e.getEntity(), this);
			return true;
		}

		return false;
	}
}
