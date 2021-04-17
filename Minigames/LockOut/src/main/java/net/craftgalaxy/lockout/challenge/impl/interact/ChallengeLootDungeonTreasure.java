package net.craftgalaxy.lockout.challenge.impl.interact;

import net.craftgalaxy.lockout.challenge.types.PlayerInteractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.loot.LootTables;

public class ChallengeLootDungeonTreasure extends PlayerInteractChallenge {

	public ChallengeLootDungeonTreasure(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public boolean handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block clicked = e.getClickedBlock();
			if (clicked != null && clicked.getState() instanceof Chest) {
				Chest chest = (Chest) clicked.getState();
				if (LootTables.SIMPLE_DUNGEON.getLootTable().equals(chest.getLootTable())) {
					this.lockOut.completeChallenge(e.getPlayer(), this);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getDisplayMessage() {
		return "Loot a dungeon chest!";
	}
}
