package net.craftgalaxy.corepackets.client;

import net.craftgalaxy.corepackets.CoreClientboundPacket;

import java.io.Serializable;
import java.util.UUID;

public class CPacketPlayerUnfreeze implements CoreClientboundPacket, Serializable {

	private static final long serialVersionUID = -5904335083989603686L;
	private final UUID uniqueId;
	private final boolean advancedAlert;

	public CPacketPlayerUnfreeze(UUID uniqueId, boolean advancedAlert) {
		this.uniqueId = uniqueId;
		this.advancedAlert = advancedAlert;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}

	public boolean isAdvancedAlert() {
		return this.advancedAlert;
	}
}
