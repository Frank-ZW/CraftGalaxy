package net.craftgalaxy.corepackets.client;

import net.craftgalaxy.corepackets.CoreClientboundPacket;

import java.io.Serializable;

public class CPacketDisconnectConfirm implements CoreClientboundPacket, Serializable {

	private static final long serialVersionUID = -8609579889672224547L;
	private final boolean invalid;

	public CPacketDisconnectConfirm(boolean invalid) {
		this.invalid = invalid;
	}

	public boolean isInvalid() {
		return this.invalid;
	}
}
