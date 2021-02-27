package net.craftgalaxy.galaxycore.bungee;

import com.google.common.collect.ImmutableList;
import net.craftgalaxy.galaxycore.bungee.command.AltsCommand;
import net.craftgalaxy.galaxycore.bungee.command.FreezeCommand;
import net.craftgalaxy.galaxycore.bungee.data.manager.PlayerManager;
import net.craftgalaxy.galaxycore.bungee.data.manager.ServerManager;
import net.craftgalaxy.galaxycore.bungee.database.DatabaseManager;
import net.craftgalaxy.galaxycore.bungee.listener.PlayerListener;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class BungeePlugin extends Plugin {

	private final List<Command> commands = ImmutableList.of(new AltsCommand(this), new FreezeCommand(this));
	private final List<ServerInfo> fallbackServers = new ArrayList<>();
	private boolean setup;
	private File configFile;
	private Configuration config;
	public static int SERVER_PORT_NUMBER;
	private static BungeePlugin instance;

	@Override
	public void onEnable() {
		instance = this;
		if (!this.getDataFolder().exists()) {
			if (!this.getDataFolder().mkdir()) {
				this.getLogger().severe("Failed to create new home directory for GalaxyCore.");
			}
		}

		this.saveDefaultConfigs();
		boolean success = this.readConfigs();
		this.registerCommands();
		this.getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
		DatabaseManager.enable(this);
		ServerManager.enable(this);
		PlayerManager.enable(this);
		if (!success) {
			this.onDisable();
		}

		this.setup = true;
	}

	@Override
	public void onDisable() {
		this.setup = false;
		this.getProxy().getPluginManager().unregisterListeners(this);
		PlayerManager.disable();
		ServerManager.disable();
		DatabaseManager.disable();
		this.saveConfig();
		instance = null;
	}

	public void registerCommands() {
		for (Command command : this.commands) {
			this.getProxy().getPluginManager().registerCommand(this, command);
		}
	}

	public void saveDefaultConfigs() {
		this.configFile = new File(this.getDataFolder(), "config.yml");
		if (!this.configFile.exists()) {
			try (InputStream input = this.getResourceAsStream("config.yml")) {
				Files.copy(input, this.configFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean readConfigs() {
		List<String> fallbacks = this.config.getStringList("server-settings.fall-back-servers");
		for (String server : fallbacks) {
			ServerInfo fallback = this.getProxy().getServerInfo(server);
			if (fallback != null) {
				this.fallbackServers.add(fallback);
			} else {
				this.getLogger().warning("Failed to find a server with the name " + server + " in the bungee config.yml file. This server has been ignored.");
			}
		}

		return true;
	}

	public List<ServerInfo> getFallbackServers() {
		return this.fallbackServers;
	}

	public boolean isSetup() {
		return this.setup;
	}

	public static BungeePlugin getInstance() {
		return instance;
	}
}
