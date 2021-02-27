package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PacketPlayInPlayerConnect implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 6646130957389774483L;
	private final Set<UUID> players = new HashSet<>();

	public PacketPlayInPlayerConnect(@NotNull Set<UUID> players) {
		this.players.addAll(players);
	}

	public PacketPlayInPlayerConnect(UUID player) {
		this.players.add(player);
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}
}
