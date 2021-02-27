package net.craftgalaxy.mavic.packet.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInLook;
import org.bukkit.entity.Player;

public class MPacketPlayInLook extends APacketPlayInLook {

	@Override
	public void accept(Player player, PacketContainer packet) {
		this.x = 0.0D;
		this.y = 0.0D;
		this.z = 0.0D;
		this.yaw = packet.getFloat().read(0);
		this.pitch = packet.getFloat().read(1);
		this.onGround = packet.getBooleans().read(0);
		this.pos = false;
		this.look = true;
	}
}
