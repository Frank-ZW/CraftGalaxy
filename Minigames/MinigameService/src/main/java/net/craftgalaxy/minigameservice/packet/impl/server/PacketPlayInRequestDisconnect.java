package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PacketPlayInRequestDisconnect implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 6193139618338871479L;
	private final Set<UUID> players;

	public PacketPlayInRequestDisconnect(Collection<? extends Player> players) {
		this.players = players.stream().map(Player::getUniqueId).collect(Collectors.toSet());
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}
}
