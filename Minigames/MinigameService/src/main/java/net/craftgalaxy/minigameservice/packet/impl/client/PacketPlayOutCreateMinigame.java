package net.craftgalaxy.minigameservice.packet.impl.client;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayOut;

import java.io.Serializable;

public class PacketPlayOutCreateMinigame implements MinigamePacketPlayOut, Serializable {

	private static final long serialVersionUID = 5902581167782409749L;
	private final int minigameId;
	private final int gameKey;
	private final int maxPlayers;

	public PacketPlayOutCreateMinigame(int minigameId, int gameKey, int maxPlayers) {
		this.minigameId = minigameId;
		this.gameKey = gameKey;
		this.maxPlayers = maxPlayers;
	}

	public int getMinigameId() {
		return this.minigameId;
	}

	public int getGameKey() {
		return this.gameKey;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}
}
