package net.craftgalaxy.mavic.packet.impl;

import net.craftgalaxy.mavic.packet.AbstractPacket;
import org.bukkit.entity.Entity;

public abstract class APacketPlayOutEntityVelocity extends AbstractPacket {

	protected int entityId;
	protected Entity entity;
	protected double velX;
	protected double velY;
	protected double velZ;

	public int getEntityId() {
		return this.entityId;
	}

	public Entity getEntity() {
		return this.entity;
	}

	public double getVelX() {
		return this.velX;
	}

	public double getVelY() {
		return this.velY;
	}

	public double getVelZ() {
		return this.velZ;
	}
}
