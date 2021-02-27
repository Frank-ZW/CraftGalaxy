package net.craftgalaxy.corepackets.server;

import net.craftgalaxy.corepackets.CoreServerboundPacket;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public class SPacketDisconnectRequest implements CoreServerboundPacket, Serializable {

	private static final long serialVersionUID = -1119312358956416673L;
	private final Set<UUID> players;

	public SPacketDisconnectRequest(Set<UUID> players) {
		this.players = players;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}
}
