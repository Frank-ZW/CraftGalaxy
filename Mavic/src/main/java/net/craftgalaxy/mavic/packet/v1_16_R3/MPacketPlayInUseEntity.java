package net.craftgalaxy.mavic.packet.v1_16_R3;

import com.comphenix.protocol.events.PacketContainer;
import net.craftgalaxy.mavic.packet.impl.APacketPlayInUseEntity;
import org.bukkit.entity.Player;

public class MPacketPlayInUseEntity extends APacketPlayInUseEntity {

	@Override
	public void accept(Player player, PacketContainer packet) {
		this.action = packet.getEntityUseActions().read(0);
		this.entity = packet.getEntityModifier(player.getWorld()).read(0);
	}
}
