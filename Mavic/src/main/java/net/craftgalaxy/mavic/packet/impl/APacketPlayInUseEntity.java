package net.craftgalaxy.mavic.packet.impl;

import com.comphenix.protocol.wrappers.EnumWrappers;
import net.craftgalaxy.mavic.packet.AbstractPacket;
import org.bukkit.entity.Entity;

public abstract class APacketPlayInUseEntity extends AbstractPacket {

	protected Entity entity;
	protected EnumWrappers.EntityUseAction action;

	public Entity getEntity() {
		return this.entity;
	}

	public EnumWrappers.EntityUseAction getAction() {
		return this.action;
	}
}
