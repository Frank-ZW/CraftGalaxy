package net.craftgalaxy.lockout.challenge.impl.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.craftgalaxy.lockout.challenge.types.PlayerArmorChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChallengeWearTurtleShell extends PlayerArmorChallenge {

	public ChallengeWearTurtleShell(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handleEvent(PlayerArmorChangeEvent e) {
		ItemStack item = e.getNewItem();
		if (item != null && item.getType() == Material.TURTLE_HELMET) {
			this.lockOut.completeChallenge(e.getPlayer(), this);
			return true;
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Wear a turtle shell!";
	}
}
