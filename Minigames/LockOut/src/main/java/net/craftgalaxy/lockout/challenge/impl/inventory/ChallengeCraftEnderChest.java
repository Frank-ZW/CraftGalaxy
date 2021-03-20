package net.craftgalaxy.lockout.challenge.impl.inventory;

import net.craftgalaxy.lockout.challenge.types.PlayerInventoryClickChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class ChallengeCraftEnderChest extends PlayerInventoryClickChallenge {

	public ChallengeCraftEnderChest(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handleEvent(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if (e.getInventory().getType() == InventoryType.WORKBENCH && e.getRawSlot() == 0 && item != null && item.getType() == Material.ENDER_CHEST) {
			this.lockOut.completeChallenge((Player) e.getWhoClicked(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Craft an Ender Chest!";
	}
}
