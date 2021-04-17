package net.craftgalaxy.lockout.challenge.impl.block;

import net.craftgalaxy.lockout.challenge.types.BlockPlaceChallenge;
import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Material;

public class ChallengePlaceBeacon extends BlockPlaceChallenge {

	public ChallengePlaceBeacon(LockOut lockOut) {
		super(lockOut);
	}

	@Override
	public String getDisplayMessage() {
		return "Place a beacon!";
	}

	@Override
	protected Material blockType() {
		return Material.BEACON;
	}
}
