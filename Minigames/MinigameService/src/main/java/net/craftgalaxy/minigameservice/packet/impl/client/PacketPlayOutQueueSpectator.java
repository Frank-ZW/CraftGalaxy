package net.craftgalaxy.minigameservice.packet.impl.client;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayOut;

import java.io.Serializable;
import java.util.UUID;

public class PacketPlayOutQueueSpectator implements MinigamePacketPlayOut, Serializable {

	private static final long serialVersionUID = -598481631821165050L;
	private final UUID spectator;
	private final UUID player;

	public PacketPlayOutQueueSpectator(UUID spectator, UUID player) {
		this.spectator = spectator;
		this.player = player;
	}

	public UUID getSpectator() {
		return this.spectator;
	}

	public UUID getPlayer() {
		return this.player;
	}
}
