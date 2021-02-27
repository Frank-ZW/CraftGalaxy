package net.craftgalaxy.mavic.check.impl.fly;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.location.PlayerLocation;

public class FlyA extends PositionCheck {

	public FlyA() {
		super("Fly A", 18, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(PlayerData playerData, PlayerLocation to, PlayerLocation from, long timestamp) {
		if (!playerData.isAllowedFlight() && !playerData.isInClimable() && !playerData.isInLiquid() && playerData.getLiquidTicks() >= 20 && !playerData.isInWeb() && !playerData.isInVehicle() && !playerData.isOnGround()) {
			double distXZ = to.groundDistanceSquared(from);
			double distY = to.getY() - from.getY();
			if (distY == 0.0D && distXZ > 0.0D) {
				this.handleViolation(playerData);
			} else {
				this.decreaseViolation(0.05D);
			}
		}
	}
}
