package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public class PacketPlayInRequestDisconnect implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 6193139618338871479L;
	private final Set<UUID> players;

	public PacketPlayInRequestDisconnect(Set<UUID> players) {
		this.players = players;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}
}
