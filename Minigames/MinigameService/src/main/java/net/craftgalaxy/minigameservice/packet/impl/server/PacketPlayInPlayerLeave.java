package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.UUID;

public class PacketPlayInPlayerLeave implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 218648040844866216L;
	private final UUID player;
	private final int minigamePlayers;
	private final int maxPlayers;

	public PacketPlayInPlayerLeave(UUID player, int minigamePlayers, int maxPlayers) {
		this.player = player;
		this.minigamePlayers = minigamePlayers;
		this.maxPlayers = maxPlayers;
	}

	public UUID getPlayer() {
		return this.player;
	}

	public int getMinigamePlayers() {
		return this.minigamePlayers;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}
}
