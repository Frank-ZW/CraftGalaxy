package net.craftgalaxy.mavic.data.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.craftgalaxy.mavic.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerManager {

	private final Map<UUID, PlayerData> players = new ConcurrentHashMap<>();
	private final ExecutorService executor = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("Mavic Player Thread").build());
	private final ExecutorService tickUpdater = Executors.newFixedThreadPool(2, new ThreadFactoryBuilder().setNameFormat("Mavic Tick Updater Thread").build());
	private static PlayerManager instance;

	public PlayerManager() {
		this.executor.execute(() -> Bukkit.getOnlinePlayers().forEach(this::addPlayer));
	}

	public static void enable() {
		instance = new PlayerManager();
	}

	public static void disable() {
		Bukkit.getOnlinePlayers().forEach(instance::removePlayer);
		instance.executor.shutdown();
		instance = null;
	}

	public static PlayerManager getInstance() {
		return instance;
	}

	public void addPlayer(Player player) {
		this.players.put(player.getUniqueId(), new PlayerData(player));
	}

	public void removePlayer(Player player) {
		this.players.remove(player.getUniqueId());
	}

	@Nullable
	public PlayerData getPlayerData(UUID uuid) {
		return this.players.get(uuid);
	}

	@Nullable
	public PlayerData getPlayerData(Player player) {
		return this.getPlayerData(player.getUniqueId());
	}

	public void execute(Runnable runnable) {
		this.executor.execute(runnable);
	}

	public void updatePlayerTicks() {
		this.tickUpdater.execute(() -> this.players.values().forEach(PlayerData::handleTickUpdate));
	}
}
