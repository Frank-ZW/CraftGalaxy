package net.craftgalaxy.mavic.check.impl.fly;

import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.BlockUtil;
import net.craftgalaxy.mavic.util.Cuboid;
import net.craftgalaxy.mavic.util.location.PlayerLocation;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FlyC extends PositionCheck {

	private int lastBypassTick = -10;
	private double threshold;

	public FlyC(PlayerData playerData) {
		super(playerData, "Fly C", 24, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(PlayerLocation to, PlayerLocation from, long timestamp) {
		if (this.playerData.getTotalTicks() - this.lastBypassTick < 20) {
			return;
		}

		if (to.getY() > from.getY() && from.isOnGround() && !this.playerData.isAllowedFlight()) {
			double distY = to.getY() - from.getY();
			boolean ticksSinceLastVelocity = this.playerData.getVelocityTicks() <= 4 * this.playerData.getMaxPingTicks();
			if (this.playerData.getVelocityQueue().stream().anyMatch(velocity -> Math.abs(velocity.getY() - distY) <= 1.25E-4D)) {
				return;
			}

			if (distY < 0.42D && (to.getY() - 0.42D) % 1.0D > 1.0E-15D) {
				World world = this.player.getWorld();
				Cuboid headCuboid = new Cuboid(from, to).move(0.0D, 2.0D, 0.0D).expand(0.5D, 0.5D, 0.5D);
				Cuboid groundCuboid = new Cuboid(from, to).move(0.0D, -0.25D, 0.0D).expand(0.5D, 0.75D, 0.5D);
				int totalTicks = this.playerData.getTotalTicks();
				PotionEffect potionEffect = this.player.getPotionEffect(PotionEffectType.JUMP);
				if (potionEffect != null) {
					this.lastBypassTick = this.playerData.getTotalTicks();
				}

				this.run(() -> {
					if (this.playerData.getTotalTicks() - this.lastBypassTick < 20) {
						return;
					}

					if (headCuboid.checkIfPresent(world, material -> !material.isAir()) && groundCuboid.checkInternalBlocks(world, location -> BlockUtil.isBadVelocityBlock(location.getBlock()))) {
						for (Entity entity : this.player.getNearbyEntities(2.5D, 2.5D, 2.5D)) {
							if (entity instanceof Boat || entity instanceof Minecart) {
								this.threshold = 0.0D;
								this.lastBypassTick = totalTicks - 100;
								this.decreaseViolation(0.025D);
								return;
							}
						}

						this.threshold += ticksSinceLastVelocity ? 0.25D : 1.0D;
						this.handleViolation("", this.threshold);
					} else {
						this.threshold = 0.0D;
						this.lastBypassTick = totalTicks;
						this.decreaseViolation(0.025D);
					}
				});
			}
		}
	}
}
