package net.craftgalaxy.bungeecore.data;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.data.manager.PlayerManager;
import net.craftgalaxy.bungeecore.data.manager.ServerManager;
import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayOut;
import net.craftgalaxy.minigameservice.packet.impl.client.PacketPlayOutConfirmDisconnect;
import net.craftgalaxy.minigameservice.packet.impl.client.PacketPlayOutQueuePlayer;
import net.craftgalaxy.minigameservice.packet.impl.server.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

public class ServerSocketData implements Runnable {

	private final BungeeCore plugin;
	private final Socket socket;
	private final int id;
	private ServerInfo server;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private int players;
	private int maxPlayers;
	private Minigames minigame;
	private ServerStatus status;

	public ServerSocketData(Socket socket, int id) {
		this.plugin = BungeeCore.getInstance();
		this.socket = socket;
		this.id = id;
		this.minigame = Minigames.INACTIVE;
		this.status = ServerStatus.INACTIVE;
	}

	public void connect(@NotNull String serverName) {
		this.server = this.plugin.getProxy().getServerInfo(serverName);
		if (this.server == null) {
			this.plugin.getLogger().warning("A server with the name " + serverName + " failed to connect with the Proxy. Please double check the 'bungeecord' setting in the Spigot.yml file is enabled and the name of the server matches the name entered for Bungee.");
		} else {
			ServerManager.getInstance().connectServer(serverName, this);
		}
	}

	public void disconnect() {
		ServerManager.getInstance().disconnectServer(this);
		this.plugin.getLogger().info(ChatColor.GREEN + "Successfully interrupted TCP socket connection for " + this.server.getName() + ". This server will no longer receive minigame requests from the Proxy.");
	}

	public Minigames getMinigame() {
		return this.minigame;
	}

	public void setMinigame(Minigames minigame) {
		this.minigame = minigame;
	}

	public void setStatus(ServerStatus status) {
		this.status = status;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public int getPlayers() {
		return this.players;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	public int incrementPlayers() {
		return ++this.players;
	}

	public String getServerName() {
		return this.server == null ? "Invalid Server" : this.server.getName();
	}

	public void sendServer(@NotNull ProxiedPlayer sender) {
		sender.connect(this.server);
	}

	public ServerSocketData reset() {
		this.minigame = Minigames.INACTIVE;
		this.status = ServerStatus.INACTIVE;
		this.players = 0;
		this.maxPlayers = 0;
		return this;
	}

	public void sendQueuePlayer(@NotNull ProxiedPlayer sender) throws IOException {
		PlayerData senderData = PlayerManager.getInstance().getPlayerData(sender);
		if (senderData != null) {
			this.sendPacket(new PacketPlayOutQueuePlayer(sender.getUniqueId()));
			this.sendServer(sender);
		}
	}

	public void sendPacket(@NotNull MinigamePacketPlayOut packet) throws IOException {
		this.output.writeObject(packet);
		this.output.flush();
	}

	public int getId() {
		return this.id;
	}

	@SuppressWarnings("InfiniteLoopStatement")
	@Override
	public void run() {
		try {
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.input = new ObjectInputStream(this.socket.getInputStream());
			while (true) {
				Object object = this.input.readObject();
				if (object instanceof PacketPlayInServerConnect) {
					PacketPlayInServerConnect packet = (PacketPlayInServerConnect) object;
					this.connect(packet.getName());
				} else if (object instanceof PacketPlayInServerDisconnect) {
					PacketPlayInServerDisconnect packet = (PacketPlayInServerDisconnect) object;
					for (UUID uniqueId : packet.getPlayers()) {
						ProxiedPlayer player = this.plugin.getProxy().getPlayer(uniqueId);
						if (player != null) {
							player.connect(this.plugin.getMainLobby());
							player.sendMessage(new TextComponent(ChatColor.GREEN + "The server you were on abruptly went down. You have been teleported back to the main lobby."));
						}
					}

					this.disconnect();
				} else if (object instanceof PacketPlayInStartCountdown) {
					PacketPlayInStartCountdown packet = (PacketPlayInStartCountdown) object;
					ServerManager.getInstance().addActiveServer(this, packet.getGameKey());
				} else if (object instanceof PacketPlayInStartTeleport) {
					PacketPlayInStartTeleport packet = (PacketPlayInStartTeleport) object;
					for (UUID uniqueId : packet.getPlayers()) {
						PlayerData playerData = PlayerManager.getInstance().getPlayerData(uniqueId);
						if (playerData != null) {
							playerData.setPlayerStatus(PlayerData.PlayerStatus.PLAYING);
						}
					}

					this.status = ServerStatus.IN_PROGRESS;
				} else if (object instanceof PacketPlayInEndTeleport) {
					PacketPlayInEndTeleport packet = (PacketPlayInEndTeleport) object;
					Set<UUID> players = packet.getPlayers();
					for (UUID uniqueId : players) {
						PlayerData playerData = PlayerManager.getInstance().getPlayerData(uniqueId);
						if (playerData != null) {
							playerData.getPlayer().connect(this.plugin.getMinigameLobby());
							playerData.setPlayerStatus(PlayerData.PlayerStatus.INACTIVE);
						}
					}

					PlayerManager.getInstance().removeDisconnection(players);
					ServerManager.getInstance().addInactiveServer(this);
				} else if (object instanceof PacketPlayInPlayerConnect) {
					PacketPlayInPlayerConnect packet = (PacketPlayInPlayerConnect) object;
					for (UUID uniqueId : packet.getPlayers()) {
						ProxiedPlayer player = this.plugin.getProxy().getPlayer(uniqueId);
						if (player != null) {
							player.connect(this.plugin.getMinigameLobby());
						}
					}
				} else if (object instanceof PacketPlayInPlayerLeave) {
					PacketPlayInPlayerLeave packet = (PacketPlayInPlayerLeave) object;
					PlayerData playerData = PlayerManager.getInstance().getPlayerData(packet.getPlayer());
					if (playerData != null) {
						playerData.getPlayer().connect(this.plugin.getMinigameLobby());
						playerData.setPlayerStatus(PlayerData.PlayerStatus.INACTIVE);
					}
				} else if (object instanceof PacketPlayInServerQueue) {
					PacketPlayInServerQueue packet = (PacketPlayInServerQueue) object;
					ServerManager.getInstance().queueServer(this, packet.isReset());
					if (packet.isReset()) {
						this.plugin.getLogger().info(ChatColor.GREEN + this.getServerName() + " has been re-added to the inactive queue.");
					} else {
						this.plugin.getLogger().info(ChatColor.GREEN + this.getServerName() + " has been re-added to the minigame queue.");
					}
				} else if (object instanceof PacketPlayInDecrementPlayerCount) {
					this.players--;
				} else if (object instanceof PacketPlayInRequestDisconnect) {
					PacketPlayInRequestDisconnect packet = (PacketPlayInRequestDisconnect) object;
					for (UUID uniqueId : packet.getPlayers()) {
						ProxiedPlayer player = this.plugin.getProxy().getPlayer(uniqueId);
						if (player == null) {
							continue;
						}

						player.connect(this.plugin.getMinigameLobby());
						player.sendMessage(new TextComponent(ChatColor.GREEN + "The server you were on has unexpectedly shutdown. You have been teleported back to the minigame lobby."));
						PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
						if (playerData != null) {
							playerData.setPlayerStatus(PlayerData.PlayerStatus.INACTIVE);
						}
					}

					ServerManager.getInstance().disconnectServer(this);
					this.sendPacket(new PacketPlayOutConfirmDisconnect());
				} else if (object instanceof PacketPlayInDispatchCommand) {
					PacketPlayInDispatchCommand packet = (PacketPlayInDispatchCommand) object;
					ProxiedPlayer player = this.plugin.getProxy().getPlayer(packet.getPlayer());
					if (player != null) {
						this.plugin.getProxy().getPluginManager().dispatchCommand(player, "play " + packet.getMinigame() + "_" + packet.getMaxPlayers());
					} else {
						this.plugin.getLogger().warning("Failed to retrieve a player in the proxy with the name " + packet.getPlayer());
					}
				} else if (object instanceof PacketPlayInUpdatePlayerCount) {
					PacketPlayInUpdatePlayerCount packet = (PacketPlayInUpdatePlayerCount) object;
					this.players = packet.getPlayers();
					this.maxPlayers = packet.getMaxPlayers();
				} else if (object instanceof PacketPlayInUpdatePlayerStatus) {
					PacketPlayInUpdatePlayerStatus packet = (PacketPlayInUpdatePlayerStatus) object;
					PlayerData playerData = PlayerManager.getInstance().getPlayerData(packet.getUniqueId());
					if (playerData != null) {
						playerData.setPlayerStatus(PlayerData.PlayerStatus.values()[packet.getStatus() % 4]);
					}
				} else if (object instanceof PacketPlayInDisconnectRemove) {
					PacketPlayInDisconnectRemove packet = (PacketPlayInDisconnectRemove) object;
					PlayerManager.getInstance().removeDisconnection(packet.getUniqueId());
				} else {
					this.plugin.getLogger().warning("Received unknown Minigame packet with " + object.getClass().getName() + ". This warning can most likely be safely ignored.");
				}
			}
		} catch (Exception e) {
			if (e instanceof EOFException) {
				this.disconnect();
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				this.output.close();
				this.input.close();
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public enum ServerStatus {
		INACTIVE,
		QUEUED,
		COUNTING_DOWN,
		IN_PROGRESS;

		ServerStatus() {

		}
	}

	public enum Minigames {

		MANHUNT("Manhunt"),
		DEATH_SWAP("Death Swap"),
		BOAT_RACE("Boat Race"),
		INACTIVE("Unknown");

		private final String displayName;

		Minigames(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return this.displayName;
		}
	}

	@Override
	public int hashCode() {
		return 43 * this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof ServerSocketData)) {
			return false;
		}

		ServerSocketData o = (ServerSocketData) obj;
		return this.id == o.getId();
	}
}
