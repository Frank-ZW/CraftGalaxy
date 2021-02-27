package net.craftgalaxy.mavic.check.impl.fly;

import net.craftgalaxy.mavic.Mavic;
import net.craftgalaxy.mavic.check.types.PositionCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.util.BlockUtil;
import net.craftgalaxy.mavic.util.Cuboid;
import net.craftgalaxy.mavic.util.location.PlayerLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyC extends PositionCheck {

	private final Mavic plugin;
	private final Map<UUID, Integer> bypassTickMap = new HashMap<>();
	private final Map<UUID, Double> thresholdMap = new HashMap<>();

	public FlyC() {
		super("Fly C", 24, CheckType.DEVELOPMENT);
		this.plugin = Mavic.getInstance();
	}

	@Override
	public void handle(PlayerData playerData, PlayerLocation to, PlayerLocation from, long timestamp) {
		Integer lastBypassTick = this.bypassTickMap.get(playerData.getUniqueId());
		if (lastBypassTick == null) {
			this.bypassTickMap.put(playerData.getUniqueId(), -10);
			return;
		}

		if (playerData.getTotalTicks() - lastBypassTick < 20) {
			return;
		}

		if (to.getY() > from.getY() && from.isOnGround() && !playerData.isAllowedFlight()) {
			double distY = to.getY() - from.getY();
			boolean lastVelocityTicks = playerData.getVelocityTicks() <= 4 * playerData.getMaxPingTicks();
			if (playerData.getVelocityQueue().stream().anyMatch(velocity -> Math.abs(velocity.getY() - distY) <= 1.25E-4D)) {
				return;
			}

			if (distY < 0.42D && (to.getY() - 0.42D) % 1.0D > 1.0E-15D) {
				Player player = playerData.getPlayer();
				Cuboid headCuboid = new Cuboid(to, from).move(0.0D, 2.0D, 0.0D).expand(0.5D, 0.5D, 0.5D);
				Cuboid groundCuboid = new Cuboid(to, from).move(0.0D, -0.25D, 0.0D).expand(0.5D, 0.75D, 0.5D);
				int totalTicks = playerData.getTotalTicks();
				PotionEffect potionEffect = playerData.getPlayer().getPotionEffect(PotionEffectType.JUMP);
				if (potionEffect != null) {
					this.bypassTickMap.put(playerData.getUniqueId(), playerData.getTotalTicks());
				}

				if (headCuboid.checkIfPresent(player.getWorld(), material -> !material.isAir()) && groundCuboid.checkInternalBlocks(player.getWorld(), location -> BlockUtil.isBadVelocityBlock(location.getBlock()))) {
					Bukkit.getScheduler().runTask(this.plugin, () -> {
						for (Entity entity : player.getNearbyEntities(2.5D, 2.5D, 2.5D)) {
							if (entity instanceof Boat || entity instanceof Minecart) {
								this.thresholdMap.put(playerData.getUniqueId(), 0.0D);
								this.bypassTickMap.put(playerData.getUniqueId(), totalTicks - 100);
								this.decreaseViolation(0.025D);
								return;
							}
						}

						this.run(() -> {
							Double threshold = this.thresholdMap.get(playerData.getUniqueId());
							if (threshold == null) {
								threshold = 0.0D;
							}

							this.thresholdMap.put(playerData.getUniqueId(), threshold + (lastVelocityTicks ? 0.25D : 1.0D));
							this.handleViolation(playerData);
						});
					});
				} else {
					this.thresholdMap.put(playerData.getUniqueId(), 0.0D);
					this.bypassTickMap.put(playerData.getUniqueId(), totalTicks);
					this.decreaseViolation(0.025D);
				}
			}
		}
	}
}
