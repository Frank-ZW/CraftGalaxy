package net.craftgalaxy.bungeecore.data.manager;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.data.PlayerData;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerManager {

	private final BungeeCore plugin;
	private final Set<UUID> disconnections = new HashSet<>();
//	private final Map<UUID, DisconnectData> disconnections = new HashMap<>();
	private final Map<UUID, PlayerData> players = new HashMap<>();
	private final TaskScheduler scheduler;
	private static PlayerManager instance;

	public PlayerManager(BungeeCore plugin) {
		this.plugin = plugin;
		this.scheduler = this.plugin.getProxy().getScheduler();
		this.scheduler.runAsync(this.plugin, () -> this.plugin.getProxy().getPlayers().forEach(this::addPlayer));
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
		this.disconnections.removeAll(players);
	}

	public void removeDisconnection(UUID uniqueId) {
		this.disconnections.remove(uniqueId);
	}

	public void addPlayer(@NotNull ProxiedPlayer player) {
		PlayerData playerData;
		if (this.disconnections.remove(player.getUniqueId())) {
			playerData = new PlayerData(player, PlayerData.PlayerStatus.PLAYING);
		} else {
			player.connect(this.plugin.getMainLobby());
			playerData = new PlayerData(player);
		}

		this.players.put(player.getUniqueId(), playerData);
	}

	public void removePlayer(@NotNull ProxiedPlayer player) {
		PlayerData playerData = this.players.remove(player.getUniqueId());
		if (playerData != null && playerData.isPlaying()) {
			ServerInfo server = player.getServer().getInfo();
			if (this.plugin.isMinigameServer(server.getName())) {
				this.disconnections.add(player.getUniqueId());
			}
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

	public void executor(Runnable runnable) {
		this.scheduler.runAsync(this.plugin, runnable);
	}
}
