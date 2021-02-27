package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public class PacketPlayInStartTeleport implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = -7864170729864921814L;
	private final Set<UUID> players;

	public PacketPlayInStartTeleport(Set<UUID> players) {
		this.players = players;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}
}
