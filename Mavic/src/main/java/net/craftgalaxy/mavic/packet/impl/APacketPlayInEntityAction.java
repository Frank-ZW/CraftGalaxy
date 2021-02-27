package net.craftgalaxy.mavic.packet.impl;

import com.comphenix.protocol.wrappers.EnumWrappers;
import net.craftgalaxy.mavic.packet.AbstractPacket;

public abstract class APacketPlayInEntityAction extends AbstractPacket {

	protected int entityId;
	protected EnumWrappers.PlayerAction playerAction;

	public int getEntityId() {
		return this.entityId;
	}

	public EnumWrappers.PlayerAction getPlayerAction() {
		return this.playerAction;
	}
}
