package net.craftgalaxy.lockout.challenge.impl.interact;

import net.craftgalaxy.lockout.challenge.types.PlayerInteractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChallengeFillComposter extends PlayerInteractChallenge {

	public ChallengeFillComposter(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			if (block != null && block.getType() == Material.COMPOSTER) {
				Levelled composter = (Levelled) block.getBlockData();
				if (composter.getLevel() >= composter.getMaximumLevel()) {
					this.lockOut.completeChallenge(e.getPlayer(), this);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Fill a composter!";
	}
}
