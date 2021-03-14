package net.craftgalaxy.bungeecore;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.craftgalaxy.bungeecore.command.ForceEndCommand;
import net.craftgalaxy.bungeecore.command.PlayCommand;
import net.craftgalaxy.bungeecore.data.manager.PlayerManager;
import net.craftgalaxy.bungeecore.data.manager.ServerManager;
import net.craftgalaxy.bungeecore.listener.PlayerListener;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.util.CaseInsensitiveSet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class BungeeCore extends Plugin {

	private File configFile;
	private Configuration config;
	private final Random random = new Random();
	private final Set<String> minigames = new CaseInsensitiveSet();
	private final Set<String> mainLobbies = new CaseInsensitiveSet();
	private final Set<String> minigameLobbies = new CaseInsensitiveSet();
	private final Set<String> specialized = new CaseInsensitiveSet();
	private AtomicInteger gameKey;
	private int portNumber;

	private final List<Command> commands = ImmutableList.of(new PlayCommand(this), new ForceEndCommand(this));
	private final List<Listener> listeners = ImmutableList.of(new PlayerListener(this));

	private static BungeeCore instance;

	@Override
	public void onEnable() {
		instance = this;
		boolean failed = this.readConfig(true);
		this.registerCommands();
		this.registerListeners();
		PlayerManager.enable(this);
		ServerManager.getInstance();
		if (failed) {
			this.onDisable();
		}
	}

	@Override
	public void onDisable() {
		ServerManager.disable();
		PlayerManager.disable();
		this.writeConfig();
		this.getProxy().getPluginManager().unregisterListeners(this);
		this.getProxy().getPluginManager().unregisterCommands(this);
		instance = null;
	}

	public void registerCommands() {
		for (Command command : this.commands) {
			this.getProxy().getPluginManager().registerCommand(this, command);
		}
	}

	public void registerListeners() {
		for (Listener listener : this.listeners) {
			this.getProxy().getPluginManager().registerListener(this, listener);
		}
	}

	private boolean saveDefaultConfig() {
		if (!this.getDataFolder().exists()) {
			if (!this.getDataFolder().mkdir()) {
				this.getLogger().warning("Failed to create plugin home directory.");
				return false;
			}
		}

		this.configFile = new File(this.getDataFolder(), "config.yml");
		if (!this.configFile.exists()) {
			try (
					InputStream input = this.getResourceAsStream("config.yml")
			) {
				Files.copy(input, this.configFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	private void saveConfig() {
		try{
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeConfig() {
		if (this.gameKey != null) {
			this.config.set("minigame-game-key", this.gameKey.get());
		}

		this.saveConfig();
	}

	public boolean readConfig(boolean saveDefaultConfig) {
		if (saveDefaultConfig && !this.saveDefaultConfig()) {
			return true;
		}

		try {
			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.configFile);

			this.minigames.addAll(this.config.getStringList("server-settings.minigames"));
			this.mainLobbies.addAll(this.config.getStringList("server-settings.main-lobbies"));
			this.minigameLobbies.addAll(this.config.getStringList("server-settings.minigame-lobbies"));
			this.specialized.addAll(this.config.getStringList("server-settings.specialized"));

			this.gameKey = new AtomicInteger(this.config.getInt("minigame-game-key"));
			this.portNumber = this.config.getInt("socket-settings.port-number");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		} catch (NumberFormatException e) {
			this.getLogger().warning("Failed to read in a number from the config file. Please check to make sure all values entered in the config.yml file are whole numbers.");
			return true;
		}
	}

	public boolean isLobbyServer(String name) {
		return this.mainLobbies.contains(name) || this.minigameLobbies.contains(name);
	}

	public boolean isSpecializedServer(String name) {
		return this.specialized.contains(name);
	}

	public boolean isMinigameServer(String name) {
		return this.minigames.contains(name);
	}

	public ServerInfo getMainLobby() {
		if (this.mainLobbies.isEmpty()) {
			throw new IllegalStateException("There are no main lobbies registered in the BungeeCore configuration file.");
		}

		return this.getProxy().getServerInfo(Iterables.get(this.mainLobbies, this.random.nextInt(this.mainLobbies.size())));
	}

	public ServerInfo getMinigameLobby() {
		if (this.minigameLobbies.isEmpty()) {
			throw new IllegalStateException("There are no minigame lobbies registered in the BungeeCore configuration file.");
		}

		return this.getProxy().getServerInfo(Iterables.get(this.minigameLobbies, this.random.nextInt(this.minigameLobbies.size())));
	}

	public void removeServer(String name) {
		this.mainLobbies.remove(name);
		this.minigameLobbies.remove(name);
		this.minigames.remove(name);
	}

	public int getAndIncrementGameKey() {
		return this.gameKey.getAndIncrement();
	}

	public int getPortNumber() {
		return this.portNumber;
	}

	public static BungeeCore getInstance() {
		return instance;
	}
}
