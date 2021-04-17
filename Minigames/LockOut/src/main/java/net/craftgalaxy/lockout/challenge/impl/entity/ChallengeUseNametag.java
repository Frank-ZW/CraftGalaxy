package net.craftgalaxy.lockout.challenge.impl.entity;

import net.craftgalaxy.lockout.challenge.types.PlayerEntityInteractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ChallengeUseNametag extends PlayerEntityInteractChallenge {

	public ChallengeUseNametag(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(PlayerInteractEntityEvent e) {
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType().isAir()) {
			item = player.getInventory().getItemInOffHand();
		}

		if (item.getType() == Material.NAME_TAG) {
			this.lockOut.completeChallenge(player, this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Use a nametag!";
	}
}
