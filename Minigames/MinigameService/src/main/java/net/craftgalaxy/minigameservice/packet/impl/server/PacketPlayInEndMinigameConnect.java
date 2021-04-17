package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public class PacketPlayInEndMinigameConnect implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = -5964277011716251338L;
	private final Collection<UUID> players;

	public PacketPlayInEndMinigameConnect(Collection<UUID> players) {
		this.players = players;
	}

	public Collection<UUID> getPlayers() {
		return this.players;
	}
}
