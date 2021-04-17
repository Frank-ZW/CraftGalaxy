package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public class PacketPlayInUpdatePlayerStatus implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 6861876666511771712L;
	private final Collection<UUID> players;
	private final byte status;

	public PacketPlayInUpdatePlayerStatus(Collection<UUID> players, byte status) {
		this.players = players;
		this.status = status;
	}

	public Collection<UUID> getPlayers() {
		return this.players;
	}

	public byte getStatus() {
		return this.status;
	}
}
