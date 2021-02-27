package net.craftgalaxy.mavic.packet.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInFlying;
import org.bukkit.entity.Player;

public class MPacketPlayInFlying extends APacketPlayInFlying {

	@Override
	public void accept(Player player, PacketContainer packet) {
		this.x = 0.0D;
		this.y = 0.0D;
		this.z = 0.0D;
		this.yaw = 0.0F;
		this.pitch = 0.0F;
		this.onGround = packet.getBooleans().read(0);
		this.pos = false;
		this.look = false;
	}
}
