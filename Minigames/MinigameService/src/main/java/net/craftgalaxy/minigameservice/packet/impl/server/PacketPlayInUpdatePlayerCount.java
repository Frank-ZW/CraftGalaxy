package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;

public class PacketPlayInUpdatePlayerCount implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 3056400189747080160L;
	private final int players;
	private final int maxPlayers;

	public PacketPlayInUpdatePlayerCount(int players, int maxPlayers) {
		this.players = players;
		this.maxPlayers = maxPlayers;
	}

	public int getPlayers() {
		return this.players;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}
}
