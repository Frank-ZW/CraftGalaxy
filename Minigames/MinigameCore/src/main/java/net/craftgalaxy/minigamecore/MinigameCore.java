package net.craftgalaxy.minigamecore;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.craftgalaxy.minigamecore.command.GameKeyCommand;
import net.craftgalaxy.minigamecore.command.LeaveCommand;
import net.craftgalaxy.minigamecore.listener.MinigameListener;
import net.craftgalaxy.minigamecore.listener.PlayerListener;
import net.craftgalaxy.minigamecore.minigame.MinigameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public final class MinigameCore extends JavaPlugin {

	private Location lobbyLocation;
	private final Map<String, CommandExecutor> commands = ImmutableMap.of("leave", new LeaveCommand(), "gamekey", new GameKeyCommand());
	private final List<Listener> listeners = ImmutableList.of(new PlayerListener(), new MinigameListener());

	public static String BUNGEE_SERVER_NAME;
	public static String SOCKET_HOST_NAME;
	public static int SOCKET_PORT_NUMBER;
	private static MinigameCore instance;

	@Override
	public void onEnable() {
		instance = this;
		this.saveDefaultConfig();
		boolean shutdown = this.readConfig();
		this.registerListeners();
		this.registerCommands();
		MinigameManager.enable(this);
		if (shutdown) {
			Bukkit.getLogger().info(ChatColor.RED + "One or more errors was detected while reading the config.yml file. Before restarting the plugin, double check to make sure there are no errors during startup.");
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		MinigameManager.disable();
		HandlerList.unregisterAll(this);
		this.writeConfig();
		instance = null;
	}

	public static MinigameCore getInstance() {
		return instance;
	}

	public void registerCommands() {
		for (Map.Entry<String, CommandExecutor> entry : this.commands.entrySet()) {
			PluginCommand command = this.getCommand(entry.getKey());
			if (command != null) {
				command.setExecutor(entry.getValue());
			}
		}
	}

	public void registerListeners() {
		for (Listener listener : this.listeners) {
			Bukkit.getPluginManager().registerEvents(listener, this);
		}
	}

	public boolean readConfig() {
		try {
			MinigameCore.BUNGEE_SERVER_NAME = this.getConfig().getString("socket-settings.bungee-server-name");
			MinigameCore.SOCKET_HOST_NAME = this.getConfig().getString("socket-settings.host-name");
			MinigameCore.SOCKET_PORT_NUMBER = this.getConfig().getInt("socket-settings.port-number");

			String name = this.getConfig().getString("lobby-location-settings.world-name");
			if (name == null) {
				Bukkit.getLogger().warning("The name for the lobby world is missing from the config files.");
				return true;
			}

			World world = Bukkit.getWorld(name);
			if (world == null) {
				Bukkit.getLogger().warning("Failed to locate a world with the name " + name + ". Is it loaded into memory?");
				return true;
			}

			double x = this.getConfig().getDouble("lobby-location-settings.X");
			double y = this.getConfig().getDouble("lobby-location-settings.Y");
			double z = this.getConfig().getDouble("lobby-location-settings.Z");
			if (y == -1) {
				Bukkit.getLogger().info(ChatColor.GREEN + "A value of '-1' has been entered in the Y-value for the config file... locating the highest Y-value at the given locations X = " + x + " and Z = " + z + ".");
				y = world.getHighestBlockYAt((int) x, (int) z);
			}

			this.lobbyLocation = new Location(world, x, ++y, z);
			return false;
		} catch (NumberFormatException e) {
			Bukkit.getLogger().warning("One or more fields entered in the configuration file does not match its designated type. Before restarting the server, check to make sure the port number and X, Y, Z values entered are a number.");
			return true;
		}
	}

	public void writeConfig() {
		if (this.lobbyLocation != null) {
			this.getConfig().set("lobby-location-settings.world-name", this.lobbyLocation.getWorld().getName());
			this.getConfig().set("lobby-location-settings.X", this.lobbyLocation.getX());
			this.getConfig().set("lobby-location-settings.Y", this.lobbyLocation.getY());
			this.getConfig().set("lobby-location-settings.Z", this.lobbyLocation.getZ());
		}
	}

	public Location getLobbyLocation() {
		return this.lobbyLocation;
	}
}
