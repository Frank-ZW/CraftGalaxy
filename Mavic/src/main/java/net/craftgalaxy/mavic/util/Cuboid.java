package net.craftgalaxy.mavic.util;

import net.craftgalaxy.mavic.util.location.PlayerLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public final class Cuboid implements Cloneable {

	private double minX;
	private double minY;
	private double minZ;
	private double maxX;
	private double maxY;
	private double maxZ;

	public Cuboid(PlayerLocation location) {
		this(location.getX(), location.getY(), location.getZ());
	}

	public Cuboid(PlayerLocation loc1, PlayerLocation loc2) {
		this(loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ());
	}

	public Cuboid(double x, double y, double z) {
		this(x, y, z, x, y, z);
	}

	public Cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.minX = Math.min(x1, x2);
		this.minY = Math.min(y1, y2);
		this.minZ = Math.min(z1, z2);
		this.maxX = Math.max(x1, x2);
		this.maxY = Math.max(y1, y2);
		this.maxZ = Math.max(z1, z2);
	}

	/**
	 * @param minX    The amount to add to the minimum x-value.
	 * @param minY    The amount to add to the minimum y-value.
	 * @param minZ    The amount to add to the minimum z-value.
	 * @param maxX    The amount to add to the maximum x-value.
	 * @param maxY    The amount to add to the maximum y-value.
	 * @param maxZ    The amount to add to the maximum z-value.
	 * @return        The modified region.
	 */
	public Cuboid add(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.minX += minX;
		this.minY += minY;
		this.minZ += minZ;
		this.maxX += maxX;
		this.maxY += maxY;
		this.maxZ += maxZ;
		return this;
	}

	/**
	 *
	 * @param x     The amount to expand the x-value by.
	 * @param y     The amount to expand the y-value by.
	 * @param z     The amount to expand the z-value by.
	 * @return      The modified region.
	 */
	public Cuboid expand(double x, double y, double z) {
		this.minX -= x;
		this.minY -= y;
		this.minZ -= z;
		this.maxX += x;
		this.maxY += y;
		this.maxZ += z;
		return this;
	}

	public Cuboid move(double x, double y, double z) {
		return this.add(x, y, z, x, y, z);
	}

	public Cuboid reset(PlayerLocation location) {
		this.minX = this.maxX = location.getX();
		this.minY = this.maxY = location.getY();
		this.minZ = this.maxZ = location.getZ();
		return this;
	}

	public Cuboid translateY(double minY, double maxY) {
		this.minY = minY;
		this.maxY = maxY;
		return this;
	}

	public double getCenterX() {
		return 0.5 * (this.minX + this.maxX);
	}

	public double getCenterY() {
		return 0.5 * (this.minY + this.maxY);
	}

	public double getCenterZ() {
		return 0.5 * (this.minZ + this.maxZ);
	}

	public boolean checkIfPresent(@NotNull World world, Predicate<Material> predicate) {
		return this.checkInternalBlocks(world, location -> predicate.test(location.getBlock().getType()));
	}

	public boolean checkInternalBlocks(@NotNull World world, Predicate<Location> predicate) {
		int blockMinX = (int) Math.floor(this.minX);
		int blockMinY = (int) Math.max(Math.floor(this.minY), 0);
		int blockMinZ = (int) Math.floor(this.minZ);
		int blockMaxX = (int) Math.ceil(this.maxX);
		int blockMaxY = (int) Math.min(Math.ceil(this.maxY), world.getMaxHeight());
		int blockMaxZ = (int) Math.ceil(this.maxZ);
		int volume = (blockMaxX - blockMinX) * (blockMaxY - blockMinY) * (blockMaxZ - blockMinZ);
		if (volume > 1000) {
			Bukkit.getLogger().warning("Cancelled check internal block request since the volume of the cuboid exceeds the maximum limit of 1000 cubic blocks.");
			return false;
		}

		Location location = new Location(world, blockMinX, blockMinY, blockMinZ);
		while (location.getBlockX() < blockMaxX) {
			while (location.getBlockZ() < blockMaxZ) {
				while (location.getBlockY() < blockMaxY) {
					if (predicate.test(location)) {
						return true;
					}

					location.setY(location.getBlockY() + 1);
				}

				location.setY(blockMinY);
				location.setZ(location.getBlockZ() + 1);
			}

			location.setY(blockMinY);
			location.setZ(blockMinZ);
			location.setX(location.getBlockX() + 1);
		}

		return false;
	}

	@Override
	protected Cuboid clone() {
		try {
			return (Cuboid) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	@Override
	public int hashCode() {
		int result = 5;
		final int prime = 17;
		result = result * prime + Double.hashCode(this.minX);
		result = result * prime + Double.hashCode(this.minY);
		result = result * prime + Double.hashCode(this.minZ);
		result = result * prime + Double.hashCode(this.maxX);
		result = result * prime + Double.hashCode(this.maxY);
		result = result * prime + Double.hashCode(this.maxZ);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Cuboid)) {
			return false;
		}

		Cuboid o = (Cuboid) obj;
		return this.minX == o.minX &&
				this.minY == o.minY &&
				this.minZ == o.minZ &&
				this.maxX == o.maxX &&
				this.maxY == o.maxY &&
				this.maxZ == o.maxZ;
	}

	@Override
	public String toString() {
		return "[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
	}
}
