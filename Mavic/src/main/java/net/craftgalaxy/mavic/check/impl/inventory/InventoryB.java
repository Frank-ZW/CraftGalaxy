package net.craftgalaxy.mavic.check.impl.inventory;

import com.comphenix.protocol.wrappers.EnumWrappers;
import net.craftgalaxy.mavic.check.types.PacketCheck;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.packet.AbstractPacket;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInUseEntity;
import org.bukkit.event.inventory.InventoryType;

public class InventoryB extends PacketCheck {

	public InventoryB(PlayerData playerData) {
		super(playerData, "Inventory B", 12, CheckType.DEVELOPMENT);
	}

	@Override
	public void handle(AbstractPacket abstractPacket, long timestamp) {
		if (abstractPacket instanceof APacketPlayInUseEntity) {
			APacketPlayInUseEntity packet = (APacketPlayInUseEntity) abstractPacket;
			if (packet.getAction() == EnumWrappers.EntityUseAction.ATTACK && this.player.getOpenInventory().getType() != InventoryType.PLAYER) {
				this.handleViolation();
			}
		}
	}
}
