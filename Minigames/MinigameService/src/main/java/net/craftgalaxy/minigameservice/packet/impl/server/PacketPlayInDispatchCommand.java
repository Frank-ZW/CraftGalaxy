package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;

public class PacketPlayInDispatchCommand implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = -7260393792308028361L;
	private final String player;
	private final String minigame;
	private final int maxPlayers;

	public PacketPlayInDispatchCommand(String player, String minigame, int maxPlayers) {
		this.player = player;
		this.minigame = minigame;
		this.maxPlayers = maxPlayers;
	}

	public String getPlayer() {
		return this.player;
	}

	public String getMinigame() {
		return this.minigame;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}
}
