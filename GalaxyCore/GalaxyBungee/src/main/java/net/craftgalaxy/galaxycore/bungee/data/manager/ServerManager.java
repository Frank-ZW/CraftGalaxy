package net.craftgalaxy.galaxycore.bungee.data.manager;

import net.craftgalaxy.corepackets.CoreClientboundPacket;
import net.craftgalaxy.galaxycore.bungee.BungeePlugin;
import net.craftgalaxy.galaxycore.bungee.data.ServerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerManager {

	private BungeePlugin plugin;
	private Map<String, ServerData> servers = new CaseInsensitiveMap<>();
	private TaskScheduler scheduler;
	private FutureTask<Boolean> futureTask;
	private ServerSocket socket;
	private boolean finished;
	private static ServerManager instance;

	public ServerManager(BungeePlugin plugin) {
		this.plugin = plugin;
		this.scheduler = plugin.getProxy().getScheduler();
		this.scheduler.runAsync(plugin, () -> {
			try {
				this.socket = new ServerSocket(BungeePlugin.SERVER_PORT_NUMBER);
				while (!this.finished) {
					this.scheduler.runAsync(plugin, new ServerData(this.socket.accept()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					this.socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void enable(BungeePlugin plugin) {
		instance = new ServerManager(plugin);
	}

	public static ServerManager getInstance() {
		return instance;
	}

	public static void disable() {
		if (instance == null) {
			return;
		}

		Runnable shutdown = () -> {
			try {
				for (ServerData serverData : instance.servers.values()) {
					serverData.forceDisconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};

		instance.futureTask = new FutureTask<>(shutdown, true);
		instance.scheduler.runAsync(instance.plugin, shutdown);
		instance.finished = true;
		try {
			if (instance.futureTask.get(8L, TimeUnit.SECONDS)) {
				instance.plugin.getLogger().info(ChatColor.GREEN + "Successfully cleared server data cache.");
			}

			instance.socket.close();
		} catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
			e.printStackTrace();
		} finally {
			instance.scheduler.cancel(instance.plugin);
			instance.servers.clear();
			instance.servers = null;
			instance.futureTask = null;
			instance.scheduler = null;
			instance.plugin = null;
			instance = null;
		}
	}

	public void connectFallback(@Nullable ProxiedPlayer player) {
		if (player == null) {
			return;
		}

		List<ServerInfo> fallbackServers = this.plugin.getFallbackServers();
		AtomicBoolean shouldBreak = new AtomicBoolean(false);
		for (ServerInfo server : fallbackServers) {
			server.ping((serverPing, e) -> {
				if (e == null) {
					player.connect(server);
					shouldBreak.set(true);
				} else {
					fallbackServers.remove(server);
				}
			});

			if (shouldBreak.get()) {
				break;
			}
		}
	}

	public void connectServer(String name, ServerData serverData) {
		this.servers.put(name, serverData);
	}

	public void disconnectServer(String name) {
		this.servers.remove(name);
	}

	public boolean sendPacketToServer(String serverName, CoreClientboundPacket packet) {
		ServerData serverData = this.servers.get(serverName);
		if (serverData == null) {
			return false;
		}

		try {
			serverData.sendPacket(packet);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
