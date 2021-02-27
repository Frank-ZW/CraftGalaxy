package net.craftgalaxy.galaxycore.bukkit;

import com.palmergames.bukkit.towny.Towny;
import net.craftgalaxy.galaxycore.bukkit.listener.PlayerListener;
import net.craftgalaxy.galaxycore.bukkit.listener.TownyListener;
import net.craftgalaxy.galaxycore.bukkit.listener.VanillaChatListener;
import net.craftgalaxy.galaxycore.bukkit.player.PlayerManager;
import net.craftgalaxy.galaxycore.bukkit.socket.manager.SocketManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;

public final class CorePlugin extends JavaPlugin {

	private List<String> blacklistedWords;
	private int slowModeCooldown;
	private String bungeecordName;
	private String hostName;
	private int portNumber;

	private Towny towny;

	private static CorePlugin instance;

	@Override
	public void onEnable() {
		instance = this;
		this.saveDefaultConfig();
		if (!this.readConfig()) {
			Bukkit.getPluginManager().disablePlugin(this);
		}

		this.towny = (Towny) Bukkit.getPluginManager().getPlugin("Towny");
		if (this.getTowny().isPresent()) {
			Bukkit.getPluginManager().registerEvents(new TownyListener(this), this);
		} else {
			Bukkit.getPluginManager().registerEvents(new VanillaChatListener(this), this);
		}

		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		PlayerManager.enable(this);
		SocketManager.enable(this);
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		PlayerManager.disable();
		SocketManager.disable();
		instance = null;
	}

	public boolean readConfig() {
		try {
			this.blacklistedWords = this.getConfig().getStringList("chat-settings.blacklisted-words");
			this.slowModeCooldown = this.getConfig().getInt("chat-settings.slow-mode-cooldown");
			this.bungeecordName = this.getConfig().getString("socket-settings.bungeecord-server-name");
			this.portNumber = this.getConfig().getInt("socket-settings.port_number");
			this.hostName = this.getConfig().getString("socket-settings.host_name");
			return true;
		} catch (NumberFormatException e) {
			Bukkit.getLogger().warning("A field entered in the config.yml file does not match its designated data type. Before reloading this plugin, double check to make sure all numbers and strings are entered in the appropriate field.");
			return false;
		}
	}

	public List<String> getBlacklistedWords() {
		return this.blacklistedWords;
	}

	public int getSlowModeCooldown() {
		return this.slowModeCooldown;
	}

	public String getBungeecordName() {
		return this.bungeecordName;
	}

	public String getHostName() {
		return this.hostName;
	}

	public int getPortNumber() {
		return this.portNumber;
	}

	public Optional<Towny> getTowny() {
		return Optional.ofNullable(this.towny);
	}

	public boolean isMcMMO() {
		return Bukkit.getPluginManager().getPlugin("mcMMO") != null;
	}

	public static CorePlugin getInstance() {
		return instance;
	}
}
