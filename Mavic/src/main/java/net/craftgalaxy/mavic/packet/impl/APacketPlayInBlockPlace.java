package net.craftgalaxy.mavic.packet.impl;

import com.comphenix.protocol.wrappers.EnumWrappers;
import net.craftgalaxy.mavic.packet.AbstractPacket;

public abstract class APacketPlayInBlockPlace extends AbstractPacket {

	protected EnumWrappers.Hand hand;
	protected long timestamp;

	public EnumWrappers.Hand getHand() {
		return this.hand;
	}

	public long getTimestamp() {
		return this.timestamp;
	}
}
