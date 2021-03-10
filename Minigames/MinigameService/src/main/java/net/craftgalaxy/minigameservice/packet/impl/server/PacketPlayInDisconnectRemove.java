package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.UUID;

public class PacketPlayInDisconnectRemove implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = -8130640438002112725L;
	private final UUID uniqueId;

	public PacketPlayInDisconnectRemove(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}
}
