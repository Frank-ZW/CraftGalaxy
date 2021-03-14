package net.craftgalaxy.minigamecore.minigame;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.craftgalaxy.deathswap.minigame.DeathSwap;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.craftgalaxy.manhunt.minigame.Manhunt;
import net.craftgalaxy.minigamecore.MinigameCore;
import net.craftgalaxy.minigamecore.socket.CoreSocket;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEndEvent;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEvent;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameStartEvent;
import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.StringUtil;
import net.craftgalaxy.minigameservice.packet.impl.client.PacketPlayOutCreateMinigame;
import net.craftgalaxy.minigameservice.packet.impl.client.PacketPlayOutForceEnd;
import net.craftgalaxy.minigameservice.packet.impl.client.PacketPlayOutQueuePlayer;
import net.craftgalaxy.minigameservice.packet.impl.server.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

public class MinigameManager {

	private final MinigameCore plugin;
	private Map<UUID, BukkitTask> disconnections = new HashMap<>();
	private Set<UUID> queuedPlayers = new HashSet<>();
	private ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("MinigameCore Socket Thread").build());
	private BukkitTask scheduledForceEnd;
	private int maxPlayers;
	private CoreSocket socket;
	private AbstractMinigame minigame;
	private Future<Boolean> socketFuture;
	private static MinigameManager instance;

	public MinigameManager() {
		this.plugin = MinigameCore.getInstance();
		try {
			this.socket = new CoreSocket();
			this.socketFuture = this.executor.submit(this.socket, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void disable() {
		if (instance == null) {
			return;
		}

		instance.executor.shutdown();
		Bukkit.getScheduler().cancelTasks(instance.plugin);
		if (instance.minigame != null) {
			if (instance.minigame.isInProgress()) {
				instance.minigame.endMinigame(true);
			} else if (instance.minigame.isFinished()) {
				instance.minigame.endTeleport();
				instance.minigame.deleteWorlds(true);
			} else if (instance.minigame.worldsLoaded()) {
				instance.minigame.deleteWorlds(true);
			} else {
				instance.minigame.cancelCountdown();
			}
		}

		if (instance.socket != null) {
			try {
				instance.socket.sendPacket(new PacketPlayInRequestDisconnect(Bukkit.getOnlinePlayers()));
				if (instance.socketFuture.get(8, TimeUnit.SECONDS)) {
					Bukkit.getLogger().info(ChatColor.GREEN + "Successfully disconnected the socket for " + MinigameCore.BUNGEE_SERVER_NAME);
				}
			} catch (InterruptedException | ExecutionException | IOException | TimeoutException e) {
				Bukkit.getLogger().log(Level.SEVERE, "An error occurred while shutting down the established TCP socket connection. Contact the developer if this occurs.", e);
			}
		}

		instance.queuedPlayers.clear();
		instance.disconnections.clear();
		instance.executor.shutdownNow();
		try {
			if (instance.executor.awaitTermination(8, TimeUnit.SECONDS)) {
				Bukkit.getLogger().info(ChatColor.GREEN + "MinigameCore executor has been terminated.");
			}
		} catch (InterruptedException e) {
			Bukkit.getLogger().log(Level.SEVERE, "An interrupted exception occurred while terminating the MinigameCore executor", e);
		} finally {
			instance.maxPlayers = 0;
			instance.disconnections = null;
			instance.queuedPlayers = null;
			instance.executor = null;
			instance.scheduledForceEnd = null;
			instance.socket = null;
			instance.minigame = null;
			instance.socketFuture = null;
			instance = null;
		}
	}

	public static MinigameManager getInstance() {
		return instance == null ? instance = new MinigameManager() : instance;
	}

	public void teleportLobby(@NotNull Player player) {
		player.teleportAsync(this.plugin.getLobbyLocation()).thenAccept(result -> {
			if (!result) {
				player.sendMessage(ChatColor.RED + "Failed to teleport you back to the lobby. Contact an administrator if this occurs.");
			}
		});
	}

	public void sendBungeeLobby(@NotNull Player player) throws IOException {
		this.socket.sendPacket(new PacketPlayInPlayerConnect(player.getUniqueId()));
	}

	public void handlePacket(Object object) {
		if (object instanceof PacketPlayOutCreateMinigame) {
			PacketPlayOutCreateMinigame packet = (PacketPlayOutCreateMinigame) object;
			this.maxPlayers = packet.getMaxPlayers();
			switch (packet.getMinigameId()) {
				case 0:
					this.minigame = new Manhunt(packet.getGameKey(), this.plugin.getLobbyLocation());
					break;
				case 1:
					this.minigame = new DeathSwap(packet.getGameKey(), this.plugin.getLobbyLocation());
					break;
				case 2:
					this.minigame = new LockOut(packet.getGameKey(), this.plugin.getLobbyLocation());
					break;
				default:
					Bukkit.getLogger().warning("Received a request to create a new minigame with an id of " + packet.getMinigameId() + ". This id does not exist and has been ignored.");
					return;
			}

			Bukkit.getScheduler().runTask(this.plugin, () -> {
				if (!this.minigame.createWorlds()) {
					Bukkit.broadcastMessage(ChatColor.RED + "An error occurred while loading up the worlds for " + (this.minigame == null ? "Unknown" : this.minigame.getName()) + ". You have been teleported back to the lobby.");
					try {
						this.socket.sendPacket(new PacketPlayInPlayerConnect(this.queuedPlayers));
						this.socket.sendPacket(new PacketPlayInServerQueue(true));
						this.minigame.deleteWorlds();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						this.queuedPlayers.clear();
					}
				}
			});
		} else if (object instanceof PacketPlayOutQueuePlayer) {
			PacketPlayOutQueuePlayer packet = (PacketPlayOutQueuePlayer) object;
			this.queuedPlayers.add(packet.getUniqueId());
		} else if (object instanceof PacketPlayOutForceEnd) {
			Bukkit.getScheduler().runTask(this.plugin, this::handleForceEnd);
		}
	}

	public void handleForceEnd() {
		if (this.minigame == null || this.minigame.isFinished()) {
			return;
		}

		Bukkit.broadcastMessage(ChatColor.GREEN + "The " + this.minigame.getName() + " you were in was forcibly ended.");
		if (this.minigame.isInProgress()) {
			this.minigame.endMinigame(true);
		} else {
			if (this.minigame.isCountingDown()) {
				this.queuedPlayers.addAll(this.minigame.getPlayers());
				this.minigame.cancelCountdown();
			}

			if (this.queuedPlayers.isEmpty()) {
				return;
			}

			try {
				this.socket.sendPacket(new PacketPlayInEndTeleport(this.queuedPlayers));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void handleConnect(@NotNull Player player) throws IOException {
		if (this.minigame == null) {
			if (!player.hasPermission(StringUtil.BACKEND_SERVER_PERMISSION)) {
				this.sendBungeeLobby(player);
			}

			return;
		}

		if (this.minigame.isCountingDown() || this.minigame.isFinished()) {
			return;
		}

		if (this.minigame.isWaiting()) {
			if (this.queuedPlayers.contains(player.getUniqueId())) {
				if (player.isDead()) {
					player.spigot().respawn();
				}

				player.getInventory().clear();
				this.teleportLobby(player);
				if (player.getFireTicks() > 0) {
					player.setFireTicks(0);
				}

				if (this.queuedPlayers.size() >= this.maxPlayers) {
					try {
						this.socket.sendPacket(new PacketPlayInStartCountdown(this.queuedPlayers, this.minigame.getGameKey()));
						this.minigame.startCountdown(new ArrayList<>(this.queuedPlayers));
						this.queuedPlayers.clear();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				if (!player.hasPermission(StringUtil.BACKEND_SERVER_PERMISSION)) {
					this.sendBungeeLobby(player);
				}
			}
		} else {
			BukkitTask task = this.disconnections.remove(player.getUniqueId());
			if (task == null) {
				this.sendBungeeLobby(player);
			} else {
				task.cancel();
				this.minigame.connectMessage(player);
			}
		}
	}

	public void handleDisconnect(@NotNull Player player) throws IOException {
		if (this.minigame == null || this.minigame.isFinished()) {
			return;
		}

		if (this.minigame.isWaiting()) {
			if (!this.queuedPlayers.remove(player.getUniqueId())) {
				return;
			}

			if (this.queuedPlayers.isEmpty()) {
				this.socket.sendPacket(new PacketPlayInServerQueue(true));
				this.maxPlayers = 0;
				this.minigame.deleteWorlds(true);
				this.minigame.unload();
				this.minigame = null;
			} else {
				this.socket.sendPacket(new PacketPlayInUpdatePlayerCount(this.queuedPlayers.size(), this.maxPlayers));
			}
		} else if (this.minigame.isCountingDown()) {
			this.minigame.removePlayer(player);
			this.socket.sendPacket(new PacketPlayInUpdatePlayerCount(this.minigame.getNumPlayers(), this.maxPlayers));
			if (this.minigame.getNumPlayers() < this.maxPlayers) {
				this.queuedPlayers.addAll(this.minigame.getPlayers());
				this.minigame.cancelCountdown();
				this.socket.sendPacket(new PacketPlayInServerQueue(this.queuedPlayers.isEmpty()));
				if (this.queuedPlayers.isEmpty()) {
					this.maxPlayers = 0;
					this.minigame.deleteWorlds();
					this.minigame.unload();
					this.minigame = null;
				}
			}
		} else {
			if (this.minigame.isSpectator(player.getUniqueId())) {
				this.minigame.removePlayer(player);
				this.socket.sendPacket(new PacketPlayInPlayerLeave(player.getUniqueId()));
				this.socket.sendPacket(new PacketPlayInUpdatePlayerCount(this.minigame.getNumPlayers(), this.maxPlayers));
			} else {
				this.minigame.disconnectMessage(player);
				this.disconnections.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
					this.disconnections.remove(player.getUniqueId());
					this.minigame.removePlayer(player);
					try {
						this.socket.sendPacket(new PacketPlayInDisconnectRemove(player.getUniqueId()));
						this.socket.sendPacket(new PacketPlayInUpdatePlayerCount(this.minigame.getNumPlayers(), this.maxPlayers));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}, TimeUnit.MINUTES.toSeconds(3) * 20));
			}
		}
	}

	public void handleEvent(@NotNull Event e) {
		if (e instanceof MinigameEvent) {
			if (e instanceof MinigameStartEvent) {
				MinigameStartEvent event = (MinigameStartEvent) e;
				try {
					this.socket.sendPacket(new PacketPlayInStartTeleport(event.getPlayers()));
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					this.scheduledForceEnd = Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
						if (this.minigame != null && this.minigame.isInProgress()) {
							Bukkit.broadcastMessage(ChatColor.GREEN + "The " + this.minigame.getName() + " you were in was automatically ended because it exceeded the three hour time limit.");
							this.minigame.endMinigame(false);
						}
					}, TimeUnit.HOURS.toSeconds(3) * 20);
				}
			} else {
				MinigameEndEvent event = (MinigameEndEvent) e;
				try {
					this.socket.sendPacket(new PacketPlayInEndTeleport(event.getPlayers()));
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					if (this.scheduledForceEnd != null && !this.scheduledForceEnd.isCancelled()) {
						this.scheduledForceEnd.cancel();
						this.scheduledForceEnd = null;
					}

					for (UUID uniqueId : this.disconnections.keySet()) {
						BukkitTask task = this.disconnections.remove(uniqueId);
						if (task != null) {
							task.cancel();
						}
					}

					this.maxPlayers = 0;
					this.disconnections.clear();
					this.minigame.unload();
					this.minigame = null;
				}
			}
		} else {
			if (this.minigame == null) {
				return;
			}

			if (e instanceof AsyncPlayerChatEvent) {
				AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) e;
				this.minigame.handleChatFormat(event);
				Player player = event.getPlayer();
				Set<Player> recipients = new HashSet<>(event.getRecipients());
				Iterator<Player> iterator = recipients.iterator();
				while (true) {
					Player recipient;
					do {
						if (!iterator.hasNext()) {
							return;
						}

						recipient = iterator.next();
					} while (this.minigame.isPlayer(player.getUniqueId()) ? (this.minigame.isSpectator(player.getUniqueId()) ? this.minigame.isSpectator(recipient.getUniqueId()) : this.minigame.isPlayer(recipient.getUniqueId())) : !this.minigame.isPlayer(recipient.getUniqueId()));
					event.getRecipients().remove(recipient);
				}
			} else {
				this.minigame.handleEvent(e);
			}
		}
	}

	@Nullable
	public AbstractMinigame getMinigame() {
		return this.minigame;
	}
}
