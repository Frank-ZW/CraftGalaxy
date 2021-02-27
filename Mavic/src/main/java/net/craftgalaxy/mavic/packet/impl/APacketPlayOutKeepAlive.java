package net.craftgalaxy.mavic.packet.impl;

import net.craftgalaxy.mavic.packet.AbstractPacket;

public abstract class APacketPlayOutKeepAlive extends AbstractPacket {

	protected long id;

	public long getId() {
		return this.id;
	}
}
