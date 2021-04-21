package net.craftgalaxy.mavic.packet.manager;

import net.craftgalaxy.mavic.util.location.MutableBlockLocation;
import net.minecraft.server.v1_16_R1.BlockPosition;
import net.minecraft.server.v1_16_R1.WorldServer;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.util.CraftMagicNumbers;
import org.jetbrains.annotations.NotNull;

public class NMSManager {

	private static NMSManager instance;

	public static NMSManager getInstance() {
		return instance == null ? instance = new NMSManager() : instance;
	}

	public Material getType(@NotNull World world, @NotNull MutableBlockLocation location) {
		WorldServer server = ((CraftWorld) world).getHandle();
		BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		if (server.isChunkLoaded(position.getX() >> 4, position.getZ() >> 4)) {
			return CraftMagicNumbers.getMaterial(server.getType(position).getBlock());
		}

		return Material.AIR;
	}
}
