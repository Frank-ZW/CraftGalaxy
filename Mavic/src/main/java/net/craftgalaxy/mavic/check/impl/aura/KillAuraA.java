package net.craftgalaxy.mavic.check.impl.aura;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.BlockUtil;
import net.craftgalaxy.mavic.util.Cuboid;
import net.craftgalaxy.mavic.util.java.MathUtil;
import net.craftgalaxy.mavic.util.location.PlayerLocation;
import org.bukkit.World;

public class KillAuraA extends PositionCheck {

	private int threshold;
	private double lastYawOffset = Double.MIN_VALUE;

	public KillAuraA(PlayerData playerData) {
		super(playerData, "KillAura A", 22, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(PlayerLocation to, PlayerLocation from, long timestamp) {
		if (this.playerData.getLastAttackTicks() <= 2 && this.playerData.isSprinting() && to.isOnGround() && from.isOnGround() && timestamp - this.playerData.getLastFastTimestamp() > 110L && timestamp - this.playerData.getLastDelayedTimestamp() > 110L) {
			double yaw = Math.toDegrees(-Math.atan2(from.getX() - to.getX(), from.getZ() - to.getZ()));
			double yawOffset = Math.min(MathUtil.getDistanceBetweenAngles(yaw, from.getYaw()), MathUtil.getDistanceBetweenAngles(yaw, to.getYaw()));
			if (this.lastYawOffset != Double.MIN_VALUE) {
				double angleDifference = MathUtil.getDistanceBetweenAngles(this.lastYawOffset, yawOffset);
				if (yawOffset > 47.5D) {
					if (angleDifference < 5.0D && ++this.threshold > 5) {
						World world = this.player.getWorld();
						Cuboid cuboid = new Cuboid(to).expand(0.5D, 0.5D, 0.5D);
						this.run(() -> {
							if (!cuboid.checkInternalBlocks(world, location -> BlockUtil.isBadVelocityBlock(location.getBlock()))) {
								this.handleViolation();
							} else {
								this.threshold = -20;
							}
						});

						this.threshold = 3;
					}
				} else {
					this.threshold = 0;
				}
			}

			this.lastYawOffset = yawOffset;
		} else {
			this.lastYawOffset = Double.MIN_VALUE;
			this.threshold = 0;
		}
	}
}
