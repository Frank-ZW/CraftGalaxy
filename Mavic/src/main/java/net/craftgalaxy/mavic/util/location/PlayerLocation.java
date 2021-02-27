package net.craftgalaxy.mavic.util.location;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;

public class PlayerLocation implements Cloneable, Serializable {

	private static final long serialVersionUID = -7798057450395684054L;
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;
	private boolean onGround;
	private long timestamp;
	private int tick;

	public PlayerLocation(double x, double y, double z, float yaw, float pitch, boolean onGround) {
		this(x, y, z, yaw, pitch, onGround, 0, 0);
	}

	public PlayerLocation(double x, double y, double z, float yaw, float pitch, boolean onGround, long timestamp, int tick) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
		this.timestamp = timestamp;
		this.tick = tick;
	}

	public static PlayerLocation fromBukkitLocation(Location o) {
		return new PlayerLocation(o.getX(), o.getY(), o.getZ(), o.getYaw(), o.getPitch(), o.getWorld().getHighestBlockYAt(o) == o.getBlockY() - 1);
	}

	public Location toBukkitLocation(World world) {
		return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
	}

	public double groundDistanceSquared(PlayerLocation o) {
		return Math.pow(o.getX() - this.x, 2.0D) + Math.pow(o.getZ() - this.z, 2.0D);
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public boolean isOnGround() {
		return this.onGround;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public int getTick() {
		return this.tick;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	public boolean sameLocation(PlayerLocation o) {
		return this.x == o.x && this.y == o.y && this.z == o.z;
	}

	public boolean sameDirection(PlayerLocation o) {
		return this.yaw == o.yaw && this.pitch == o.pitch;
	}

	@Override
	public PlayerLocation clone() {
		try {
			return (PlayerLocation) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 23;
		int result = 1;
		result = result * prime + Double.hashCode(this.x);
		result = result * prime + Double.hashCode(this.y);
		result = result * prime + Double.hashCode(this.z);
		result = result * prime + Float.hashCode(this.yaw);
		result = result * prime + Float.hashCode(this.pitch);
		result = result * prime + (this.isOnGround() ? 1 : 0);
		result = result * prime + Long.hashCode(this.timestamp);
		result = result * prime + this.tick;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof PlayerLocation)) {
			return false;
		}

		PlayerLocation o = (PlayerLocation) obj;
		return this.sameDirection(o) && this.sameLocation(o) && this.onGround == o.isOnGround() && this.timestamp == o.timestamp && this.tick == o.tick;
	}
}
