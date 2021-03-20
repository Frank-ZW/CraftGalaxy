package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class PlayerInventoryClickChallenge extends AbstractChallenge<InventoryClickEvent> {

	public PlayerInventoryClickChallenge(LockOut lockOut) {
		super(lockOut);
	}
}
