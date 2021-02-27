package net.craftgalaxy.asyncwilderness;

import net.craftgalaxy.asyncwilderness.command.ReloadCommand;
import net.craftgalaxy.asyncwilderness.command.TeleportCommand;
import net.craftgalaxy.asyncwilderness.runnable.manager.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class AsyncWilderness extends JavaPlugin {

	private int initialTeleportDuration;
	private int tries;
	private int minX;
	private int minZ;
	private int maxX;
	private int maxZ;
	private long donatorCooldown;
	private long defaultCooldown;
	private boolean spawnParticles;
	private boolean playEndermanEffect;
	private World world;

	@Override
	public void onEnable() {
		boolean success = this.readConfig(true);
		TeleportManager.enable(this, this.defaultCooldown, this.donatorCooldown);
		PluginCommand rtp = this.getCommand("rtp");
		if (rtp != null) {
			rtp.setExecutor(new TeleportCommand(this));
		}

		PluginCommand reload = this.getCommand("asyncwild");
		if (reload != null) {
			reload.setExecutor(new ReloadCommand(this));
		}

		if (!success) {
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		TeleportManager.disable();
		this.saveConfig();
	}

	public boolean readConfig(boolean saveDefaultConfig) {
		if (saveDefaultConfig) {
			this.saveDefaultConfig();
		} else {
			this.reloadConfig();
		}

		try {
			String name = this.getConfig().getString("world-settings.name");
			if (name == null) {
				Bukkit.getLogger().warning("A name for the world to randomly teleport players to does not exist. Please make sure all values in the config.yml file are appropriately entered.");
				return false;
			}

			this.world = this.getServer().getWorld(name);
			if (this.world == null) {
				Bukkit.getLogger().warning("A world with the name " + name + " does not exist. Has it been loaded into memory?");
				return false;
			}

			int x1 = this.getConfig().getInt("world-settings.x1");
			int z1 = this.getConfig().getInt("world-settings.z1");
			int x2 = this.getConfig().getInt("world-settings.x2");
			int z2 = this.getConfig().getInt("world-settings.z2");
			this.minX = Math.min(x1, x2);
			this.minZ = Math.min(z1, z2);
			this.maxX = Math.max(x1, x2);
			this.maxZ = Math.max(z1, z2);
			this.tries = this.getConfig().getInt("teleportation-settings.tries");
			this.spawnParticles = this.getConfig().getBoolean("teleportation-settings.spawn-particles");
			this.playEndermanEffect = this.getConfig().getBoolean("teleportation-settings.play-enderman-sound");
			this.defaultCooldown = this.getConfig().getLong("cooldowns.default-in-seconds");
			this.donatorCooldown = this.getConfig().getLong("cooldowns.donator-in-seconds");
			this.initialTeleportDuration = this.getConfig().getInt("cooldowns.initial-teleport-duration");
			return true;
		} catch (NumberFormatException e) {
			Bukkit.getLogger().warning("AsyncWilderness failed to read in one or more values from the configuration file. Before reloading the plugin, ensure that all numbers are correctly entered as numbers.");
			return false;
		}
	}

	public boolean isSpawnParticles() {
		return this.spawnParticles;
	}

	public boolean isPlayEndermanEffect() {
		return this.playEndermanEffect;
	}

	public int getTries() {
		return this.tries;
	}

	public int getMinX() {
		return this.minX;
	}

	public int getMinZ() {
		return this.minZ;
	}

	public int getMaxX() {
		return this.maxX;
	}

	public int getMaxZ() {
		return this.maxZ;
	}

	public int getInitialTeleportDuration() {
		return this.initialTeleportDuration;
	}

	public World getWorld() {
		return this.world;
	}
}
