package net.craftgalaxy.mavic.packet.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInEntityAction;
import org.bukkit.entity.Player;

public class MPacketPlayInEntityAction extends APacketPlayInEntityAction {

	@Override
	public void accept(Player player, PacketContainer packet) {
		this.entityId = packet.getIntegers().read(0);
		this.playerAction = packet.getPlayerActions().read(0);
	}
}
