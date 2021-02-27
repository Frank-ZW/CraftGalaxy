package net.craftgalaxy.asyncwilderness.runnable;

import io.papermc.lib.PaperLib;
import net.craftgalaxy.asyncwilderness.AsyncWilderness;
import net.craftgalaxy.asyncwilderness.util.BiomeUtil;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class TeleportSupplier implements Supplier<Location> {

	private final AsyncWilderness plugin;
	private final World world;
	private Location location;

	public TeleportSupplier(AsyncWilderness plugin) {
		this.plugin = plugin;
		this.world = plugin.getWorld();
	}

	public boolean checkBiomeInternally(Chunk chunk) {
		for (int i = 0; i < 16; i += 5) {
			for (int j = 0; j < 16; j += 5) {
				Block block = chunk.getBlock(i, 0, j);
				if (BiomeUtil.isInvalidBiome(block)) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean checkLocationInternally() {
		return this.location != null && this.location.getBlock().getType() != Material.LAVA && this.location.getBlock().getType() != Material.CACTUS;
	}

	@Override
	public Location get() {
		int tries = this.plugin.getTries() < 0 ? Integer.MAX_VALUE : this.plugin.getTries();
		while (tries-- > 0) {
			int x = ThreadLocalRandom.current().nextInt(this.plugin.getMinX(), this.plugin.getMaxX() + 1);
			int z = ThreadLocalRandom.current().nextInt(this.plugin.getMinZ(), this.plugin.getMaxZ() + 1);
			int y = this.world.getHighestBlockYAt(x, z);
			this.location = new Location(this.world, x, y, z);
			Chunk chunk = PaperLib.getChunkAtAsync(this.location).join();
			if (this.checkBiomeInternally(chunk)) {
				if (this.checkLocationInternally()) {
					return this.location;
				}

				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 16; j++) {
						x = chunk.getX() * 16 + i;
						z = chunk.getZ() * 16 + j;
						y = this.world.getHighestBlockYAt(x, z);
						this.location = new Location(this.world, x, y, z);
						if (this.checkLocationInternally()) {
							return this.location;
						}
					}
				}
			}
		}

		return this.location;
	}

	@Override
	public int hashCode() {
		final int prime = 53;
		int result = 1;
		result = result * prime + this.world.hashCode();
		result = result * prime + (this.location == null ? 0 : this.location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof TeleportSupplier)) {
			return false;
		}

		TeleportSupplier o = (TeleportSupplier) obj;
		return this.world.equals(o.world) && Objects.equals(this.location, o.location);
	}
}
