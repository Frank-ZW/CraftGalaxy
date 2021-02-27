package net.craftgalaxy.mavic.util.location;

import com.comphenix.protocol.wrappers.BlockPosition;
import net.craftgalaxy.mavic.packet.manager.NMSManager;
import org.bukkit.Material;
import org.bukkit.World;

public class MutableBlockLocation {

	private int blockX;
	private int blockY;
	private int blockZ;

	public MutableBlockLocation(BlockPosition position) {
		this(position.getX(), position.getY(), position.getZ());
	}

	public MutableBlockLocation(PlayerLocation location) {
		this((int) Math.floor(location.getX()), (int) Math.floor(location.getY()), (int) Math.floor(location.getZ()));
	}

	public MutableBlockLocation(int blockX, int blockY, int blockZ) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
	}

	public MutableBlockLocation add(int blockX, int blockY, int blockZ) {
		this.blockX += blockX;
		this.blockY += blockY;
		this.blockZ += blockZ;
		return this;
	}

	public MutableBlockLocation incrementX() {
		return this.add(1, 0, 0);
	}

	public MutableBlockLocation incrementY() {
		return this.add(0, 1, 0);
	}

	public MutableBlockLocation incrementZ() {
		return this.add(0, 0, 1);
	}

	public int getBlockX() {
		return this.blockX;
	}

	public void setBlockX(int blockX) {
		this.blockX = blockX;
	}

	public int getBlockY() {
		return this.blockY;
	}

	public void setBlockY(int blockY) {
		this.blockY = blockY;
	}

	public int getBlockZ() {
		return this.blockZ;
	}

	public void setBlockZ(int blockZ) {
		this.blockZ = blockZ;
	}

	public Material getType(World world) {
		return NMSManager.getInstance().getType(world, this);
	}
}
