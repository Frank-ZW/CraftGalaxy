package net.craftgalaxy.corepackets.client;

import net.craftgalaxy.corepackets.CoreClientboundPacket;

import java.io.Serializable;
import java.util.UUID;

public class CPacketPlayerFreeze implements CoreClientboundPacket, Serializable {

	private static final long serialVersionUID = 6432060131890975698L;
	private final UUID uniqueId;
	private final boolean advancedAlert;
	private final boolean unknownAddress;

	public CPacketPlayerFreeze(UUID uniqueId, boolean advancedAlert, boolean unknownAddress) {
		this.uniqueId = uniqueId;
		this.advancedAlert = advancedAlert;
		this.unknownAddress = unknownAddress;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}

	public boolean isAdvancedAlert() {
		return this.advancedAlert;
	}

	public boolean isUnknownAddress() {
		return this.unknownAddress;
	}
}
