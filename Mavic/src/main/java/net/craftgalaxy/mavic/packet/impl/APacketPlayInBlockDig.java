package net.craftgalaxy.mavic.packet.impl;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.craftgalaxy.mavic.packet.AbstractPacket;

public abstract class APacketPlayInBlockDig extends AbstractPacket {

	protected BlockPosition position;
	protected EnumWrappers.Direction direction;
	protected EnumWrappers.PlayerDigType digType;

	public BlockPosition getPosition() {
		return this.position;
	}

	public EnumWrappers.Direction getDirection() {
		return this.direction;
	}

	public EnumWrappers.PlayerDigType getDigType() {
		return this.digType;
	}
}
