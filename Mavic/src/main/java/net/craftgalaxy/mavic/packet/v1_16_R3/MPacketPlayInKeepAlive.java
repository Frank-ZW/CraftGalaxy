package net.craftgalaxy.mavic.packet.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInKeepAlive;
import org.bukkit.entity.Player;

public class MPacketPlayInKeepAlive extends APacketPlayInKeepAlive {

	@Override
	public void accept(Player player, PacketContainer packet) {
		this.id = packet.getLongs().read(0);
	}
}
