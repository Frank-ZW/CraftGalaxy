package net.craftgalaxy.minigameservice.packet.impl.server;

import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;

import java.io.Serializable;

public class PacketPlayInServerConnect implements MinigamePacketPlayIn, Serializable {

	private static final long serialVersionUID = 4341735093881022035L;
	private final String name;

	public PacketPlayInServerConnect(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
