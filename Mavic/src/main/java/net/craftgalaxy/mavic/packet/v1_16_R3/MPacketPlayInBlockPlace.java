package net.craftgalaxy.mavic.packet.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInBlockPlace;
import org.bukkit.entity.Player;

public class MPacketPlayInBlockPlace extends APacketPlayInBlockPlace {

	@Override
	public void accept(Player player, PacketContainer packet) {
		this.hand = packet.getHands().read(0);
		this.timestamp = packet.getLongs().read(0);
	}
}
