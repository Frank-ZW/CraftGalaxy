package net.craftgalaxy.mavic.util;

import net.craftgalaxy.mavic.util.java.MathUtil;

public class Velocity {

	private double x;
	private double y;
	private double z;
	private int totalTicks;
	private boolean attenuated;
	private final long timestamp = System.currentTimeMillis();

	public Velocity(double x, double y, double z, int totalTicks) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.totalTicks = totalTicks;
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return this.z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public int getTotalTicks() {
		return this.totalTicks;
	}

	public void setTotalTicks(int totalTicks) {
		this.totalTicks = totalTicks;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public boolean attenuate(boolean onGround) {
		if (onGround) {
			this.y = 0.0D;
		} else if (this.y > 0.0D) {
			this.y -= Math.min(this.y, 0.08D);
		}

		this.x *= 0.91;
		this.y *= 0.98;
		this.z *= 0.91;
		if (onGround) {
			this.x *= 0.6;
			this.z *= 0.6;
		}

		this.attenuated = true;
		return Math.abs(MathUtil.highestAbsDouble(this.x, this.y, this.z)) <= 0.001D;
	}
}
