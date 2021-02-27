package net.craftgalaxy.mavic.packet.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import net.craftgalaxy.mavic.packet.impl.APacketPlayOutEntityVelocity;
import org.bukkit.entity.Player;

public class MPacketPlayOutEntityVelocity extends APacketPlayOutEntityVelocity {

	@Override
	public void accept(Player player, PacketContainer packet) {
		this.entityId = packet.getIntegers().read(0);
		this.entity = packet.getEntityModifier(player.getWorld()).read(0);
		this.velX = packet.getIntegers().read(1) / 8000.0D;
		this.velY = packet.getIntegers().read(2) / 8000.0D;
		this.velZ = packet.getIntegers().read(3) / 8000.0D;
	}
}
