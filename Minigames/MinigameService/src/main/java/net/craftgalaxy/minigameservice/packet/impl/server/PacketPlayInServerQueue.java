package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;

public class PacketPlayInServerQueue implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 3079104101880053755L;
	private final boolean reset;

	public PacketPlayInServerQueue(boolean reset) {
		this.reset = reset;
	}

	public boolean isReset() {
		return this.reset;
	}
}
