package net.craftgalaxy.bungeecore.data.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.data.PlayerData;
import net.craftgalaxy.bungeecore.data.ServerSocketData;
import net.craftgalaxy.minigameservice.bungee.StringUtil;
import net.craftgalaxy.minigameservice.packet.impl.client.PacketPlayOutCreateMinigame;
import net.craftgalaxy.minigameservice.packet.impl.client.PacketPlayOutForceEnd;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerManager {

	private final BungeeCore plugin;
	private final AtomicInteger serverUniqueId = new AtomicInteger();
	private Map<String, ServerSocketData> lobbies = new HashMap<>();
	private Map<String, ServerSocketData> minigames = new HashMap<>();
	private Queue<ServerSocketData> inactives = new LinkedList<>();
	private Map<ServerSocketData.Minigames, Map<Integer, Set<ServerSocketData>>> queued = new HashMap<>();
	private Set<ServerSocketData> actives = new HashSet<>();
	private Set<ServerSocketData> confirmations = new HashSet<>();
	private BiMap<Integer, ServerSocketData> gameKeys = HashBiMap.create();
	private final Random random = new Random();
	private TaskScheduler scheduler;
	private ServerSocket serverSocket;
	private boolean finished;
	private static ServerManager instance;

	public ServerManager() {
		this.plugin = BungeeCore.getInstance();
		this.scheduler = this.plugin.getProxy().getScheduler();
		this.scheduler.runAsync(this.plugin, () -> {
			try {
				this.serverSocket = new ServerSocket(this.plugin.getPortNumber());
				while (!this.finished) {
					this.scheduler.runAsync(this.plugin, new ServerSocketData(this.serverSocket.accept(), this.serverUniqueId.incrementAndGet()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					this.serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void disable() {
		if (instance != null) {
			instance.finished = true;
			instance.scheduler.cancel(instance.plugin);
			instance.minigames.values().forEach(ServerSocketData::disconnect);
			instance.lobbies.values().forEach(ServerSocketData::disconnect);
			instance.lobbies.clear();
			instance.gameKeys.clear();
			instance.minigames.clear();
			instance.inactives.clear();
			instance.queued.clear();
			instance.actives.clear();
			instance.confirmations.clear();
			instance.scheduler = null;
			instance.lobbies = null;
			instance.minigames = null;
			instance.gameKeys = null;
			instance.inactives = null;
			instance.actives = null;
			instance.queued = null;
			instance.confirmations = null;
			instance = null;
		}
	}

	public static ServerManager getInstance() {
		return instance == null ? instance = new ServerManager() : instance;
	}

	/**
	 * @param serverName    The name of the server to be connected.
	 * @param serverData    The server instance to be connected.
	 */
	public void connectServer(@NotNull String serverName, @NotNull ServerSocketData serverData) {
		if (this.plugin.isSpecializedServer(serverName)) {
			return;
		}

		if (this.plugin.isLobbyServer(serverName)) {
			this.lobbies.put(serverName, serverData);
		} else {
			this.minigames.put(serverName, serverData);
			this.inactives.add(serverData);
		}

		this.plugin.getLogger().info(ChatColor.GREEN + "Established underlying TCP socket connection with " + serverName + ". This server can now communicate with the proxy.");
	}

	/**
	 * Removes a registered server from the plugin's cache, including those queued and active. This
	 * method should only be called if the back-end server is disabled.
	 *
	 * @param serverData    The server to be removed.
	 */
	public void disconnectServer(@NotNull ServerSocketData serverData) {
		this.lobbies.remove(serverData.getServerName());
		this.minigames.remove(serverData.getServerName());
		this.inactives.remove(serverData);
		this.confirmations.remove(serverData);
		Map<Integer, Set<ServerSocketData>> minigames = this.queued.remove(serverData.getMinigame());
		if (minigames == null) {
			return;
		}

		Set<ServerSocketData> players = minigames.get(serverData.getMaxPlayers());
		if (players == null) {
			return;
		}

		players.remove(serverData);
		minigames.put(serverData.getMaxPlayers(), players);
		this.queued.put(serverData.getMinigame(), minigames);
		this.plugin.removeServer(serverData.getServerName());
	}

	/**
	 * Removes the specified server from the minigame queue list. This method should only be called
	 * if an inactive server is in the queued server list.
	 *
	 * @param serverData    The server to be de-queued.
	 */
	public void removeFromQueued(@NotNull ServerSocketData serverData) {
		Map<Integer, Set<ServerSocketData>> specifiedMinigames = this.queued.get(serverData.getMinigame());
		if (specifiedMinigames != null) {
			Set<ServerSocketData> specifiedPlayers = specifiedMinigames.get(serverData.getMaxPlayers());
			if (specifiedPlayers != null && specifiedPlayers.remove(serverData)) {
				specifiedMinigames.put(serverData.getMaxPlayers(), specifiedPlayers);
				this.queued.put(serverData.getMinigame(), specifiedMinigames);
			}
		}
	}

	public void addInactiveServer(@NotNull ServerSocketData serverData) {
		if (this.actives.remove(serverData)) {
			this.plugin.getLogger().info(ChatColor.GREEN + "Re-added " + serverData.getServerName() + " to the inactive server queue.");
		} else {
			this.plugin.getLogger().warning("Mismatched server state for " + serverData.getServerName() + " while adding inactive server. This warning can be safely ignored.");
			this.confirmations.remove(serverData);
			this.removeFromQueued(serverData);
		}

		serverData.setStatus(ServerSocketData.ServerStatus.INACTIVE);
		this.gameKeys.inverse().remove(serverData);
		this.inactives.add(serverData.reset());
	}

	public void addActiveServer(@NotNull ServerSocketData serverData, int gameKey) {
		if (!this.confirmations.remove(serverData)) {
			this.plugin.getLogger().warning("Mismatched server state for " + serverData.getServerName() + " while adding active server. This warning can be safely ignored.");
			this.inactives.remove(serverData);
			this.gameKeys.inverse().remove(serverData);
			this.actives.remove(serverData);
			this.removeFromQueued(serverData);
		}

		serverData.setStatus(ServerSocketData.ServerStatus.COUNTING_DOWN);
		this.gameKeys.put(gameKey, serverData);
		this.actives.add(serverData);
		this.plugin.getLogger().info(ChatColor.GREEN + "Starting minigame on " + serverData.getServerName() + " with game key " + gameKey);
	}

	private void forceEnd(@NotNull ServerSocketData serverData) {
		try {
			serverData.sendPacket(new PacketPlayOutForceEnd());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param gameKey   The game key of the minigame.
	 * @return          True if the minigame was forcibly ended, false otherwise.
	 */
	public boolean forceEnd(int gameKey) {
		ServerSocketData serverData = this.gameKeys.remove(gameKey);
		if (serverData == null) {
			return false;
		}

		this.forceEnd(serverData);
		return true;
	}

	/**
	 * @param server    The minigame server to be forcibly ended.
	 * @return          True if the minigame was forcibly ended, false otherwise.
	 */
	public boolean forceEnd(@NotNull ServerInfo server) {
		ServerSocketData serverData = this.minigames.get(server.getName());
		if (serverData == null) {
			return false;
		}

		this.forceEnd(serverData);
		return true;
	}

	/**
	 * Checks if the minigame the player has entered exists. If the minigame does not exist
	 * or the maximum number of players entered exceeds the threshold, then the method returns
	 * INACTIVE and cancels the minigame queue request.
	 *
	 * @param sender        The player sending the command.
	 * @param name          The name of the minigame to queue.
	 * @param maxPlayers    The maximum number of players in the minigame.
	 * @return              The minigame enum if it exists and INACTIVE otherwise.
	 */
	public ServerSocketData.Minigames checkMinigame(@NotNull ProxiedPlayer sender, String name, int maxPlayers) {
		boolean valid;
		ServerSocketData.Minigames minigame;
		switch (name) {
			case "manhunt":
				minigame = ServerSocketData.Minigames.MANHUNT;
				valid = maxPlayers == 1 ? sender.hasPermission(StringUtil.SOLO_MANHUNT_COMMAND) : maxPlayers >= 2 && maxPlayers <= 5;
				break;
			case "deathswap":
			case "swap":
				minigame = ServerSocketData.Minigames.DEATH_SWAP;
				valid = maxPlayers >= 2 && maxPlayers <= 8;
				break;
			case "tbr":
			case "boatrace":
				minigame = ServerSocketData.Minigames.BOAT_RACE;
				valid = maxPlayers >= 1 && maxPlayers <= 4;
				break;
			case "lockout":
				minigame = ServerSocketData.Minigames.LOCK_OUT;
				valid = maxPlayers >= 1 && maxPlayers <= 5;
				break;
			default:
				sender.sendMessage(new TextComponent(ChatColor.RED + "That minigame has not been added to the server. To help the server grow, consider making a small donation through our webstore."));
				return ServerSocketData.Minigames.INACTIVE;
		}

		if (!valid) {
			sender.sendMessage(new TextComponent(ChatColor.RED + minigame.getDisplayName() + "s with " + maxPlayers + " are currently unsupported. To help the developer, consider making a small donation to the server."));
			return ServerSocketData.Minigames.INACTIVE;
		}

		return minigame;
	}

	public void queueServer(@NotNull ServerSocketData serverData, boolean reset) {
		if (reset) {
			this.actives.remove(serverData);
			this.gameKeys.inverse().remove(serverData);
			this.confirmations.remove(serverData);
			this.removeFromQueued(serverData);
			this.inactives.add(serverData.reset());
		} else {
			Map<Integer, Set<ServerSocketData>> minigames = this.queued.get(serverData.getMinigame());
			if (minigames == null) {
				minigames = new HashMap<>();
			}

			Set<ServerSocketData> servers = minigames.get(serverData.getMaxPlayers());
			if (servers == null) {
				servers = new HashSet<>();
			}

			servers.add(serverData);
			minigames.put(serverData.getMaxPlayers(), servers);
			this.queued.put(serverData.getMinigame(), minigames);
		}
	}

	public void queuePlayer(@NotNull PlayerData senderData, String name, int maxPlayers) {
		ProxiedPlayer sender = senderData.getPlayer();
		ServerSocketData.Minigames type = this.checkMinigame(sender, name, maxPlayers);
		if (type == ServerSocketData.Minigames.INACTIVE) {
			return;
		}

		ServerSocketData serverData;
		Map<Integer, Set<ServerSocketData>> minigames = this.queued.get(type);
		if (minigames == null) {
			minigames = new HashMap<>();
		}

		Set<ServerSocketData> servers = minigames.get(maxPlayers);
		if (servers == null) {
			servers = new HashSet<>();
		}

		if (servers.isEmpty()) {
			serverData = this.inactives.poll();
			if (serverData == null) {
				sender.sendMessage(new TextComponent(ChatColor.RED + "There are currently no available servers to host a " + type.getDisplayName() + " on. Please try again later."));
				return;
			}

			try {
				serverData.sendPacket(new PacketPlayOutCreateMinigame(type.ordinal(), this.plugin.getAndIncrementGameKey(), maxPlayers));
				serverData.setStatus(ServerSocketData.ServerStatus.QUEUED);
				serverData.setMinigame(type);
				serverData.setMaxPlayers(maxPlayers);
				serverData.setPlayers(0);
				servers.add(serverData);
			} catch (IOException e) {
				e.printStackTrace();
				sender.sendMessage(new TextComponent(ChatColor.RED + "An error occurred while queuing you for " + serverData.getServerName() + ". Contact an administrator urgently if this occurs."));
				return;
			}
		} else {
			serverData = Iterables.get(servers, this.random.nextInt(servers.size()));
		}

		if (serverData.incrementPlayers() >= maxPlayers) {
			servers.remove(serverData);
			this.confirmations.add(serverData);
		}

		try {
			serverData.sendQueuePlayer(sender);
			minigames.put(maxPlayers, servers);
			this.queued.put(type, minigames);
		} catch (IOException e) {
			e.printStackTrace();
			sender.sendMessage(new TextComponent(ChatColor.RED + "An error occurred while sending you to " + serverData.getServerName() + ". Contact an administrator urgently if this occurs."));
		}
	}
}
