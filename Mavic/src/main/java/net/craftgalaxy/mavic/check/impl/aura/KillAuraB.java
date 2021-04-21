package net.craftgalaxy.mavic.check.impl.aura;

import com.comphenix.protocol.wrappers.EnumWrappers;
import net.craftgalaxy.mavic.check.types.PacketCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.packet.AbstractPacket;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInBlockPlace;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInFlying;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInUseEntity;

public class KillAuraB extends PacketCheck {

	private boolean placing;

	public KillAuraB(PlayerData playerData) {
		super(playerData, "Kill Aura B", 18, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(AbstractPacket abstractPacket, long timestamp) {
		if (abstractPacket instanceof APacketPlayInFlying) {
			this.placing = false;
		} else if (abstractPacket instanceof APacketPlayInBlockPlace) {
			this.placing = true;
		} else if (abstractPacket instanceof APacketPlayInUseEntity) {
			APacketPlayInUseEntity packet = (APacketPlayInUseEntity) abstractPacket;
			if (packet.getAction() == EnumWrappers.EntityUseAction.ATTACK && this.placing) {
				this.handleViolation();
				this.placing = false;
			}
		}
	}
}
