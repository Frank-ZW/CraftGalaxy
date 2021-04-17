package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public class PacketPlayInEndConnection implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = -5964277011716251338L;
	private final Set<UUID> players;

	public PacketPlayInEndConnection(Set<UUID> players) {
		this.players = players;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}
}
