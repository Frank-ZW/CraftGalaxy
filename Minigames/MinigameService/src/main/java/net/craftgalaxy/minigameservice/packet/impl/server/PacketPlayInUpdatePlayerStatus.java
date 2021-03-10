package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.UUID;

public class PacketPlayInUpdatePlayerStatus implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 6861876666511771712L;
	private final UUID uniqueId;
	private final byte status;

	public PacketPlayInUpdatePlayerStatus(UUID uniqueId, byte status) {
		this.uniqueId = uniqueId;
		this.status = status;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}

	public byte getStatus() {
		return this.status;
	}
}
