package net.craftgalaxy.lockout.challenge.impl.inventory;

import net.craftgalaxy.lockout.challenge.types.PlayerInventoryClickChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class ChallengeCraftDispenser extends PlayerInventoryClickChallenge {

	public ChallengeCraftDispenser(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if (e.getInventory().getType() == InventoryType.WORKBENCH && e.getRawSlot() == 0 && item != null && item.getType() == Material.DISPENSER) {
			this.lockOut.completeChallenge((Player) e.getWhoClicked(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return ChatColor.GREEN + "Craft a dispenser!";
	}
}
