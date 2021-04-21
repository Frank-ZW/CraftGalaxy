package net.craftgalaxy.mavic.check.impl.fly;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.location.PlayerLocation;

public final class FlyB extends PositionCheck {

	private double lastDistY = Double.MIN_VALUE;

	public FlyB(PlayerData playerData) {
		super(playerData, "Fly B", 24, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(PlayerLocation to, PlayerLocation from, long timestamp) {
		if (!this.playerData.isAllowedFlight() && !this.playerData.isInLiquid() && !this.playerData.isInWeb() && !this.playerData.isUnderBlock() && this.playerData.getLiquidTicks() >= 20) {
			double distY = to.getY() - from.getY();
			if (this.lastDistY != Double.MIN_VALUE) {
				double predictedDistY = (this.lastDistY - 0.08D) * 0.98D;
				if (!this.playerData.isOnGround() && !this.playerData.isLastOnGround() && !this.playerData.isLastLastOnGround() && Math.abs(predictedDistY) >= 0.003D) {
					if (Math.abs(distY - predictedDistY) >= 0.001D) {
						this.handleViolation();
					} else {
						this.decreaseViolation(0.05D);
					}
				}
			}

			this.lastDistY = distY;
		}
	}
}
