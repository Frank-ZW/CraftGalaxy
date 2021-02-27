package net.craftgalaxy.mavic.packet;

import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public abstract class AbstractPacket {

	public abstract void accept(Player player, PacketContainer packet);
}
