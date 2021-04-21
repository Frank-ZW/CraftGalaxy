package net.craftgalaxy.mavic.check.impl.fly;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.location.PlayerLocation;

public class FlyA extends PositionCheck {

	public FlyA(PlayerData playerData) {
		super(playerData, "Fly A", 24, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(PlayerLocation to, PlayerLocation from, long timestamp) {
		if (!this.playerData.isAllowedFlight() && !this.playerData.isInClimable() && !this.playerData.isInLiquid() && this.playerData.getLiquidTicks() >= 20 && !this.playerData.isInWeb() && !this.playerData.isInVehicle() && !this.playerData.isOnGround()) {
			double distXZ = to.groundDistanceSquared(from);
			double distY = to.getY() - from.getY();
			if (distY == 0.0D && distXZ > 0.0D) {
				this.handleViolation();
			} else {
				this.decreaseViolation(0.05D);
			}
		}
	}
}
