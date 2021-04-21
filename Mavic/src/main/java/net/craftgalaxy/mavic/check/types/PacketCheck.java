package net.craftgalaxy.mavic.check.types;

import net.craftgalaxy.mavic.check.Check;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.packet.AbstractPacket;

public abstract class PacketCheck extends Check {

	public PacketCheck(PlayerData playerData, String name, int maxViolations, CheckType checkType) {
		super(playerData, name, maxViolations, checkType);
	}

	public abstract void handle(AbstractPacket abstractPacket, long timestamp);
}
