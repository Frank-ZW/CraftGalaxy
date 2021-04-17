package net.craftgalaxy.lockout.challenge.impl.inventory;

import net.craftgalaxy.lockout.challenge.types.PlayerInventoryClickChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

public class ChallengeBrewHealingPotion extends PlayerInventoryClickChallenge {

	public ChallengeBrewHealingPotion(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if (e.getInventory().getType() == InventoryType.BREWING && e.getRawSlot() >= 0 && e.getRawSlot() <= 2 && item != null && (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION)) {
			PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
			if (potionMeta != null) {
				PotionEffectType type = potionMeta.getBasePotionData().getType().getEffectType();
				if (type == PotionEffectType.HEAL || type == PotionEffectType.HEALTH_BOOST) {
					this.lockOut.completeChallenge((Player) e.getWhoClicked(), this);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Brew a potion of healing!";
	}
}
