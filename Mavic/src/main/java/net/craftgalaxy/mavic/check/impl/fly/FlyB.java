package net.craftgalaxy.mavic.check.impl.fly;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.location.PlayerLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FlyB extends PositionCheck {

	private final Map<UUID, Double> lastDistMap = new HashMap<>();

	public FlyB() {
		super("Fly B", 24, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(PlayerData playerData, PlayerLocation to, PlayerLocation from, long timestamp) {
		if (!playerData.isAllowedFlight() && !playerData.isInLiquid() && !playerData.isInWeb() && !playerData.isUnderBlock() && playerData.getLiquidTicks() > 20) {
			double distY = to.getY() - from.getY();
			Double lastDistY = this.lastDistMap.remove(playerData.getUniqueId());
			if (lastDistY == null) {
				this.lastDistMap.put(playerData.getUniqueId(), distY);
				return;
			}

			double predictedDistY = (lastDistY - 0.08D) * 0.98D;
			if (!playerData.isOnGround() && !playerData.isLastOnGround() && !playerData.isLastLastOnGround() && Math.abs(predictedDistY) >= 0.003D) {
				if (Math.abs(distY - predictedDistY) >= 0.001D) {
					this.handleViolation(playerData);
				} else {
					this.decreaseViolation(0.05D);
				}
			}

			this.lastDistMap.put(playerData.getUniqueId(), distY);
		}
	}
}
