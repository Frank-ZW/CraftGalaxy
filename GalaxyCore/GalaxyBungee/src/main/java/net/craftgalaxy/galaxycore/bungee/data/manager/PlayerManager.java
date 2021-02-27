package net.craftgalaxy.galaxycore.bungee.data.manager;

import net.craftgalaxy.galaxycore.bungee.BungeePlugin;
import net.craftgalaxy.galaxycore.bungee.data.PlayerData;
import net.craftgalaxy.galaxycore.bungee.database.DatabaseManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class PlayerManager {

	private BungeePlugin plugin;
	private Map<UUID, PlayerData> players = new HashMap<>();
	private static PlayerManager instance;

	public PlayerManager(BungeePlugin plugin) {
		this.plugin = plugin;
	}

	public static void enable(BungeePlugin plugin) {
		instance = new PlayerManager(plugin);
	}

	public static void disable() {
		if (instance == null) {
			return;
		}

		Runnable shutdown = () -> {
			for (PlayerData playerData : instance.players.values()) {
				instance.removePlayer(playerData.getPlayer(), false);
			}
		};
		FutureTask<Boolean> future = new FutureTask<>(shutdown, true);
		instance.plugin.getProxy().getScheduler().runAsync(instance.plugin, shutdown);
		try {
			if (future.get(8, TimeUnit.SECONDS)) {
				instance.plugin.getLogger().info(ChatColor.GREEN + "Removed all player data cache and saved to local database.");
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			instance.plugin.getLogger().log(Level.SEVERE, "Failed to save all player data to the local database", e);
		} finally {
			instance.players.clear();
			instance.players = null;
			instance.plugin = null;
			instance = null;
		}
	}

	public static PlayerManager getInstance() {
		return instance;
	}

	public void addPlayer(@NotNull PendingConnection connection) {
		this.players.put(connection.getUniqueId(), DatabaseManager.getInstance().fetchPlayerData(connection.getName(), connection.getUniqueId(), connection.getVirtualHost()));
	}

	public void onPlayerConnect(@NotNull ProxiedPlayer player) {
		PlayerData playerData = this.players.get(player.getUniqueId());
		if (playerData != null) {
			playerData.setPlayer(player);
			playerData.onPlayerConnect();
		}
	}

	public void removePlayer(@NotNull ProxiedPlayer player, boolean runAsync) {
		if (runAsync) {
			this.plugin.getProxy().getScheduler().runAsync(this.plugin, () -> this.removePlayer(player));
		} else {
			this.removePlayer(player);
		}
	}

	private void removePlayer(@NotNull ProxiedPlayer player) {
		PlayerData playerData = this.players.remove(player.getUniqueId());
		if (playerData != null) {
			DatabaseManager.getInstance().writePlayerData(playerData);
		}
	}

	@Nullable
	public PlayerData getPlayerData(UUID uniqueId) {
		return this.players.get(uniqueId);
	}

	public Collection<PlayerData> getPlayers() {
		return this.players.values();
	}
}
