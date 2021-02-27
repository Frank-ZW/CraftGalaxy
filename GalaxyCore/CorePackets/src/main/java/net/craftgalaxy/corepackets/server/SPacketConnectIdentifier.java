package net.craftgalaxy.corepackets.server;

import net.craftgalaxy.corepackets.CoreServerboundPacket;

import java.io.Serializable;

public class SPacketConnectIdentifier implements CoreServerboundPacket, Serializable {

	private static final long serialVersionUID = -6435623496233306792L;
	private final String serverName;

	public SPacketConnectIdentifier(String serverName) {
		this.serverName = serverName;
	}

	public String getServerName() {
		return this.serverName;
	}
}
