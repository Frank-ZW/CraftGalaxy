package net.craftgalaxy.mavic.packet.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInHeldItemSlot;
import org.bukkit.entity.Player;

public class MPacketPlayInHeldItemSlot extends APacketPlayInHeldItemSlot {

	@Override
	public void accept(Player player, PacketContainer packet) {
		this.heldItemIndex = packet.getIntegers().read(0);
	}
}
