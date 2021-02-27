package net.craftgalaxy.galaxycore.bukkit.socket.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.craftgalaxy.corepackets.server.SPacketDisconnectRequest;
import net.craftgalaxy.galaxycore.bukkit.CorePlugin;
import net.craftgalaxy.galaxycore.bukkit.socket.CoreSocket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SocketManager {

	private CorePlugin plugin;
	private CoreSocket socket;
	private Future<Boolean> future;
	private ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Core Executor").build());
	private static SocketManager instance;

	public SocketManager(CorePlugin plugin) {
		this.plugin = plugin;
		this.socket = new CoreSocket(plugin.getHostName(), plugin.getPortNumber());
		this.future = this.executor.submit(this.socket, true);
	}

	public static void enable(CorePlugin plugin) {
		instance = new SocketManager(plugin);
	}

	public static void disable() {
		if (instance == null) {
			return;
		}

		instance.executor.shutdown();
		if (instance.socket != null) {
			try {
				instance.socket.sendPacket(new SPacketDisconnectRequest(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet())));
				if (instance.future.get(5, TimeUnit.SECONDS)) {
					Bukkit.getLogger().info("Successfully interrupted underlying socket connection.");
				}
			} catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
				Bukkit.getLogger().log(Level.SEVERE, "An error occurred while shutting down the TCP socket connection for " + instance.plugin.getBungeecordName(), e);
			}
		}

		try {
			instance.executor.shutdownNow();
			if (instance.executor.awaitTermination(5, TimeUnit.SECONDS)) {
				Bukkit.getLogger().info("Cleanly shutdown all pending executor tasks for GalaxyCore");
			} else {
				Bukkit.getLogger().warning("One or more tasks were active while the executor was shutting down");
			}
		} catch (InterruptedException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to shut down one or more pending tasks", e);
		} finally {
			instance.executor = null;
			instance.socket = null;
			instance.future = null;
			instance.plugin = null;
			instance = null;
		}
	}

	public static SocketManager getInstance() {
		return instance;
	}
}
