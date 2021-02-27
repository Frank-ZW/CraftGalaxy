package net.craftgalaxy.mavic.packet.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInBlockDig;
import org.bukkit.entity.Player;

public class MPacketPlayInBlockDig extends APacketPlayInBlockDig {

	@Override
	public void accept(Player player, PacketContainer packet) {
		this.direction = packet.getDirections().read(0);
		this.position = packet.getBlockPositionModifier().read(0);
		this.digType = packet.getPlayerDigTypes().read(0);
	}
}
