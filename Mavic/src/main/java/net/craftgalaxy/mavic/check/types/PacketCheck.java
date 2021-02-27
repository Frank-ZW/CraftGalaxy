package net.craftgalaxy.mavic.check.types;

import net.craftgalaxy.mavic.check.Check;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.packet.AbstractPacket;

public abstract class PacketCheck extends Check {
		public PacketCheck(String name, int maxViolations, CheckType checkType) {
			super(name, maxViolations, checkType);
		}

		public abstract void handle(PlayerData playerData, AbstractPacket abstractPacket, long timestamp);
}
