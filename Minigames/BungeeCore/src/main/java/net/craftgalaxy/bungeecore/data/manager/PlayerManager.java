package net.craftgalaxy.bungeecore.data.manager;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.data.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerManager {

	private final BungeeCore plugin;
	private final Map<UUID, ServerInfo> disconnections = new HashMap<>();
	private final Map<UUID, PlayerData> players = new HashMap<>();
	private static PlayerManager instance;

	public PlayerManager(BungeeCore plugin) {
		this.plugin = plugin;
		this.plugin.getProxy().getPlayers().forEach(this::addPlayer);
	}

	public static void enable(BungeeCore plugin) {
		instance = new PlayerManager(plugin);
	}

	public static void disable() {
		if (instance != null) {
			Collection<ProxiedPlayer> proxiedPlayers = instance.plugin.getProxy().getPlayers();
			for (ProxiedPlayer player : proxiedPlayers) {
				instance.removePlayer(player);
			}

			instance.players.clear();
			instance.disconnections.clear();
			instance = null;
		}
	}

	public static PlayerManager getInstance() {
		return instance;
	}

	public void removeDisconnection(Collection<UUID> players) {
		this.disconnections.keySet().removeAll(players);
	}

	public void removeDisconnections(ServerInfo server) {
		this.disconnections.values().remove(server);
	}

	@Nullable
	public ServerInfo removeDisconnection(UUID uniqueId) {
		return this.disconnections.remove(uniqueId);
	}

	public void addPlayer(@NotNull ProxiedPlayer player) {
		this.players.put(player.getUniqueId(), new PlayerData(player));
	}

	public void removePlayer(@NotNull ProxiedPlayer player) {
		PlayerData playerData = this.players.remove(player.getUniqueId());
		if (playerData != null && playerData.isPlaying()) {
			ServerInfo server = player.getServer().getInfo();
			this.plugin.getLogger().info(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Player Manager" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + "Added " + player.getName() + " to the disconnection map for server " + server.getName());
			this.disconnections.put(player.getUniqueId(), player.getServer().getInfo());
		}
	}

	@Nullable
	public PlayerData getPlayerData(UUID uniqueId) {
		return this.players.get(uniqueId);
	}

	@Nullable
	public PlayerData getPlayerData(@NotNull ProxiedPlayer player) {
		return this.getPlayerData(player.getUniqueId());
	}
}
