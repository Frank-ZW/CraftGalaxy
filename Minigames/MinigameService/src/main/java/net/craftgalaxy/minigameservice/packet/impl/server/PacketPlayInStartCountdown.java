package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public class PacketPlayInStartCountdown implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 4400994757886390561L;
	private final Set<UUID> players;
	private final int gameKey;

	public PacketPlayInStartCountdown(Set<UUID> players, int gameKey) {
		this.players = players;
		this.gameKey = gameKey;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}

	public int getGameKey() {
		return this.gameKey;
	}
}
