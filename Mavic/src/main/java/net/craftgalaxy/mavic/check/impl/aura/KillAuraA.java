package net.craftgalaxy.mavic.check.impl.aura;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.BlockUtil;
import net.craftgalaxy.mavic.util.Cuboid;
import net.craftgalaxy.mavic.util.java.MathUtil;
import net.craftgalaxy.mavic.util.location.PlayerLocation;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraA extends PositionCheck {

	private final Map<UUID, Integer> thresholdMap = new HashMap<>();
	private final Map<UUID, Double> lastAngleMap = new HashMap<>();

	public KillAuraA() {
		super("KillAura A", 22, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(PlayerData playerData, PlayerLocation to, PlayerLocation from, long timestamp) {
		if (playerData.getLastAttackTicks() <= 2 && playerData.isSprinting() && to.isOnGround() && from.isOnGround() && timestamp - playerData.getLastFastTimestamp() > 110L && timestamp - playerData.getLastDelayedTimestamp() > 110L) {
			double yaw = Math.toDegrees(-Math.atan2(from.getX() - to.getX(), from.getZ() - to.getZ()));
			double yawOffset = Math.min(MathUtil.getDistanceBetweenAngles(yaw, from.getYaw()), MathUtil.getDistanceBetweenAngles(yaw, to.getYaw()));
			if (this.lastAngleMap.containsKey(playerData.getUniqueId())) {
				double angleDifference = MathUtil.getDistanceBetweenAngles(this.lastAngleMap.get(playerData.getUniqueId()), yawOffset);
				if (yawOffset > 47.5D) {
					int threshold = this.thresholdMap.get(playerData.getUniqueId());
					this.thresholdMap.put(playerData.getUniqueId(), ++threshold);
					if (angleDifference < 5.0D && threshold > 5) {
						World world = playerData.getPlayer().getWorld();
						Cuboid cuboid = new Cuboid(to).expand(0.5D, 0.5D, 0.5D);
						this.run(() -> {
							if (!cuboid.checkInternalBlocks(world, location -> BlockUtil.isBadVelocityBlock(location.getBlock()))) {
								this.handleViolation(playerData);
							} else {
								this.thresholdMap.put(playerData.getUniqueId(), -20);
							}
						});

						this.thresholdMap.put(playerData.getUniqueId(), 3);
					}
				} else {
					this.thresholdMap.put(playerData.getUniqueId(), 0);
				}
			}

			this.lastAngleMap.put(playerData.getUniqueId(), yawOffset);
		} else {
			this.lastAngleMap.remove(playerData.getUniqueId());
			this.thresholdMap.put(playerData.getUniqueId(), 0);
		}
	}
}
