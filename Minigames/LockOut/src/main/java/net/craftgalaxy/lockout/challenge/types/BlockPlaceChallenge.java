package net.craftgalaxy.lockout.challenge.types;

import net.craftgalaxy.lockout.challenge.AbstractChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;

public abstract class BlockPlaceChallenge extends AbstractChallenge<BlockPlaceEvent> {

	public BlockPlaceChallenge(LockOut lockOut) {
		super(lockOut, BlockPlaceEvent.class);
	}

	@Override
	public boolean handle(BlockPlaceEvent e) {
		if (e.getBlock().getType() == this.blockType()) {
			this.lockOut.completeChallenge(e.getPlayer(), this);
			return true;
		}

		return false;
	}

	protected abstract Material blockType();
}
