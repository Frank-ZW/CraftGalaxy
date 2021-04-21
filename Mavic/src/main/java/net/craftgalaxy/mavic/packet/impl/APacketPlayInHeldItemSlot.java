package net.craftgalaxy.mavic.packet.impl;

import net.craftgalaxy.mavic.packet.AbstractPacket;

public abstract class APacketPlayInHeldItemSlot extends AbstractPacket {

	protected int heldItemIndex;

	public int getHeldItemIndex() {
		return this.heldItemIndex;
	}
}
