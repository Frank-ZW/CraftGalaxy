package net.craftgalaxy.lockout.challenge.impl.interact;

import net.craftgalaxy.lockout.challenge.types.PlayerInteractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.loot.LootTables;

public class ChallengeLootBuriedTreasure extends PlayerInteractChallenge {

	public ChallengeLootBuriedTreasure(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handleEvent(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			if (block != null && block.getState() instanceof Chest) {
				Chest chest = (Chest) block.getState();
				if (LootTables.BURIED_TREASURE.getLootTable().equals(chest.getLootTable())) {
					this.lockOut.completeChallenge(e.getPlayer(), this);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Loot a buried treasure chest!";
	}
}
