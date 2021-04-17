package net.craftgalaxy.lockout.challenge.impl.interact;

import net.craftgalaxy.lockout.challenge.types.PlayerInteractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChallengeLightFirework extends PlayerInteractChallenge {

	public ChallengeLightFirework(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = e.getItem();
			if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
				this.lockOut.completeChallenge(e.getPlayer(), this);
				return true;
			}
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Light a firework!";
	}
}
