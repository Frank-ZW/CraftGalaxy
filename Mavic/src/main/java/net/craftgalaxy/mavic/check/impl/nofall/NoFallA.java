package net.craftgalaxy.mavic.check.impl.nofall;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.location.PlayerLocation;

public final class NoFallA extends PositionCheck {

	public NoFallA() {
		super("No Fall A", 24, CheckType.RELEASE);
	}

	@Override
	public void handle(PlayerData playerData, PlayerLocation to, PlayerLocation from, long timestamp) {
		if (!playerData.isOnGround() && to.isOnGround() && from.isOnGround()) {
			this.handleViolation(playerData);
		}
	}
}
