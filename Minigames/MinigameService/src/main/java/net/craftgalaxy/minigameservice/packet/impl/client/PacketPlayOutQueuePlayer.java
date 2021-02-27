package net.craftgalaxy.minigameservice.packet.impl.client;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayOut;

import java.io.Serializable;
import java.util.UUID;

public class PacketPlayOutQueuePlayer implements MinigamePacketPlayOut, Serializable {

	private static final long serialVersionUID = -3115099148151015073L;
	private final UUID uniqueId;

	public PacketPlayOutQueuePlayer(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}
}
