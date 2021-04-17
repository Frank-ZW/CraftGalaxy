package net.craftgalaxy.lockout.challenge.impl.interact;

import net.craftgalaxy.lockout.challenge.types.PlayerInteractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.craftgalaxy.minigameservice.bukkit.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChallengePlayMusicDisc extends PlayerInteractChallenge {

	public ChallengePlayMusicDisc(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			ItemStack item = e.getItem();
			if (block != null && item != null && block.getType() == Material.JUKEBOX && ItemUtil.isMusicDiscType(item.getType())) {
				this.lockOut.completeChallenge(e.getPlayer(), this);
				return true;
			}
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Play a music disc!";
	}
}
