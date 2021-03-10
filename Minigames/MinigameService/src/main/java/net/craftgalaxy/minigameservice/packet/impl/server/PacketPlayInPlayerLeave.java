package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;
import java.util.UUID;

public class PacketPlayInPlayerLeave implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 218648040844866216L;
	private final UUID player;

	public PacketPlayInPlayerLeave(UUID player) {
		this.player = player;
	}

	public UUID getPlayer() {
		return this.player;
	}
}
