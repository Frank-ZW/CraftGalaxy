package net.craftgalaxy.mavic.data;

import com.comphenix.protocol.wrappers.EnumWrappers;
import net.craftgalaxy.mavic.alert.Alert;
import net.craftgalaxy.mavic.check.Check;
import net.craftgalaxy.mavic.check.CheckLoader;
import net.craftgalaxy.mavic.packet.AbstractPacket;
import net.craftgalaxy.mavic.packet.impl.*;
import net.craftgalaxy.mavic.util.Cuboid;
import net.craftgalaxy.mavic.util.Velocity;
import net.craftgalaxy.mavic.util.java.MathUtil;
import net.craftgalaxy.mavic.util.java.StringUtil;
import net.craftgalaxy.mavic.util.location.PlayerLocation;
import org.bukkit.*;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

public class PlayerData {

	private final Player player;
	private final String name;
	private final UUID uniqueId;

	private final Set<String> checkAlertsIgnored = new HashSet<>();
	private final Map<Long, Long> keepAliveMap = new ConcurrentHashMap<>();
	private final Map<UUID, List<Location>> targetAttackMap = new ConcurrentHashMap<>();
	private final Queue<Alert> alerts = new ConcurrentLinkedQueue<>();
	private final Queue<BiConsumer<Integer, Double>> pingQueue = new ConcurrentLinkedQueue<>();
	private final Queue<Integer> connectionFrequencyQueue = new ConcurrentLinkedQueue<>();
	private final Queue<Velocity> velocityQueue = new ConcurrentLinkedQueue<>();

	private final Cuboid boundingBox;
	private final Cuboid headCuboid;
	private final Cuboid feetCuboid;
	private final CheckLoader checkLoader;
	private final PlayerLocation location;
	private PlayerLocation lastLocation;
	private PlayerLocation lastLastLocation;
	private PlayerLocation lastDelayedLocation;
	private Entity lastAttackedEntity;
	private Player lastAttackedPlayer;

	private int ping;
	private int lastPing;
	private int averagePing;
	private int totalTicks;
	private int lastAttackTicks;
	private int swingTicks;
	private int lastPlaceTicks;
	private int liquidTicks;
	private int flyingTicks;
	private int vehicleTicks;
	private int survivalTicks;
	private int allowFlightTicks;
	private int blockingTicks;
	private int teleportTicks;
	private int velocityTicks;
	private int groundTicks;
	private int cps;

	private double lastGroundY;

	private long lastAttackTimestamp;
	private long lastFlyingTimestamp;
	private long lastLastFlyingTimestamp;
	private long lastDelayedTimestamp;
	private long lastFastTimestamp;
	private long lastKeepAliveTimestamp;

	private boolean receiveAlerts;
	private boolean onGround;
	private boolean inVehicle;
	private boolean inLiquid;
	private boolean inClimable;
	private boolean inWeb;
	private boolean onIce;
	private boolean onStairs;
	private boolean onSlime;
	private boolean underBlock;
	private boolean digging;
	private boolean placing;
	private boolean blocking;
	private boolean sprinting;
	private boolean sneaking;

	public PlayerData(Player player) {
		this.player = player;
		this.name = player.getName();
		this.uniqueId = player.getUniqueId();
		this.checkLoader = CheckLoader.getInstance();
		this.location = PlayerLocation.fromBukkitLocation(player.getLocation());
		this.lastLocation = this.location.clone();
		this.lastLastLocation = this.lastLocation;
		this.receiveAlerts = player.hasPermission(StringUtil.NOTIFY_ALERT_PERMISSION);
		this.boundingBox = new Cuboid(this.location).add(-0.3D, 0.0D, -0.3D, 0.3D, 1.8D, 0.3D);
		this.headCuboid = new Cuboid(this.location).add(-0.3D, 2.0D, -0.3D, 0.3D, 3.0D, 0.3D);
		this.feetCuboid = new Cuboid(this.location).add(-0.3D, -0.5000001D, -0.3D, 0.3D, 0.0D, 0.3D);
		this.totalTicks = 0;
	}

	public void registerAlert(Alert alert) {
		this.alerts.add(alert);
	}

	public void registerPlayerAttack(UUID targetUuid, Location location) {
		List<Location> locations = this.targetAttackMap.get(targetUuid);
		if (locations == null) {
			locations = new ArrayList<>();
		}

		locations.add(location);
		this.targetAttackMap.put(targetUuid, locations);
	}

	public void ignoreCheckAlerts(String checkName) {
		this.checkAlertsIgnored.add(checkName);
	}

	public boolean unignoreCheckAlerts(String checkName) {
		return this.checkAlertsIgnored.remove(checkName);
	}

	public Player getPlayer() {
		return this.player;
	}

	public String getName() {
		return this.name;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}

	public double getLastGroundY() {
		return this.lastGroundY;
	}

	public int getMaxPing() {
		return MathUtil.highestInt(this.ping, this.lastPing, this.averagePing);
	}

	public int getMaxPingTicks() {
		return this.getMaxPing() / 50 + 3;
	}

	public int getPing() {
		return this.averagePing;
	}

	public int getLastAttackTicks() {
		return this.lastAttackTicks;
	}

	public boolean isInVehicle() {
		return this.inVehicle;
	}

	public void setInVehicle(boolean inVehicle) {
		this.inVehicle = inVehicle;
	}

	public boolean isInLiquid() {
		return this.inLiquid;
	}

	public boolean isInWeb() {
		return this.inWeb;
	}

	public int getGroundTicks() {
		return this.groundTicks;
	}

	public int getLiquidTicks() {
		return this.liquidTicks;
	}

	public int getVelocityTicks() {
		return this.velocityTicks;
	}

	public int getTotalTicks() {
		return this.totalTicks;
	}

	public Queue<Velocity> getVelocityQueue() {
		return this.velocityQueue;
	}

	public boolean isSprinting() {
		return this.sprinting;
	}

	public boolean isInClimable() {
		return this.inClimable;
	}

	public boolean isAllowedFlight() {
		return this.allowFlightTicks <= this.getMaxPingTicks() * 3;
	}

	public boolean isReceiveAlerts(String checkName) {
		return this.receiveAlerts && !this.checkAlertsIgnored.contains(checkName);
	}

	public boolean isReceiveAlerts() {
		return this.receiveAlerts;
	}

	public void setReceiveAlerts(boolean receiveAlerts) {
		this.receiveAlerts = receiveAlerts;
	}

	public boolean isOnGround() {
		return this.onGround;
	}

	public boolean isLastOnGround() {
		return this.lastLocation.isOnGround();
	}

	public boolean isLastLastOnGround() {
		return this.lastLastLocation.isOnGround();
	}

	public boolean isUnderBlock() {
		return this.underBlock;
	}

	public long getLastFastTimestamp() {
		return this.lastFastTimestamp;
	}

	public long getLastDelayedTimestamp() {
		return this.lastDelayedTimestamp;
	}

	public void handleTickUpdate() {
		this.groundTicks = this.location.isOnGround() ? ++this.groundTicks : 0;
		this.vehicleTicks = this.inVehicle ? ++this.vehicleTicks : 0;
		this.blockingTicks = this.blocking ? ++this.blockingTicks : 0;
		this.liquidTicks = this.inLiquid ? 0 : ++this.liquidTicks;
		this.flyingTicks = this.player.isFlying() ? 0 : ++this.flyingTicks;
		this.allowFlightTicks = this.player.getAllowFlight() ? 0 : ++this.allowFlightTicks;
		this.survivalTicks = this.player.getGameMode() == GameMode.ADVENTURE || this.player.getGameMode() == GameMode.SURVIVAL ? ++this.survivalTicks : 0;
		if (this.player.getGameMode() == GameMode.SPECTATOR) {
			this.sprinting = false;
		}

		if (this.placing) {
			this.placing = false;
		}

		this.lastPlaceTicks++;
		this.lastAttackTicks++;
		this.velocityTicks++;
		this.totalTicks++;
	}

	public void handlePacket(AbstractPacket abstractPacket, boolean serverbound) {
		if (serverbound) {
			long now = System.currentTimeMillis();
			if (abstractPacket instanceof APacketPlayInFlying) {
				APacketPlayInFlying packet = (APacketPlayInFlying) abstractPacket;
				this.lastLastLocation = this.lastLocation.clone();
				this.lastLocation = this.location.clone();
				if (packet.isPos()) {
					this.location.setX(packet.getX());
					this.location.setY(packet.getY());
					this.location.setZ(packet.getZ());
				}

				if (packet.isLook()) {
					this.location.setYaw(packet.getYaw());
					this.location.setPitch(packet.getPitch());
				}

				this.location.setOnGround(packet.isOnGround());
				this.location.setTimestamp(now);

				long delay = now - this.lastFlyingTimestamp;
				if (delay > 110L) {
					this.lastDelayedTimestamp = now;
					this.lastDelayedLocation = this.location;
				}

				if (delay < 15L) {
					this.lastFastTimestamp = now;
				}

				this.velocityQueue.removeIf(velocity -> this.totalTicks - velocity.getTotalTicks() > 2 * this.getMaxPingTicks() && velocity.attenuate(packet.isOnGround()));
				this.connectionFrequencyQueue.add(50 - (int) delay);
				this.lastLastFlyingTimestamp = this.lastFlyingTimestamp;
				this.lastFlyingTimestamp = now;

				double distX = this.location.getX() - this.lastLocation.getX();
				double distY = this.location.getY() - this.lastLocation.getY();
				double distZ = this.location.getZ() - this.lastLocation.getZ();
				this.headCuboid.move(distX, distY, distZ);
				this.feetCuboid.move(distX, distY, distZ);
				this.boundingBox.move(distX, distY, distZ);
				this.inLiquid = this.boundingBox.checkIfPresent(this.player.getWorld(), material -> material == Material.WATER || material == Material.LAVA);
				this.inWeb = this.boundingBox.checkIfPresent(this.player.getWorld(), material -> material == Material.COBWEB);
				this.inClimable = this.boundingBox.checkIfPresent(this.player.getWorld(), material -> material == Material.LADDER || material == Material.VINE || material == Material.SCAFFOLDING || material == Material.TWISTING_VINES || material == Material.TWISTING_VINES_PLANT || material == Material.WEEPING_VINES || material == Material.WEEPING_VINES_PLANT);
				this.onGround = this.feetCuboid.checkIfPresent(this.player.getWorld(), material -> !material.isAir() && material != Material.WATER && material != Material.LAVA);
				this.onIce = this.feetCuboid.checkIfPresent(this.player.getWorld(), material -> material == Material.ICE || material == Material.BLUE_ICE || material == Material.PACKED_ICE || material == Material.FROSTED_ICE);
				this.onStairs = this.feetCuboid.checkInternalBlocks(this.player.getWorld(), location -> location.getBlock().getBlockData() instanceof Stairs);
				this.onSlime = this.feetCuboid.checkIfPresent(this.player.getWorld(), material -> material == Material.SLIME_BLOCK);
				this.underBlock = this.headCuboid.checkIfPresent(this.player.getWorld(), material -> !material.isAir());
				if (this.onGround) {
					this.lastGroundY = this.location.getY();
				}

				boolean moved = !this.location.sameLocation(this.lastLocation);
				boolean rotated = !this.location.sameDirection(this.lastLocation);
				if (moved) {
					this.checkLoader.getPositionChecks().stream().filter(Check::isEnabled).forEach(check -> check.handle(this, this.location, this.lastLocation, now));
				}

				if (rotated) {
					this.checkLoader.getRotationChecks().stream().filter(Check::isEnabled).forEach(check -> check.handle(this, this.location, this.lastLocation, now));
				}
			} else if (abstractPacket instanceof APacketPlayInUseEntity) {
				APacketPlayInUseEntity packet = (APacketPlayInUseEntity) abstractPacket;
				if (packet.getAction() == EnumWrappers.EntityUseAction.ATTACK) {
					Entity target = packet.getEntity();
					this.lastAttackTicks = 0;
					this.lastAttackTimestamp = now;
					this.lastAttackedEntity = target;
					if (target instanceof Player) {
						this.lastAttackedPlayer = (Player) target;
						this.registerPlayerAttack(target.getUniqueId(), target.getLocation());
					}
				}
			} else if (abstractPacket instanceof APacketPlayInKeepAlive) {
				APacketPlayInKeepAlive packet = (APacketPlayInKeepAlive) abstractPacket;
				Long timestamp = this.keepAliveMap.remove(packet.getId());
				if (timestamp != null) {
					this.lastPing = this.ping;
					this.ping = (int) (now - timestamp);
					this.averagePing = (this.averagePing * 9 + this.ping) / 10;
					this.lastKeepAliveTimestamp = now;
				}
			} else if (abstractPacket instanceof APacketPlayInBlockDig) {
				APacketPlayInBlockDig packet = (APacketPlayInBlockDig) abstractPacket;
				switch (packet.getDigType()) {
					case START_DESTROY_BLOCK:
						this.digging = true;
						break;
					case STOP_DESTROY_BLOCK:
					case ABORT_DESTROY_BLOCK:
						this.digging = false;
						break;
					default:
				}
			} else if (abstractPacket instanceof APacketPlayInBlockPlace) {
				this.placing = true;
			} else if (abstractPacket instanceof APacketPlayInEntityAction) {
				APacketPlayInEntityAction packet = (APacketPlayInEntityAction) abstractPacket;
				if (packet.getEntityId() == this.player.getEntityId()) {
					switch (packet.getPlayerAction()) {
						case START_SNEAKING:
							this.sneaking = true;
							break;
						case STOP_SNEAKING:
							this.sneaking = false;
							break;
						case START_SPRINTING:
							this.sprinting = true;
							break;
						case STOP_SPRINTING:
							this.sprinting = false;
							break;
						default:
					}
				}
			}
		} else if (abstractPacket instanceof APacketPlayOutKeepAlive) {
			APacketPlayOutKeepAlive packet = (APacketPlayOutKeepAlive) abstractPacket;
			this.keepAliveMap.put(packet.getId(), System.currentTimeMillis());
		} else if (abstractPacket instanceof APacketPlayOutEntityVelocity) {
			APacketPlayOutEntityVelocity packet = (APacketPlayOutEntityVelocity) abstractPacket;
			if (this.player.getEntityId() == packet.getEntityId()) {
				Velocity velocity = new Velocity(packet.getVelX(), packet.getVelY(), packet.getVelZ(), this.totalTicks);
				this.velocityQueue.add(velocity);
				this.velocityTicks = 0;
			}
		}

		this.checkLoader.getPacketChecks().parallelStream().filter(Check::isEnabled).forEach(check -> check.handle(this, abstractPacket, System.currentTimeMillis()));
	}
}
