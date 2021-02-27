package net.craftgalaxy.mavic.packet.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInPosition;
import org.bukkit.entity.Player;

public class MPacketPlayInPosition extends APacketPlayInPosition {

	@Override
	public void accept(Player player, PacketContainer packet) {
		this.x = packet.getDoubles().read(0);
		this.y = packet.getDoubles().read(1);
		this.z = packet.getDoubles().read(2);
		this.yaw = 0.0F;
		this.pitch = 0.0F;
		this.onGround = packet.getBooleans().read(0);
		this.pos = true;
		this.look = false;
	}
}
