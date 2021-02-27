package net.craftgalaxy.bungeecore.data.manager;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.data.PlayerData;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {


	private final BungeeCore plugin;
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
			instance = null;
		}
	}

	public static PlayerManager getInstance() {
		return instance;
	}

	public void addPlayer(@NotNull ProxiedPlayer player) {
		this.players.put(player.getUniqueId(), new PlayerData(player));
	}

	public void removePlayer(@NotNull ProxiedPlayer player) {
		this.removePlayer(player.getUniqueId());
	}

	public void removePlayer(@NotNull UUID uniqueId) {
		this.players.remove(uniqueId);
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
