package net.craftgalaxy.corepackets.client;

import net.craftgalaxy.corepackets.CoreClientboundPacket;

import java.io.Serializable;
import java.util.UUID;

public class CPacketPasswordRequest implements CoreClientboundPacket, Serializable {

	private static final long serialVersionUID = 5048340011893321778L;
	private final UUID uniqueId;

	public CPacketPasswordRequest(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}
}
