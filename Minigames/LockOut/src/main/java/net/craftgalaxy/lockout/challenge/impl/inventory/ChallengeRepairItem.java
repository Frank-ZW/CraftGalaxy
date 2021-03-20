package net.craftgalaxy.lockout.challenge.impl.inventory;

import net.craftgalaxy.lockout.challenge.types.PlayerInventoryClickChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class ChallengeRepairItem extends PlayerInventoryClickChallenge {

	public ChallengeRepairItem(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handleEvent(InventoryClickEvent e) {
		if (e.getInventory().getType() == InventoryType.ANVIL && e.getRawSlot() == 2) {
			Inventory anvil = e.getInventory();
			if (anvil.getItem(0) != null && anvil.getItem(1) != null) {
				this.lockOut.completeChallenge((Player) e.getWhoClicked(), this);
			}
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Repair an item!";
	}
}
