package net.craftgalaxy.minigamecore.minigame;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.craftgalaxy.deathswap.minigame.DeathSwap;
import net.craftgalaxy.lockout.minigame.LockOut;
import net.craftgalaxy.manhunt.minigame.impl.SurvivalManhunt;
import net.craftgalaxy.manhunt.minigame.impl.VanillaManhunt;
import net.craftgalaxy.minigamecore.MinigameCore;
import net.craftgalaxy.minigamecore.runnable.SocketConnectionRunnable;
import net.craftgalaxy.minigamecore.socket.MinigameSocket;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEndEvent;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEvent;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameStartEvent;
import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import net.craftgalaxy.minigameservice.bukkit.util.java.StringUtil;
import net.craftgalaxy.minigameservice.packet.impl.client.*;
import net.craftgalaxy.minigameservice.packet.impl.server.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MinigameManager {

	private final MinigameCore plugin;
	private Map<UUID, BukkitTask> disconnections = new HashMap<>();
	private Set<UUID> queuedPlayers = new HashSet<>();
	private Map<UUID, UUID> queuedSpectators = new HashMap<>();
	private ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("MinigameCore Socket Thread").build());
	private BukkitTask scheduledForceEnd;
	private int maxPlayers;
	private MinigameSocket socket;
	private AbstractMinigame minigame;
	private Future<Boolean> future;
	private static MinigameManager instance;

	public MinigameManager(MinigameCore plugin) {
		this.plugin = plugin;
		BukkitRunnable socketScheduler = new SocketConnectionRunnable(this);
		socketScheduler.runTaskTimer(plugin, 0, 200);
	}

	public static void enable(MinigameCore plugin) {
		instance = new MinigameManager(plugin);
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

		if (instance.socket == null) {
			Bukkit.getLogger().warning("Ignoring socket since no connection was forwarded to the proxy.");
		} else {
			try {
				instance.socket.sendPacket(new PacketPlayInRequestDisconnect(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet())));
				if (instance.future.get(8, TimeUnit.SECONDS)) {
					Bukkit.getLogger().info(ChatColor.GREEN + "Successful disconnect from the proxy.");
				}
			} catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
				Bukkit.getLogger().log(Level.SEVERE, "An error occurred while shutting down the established TCP socket connection. Contact the developer if this occurs.", e);
			}
		}

		instance.queuedPlayers.clear();
		instance.queuedSpectators.clear();
		instance.disconnections.clear();
		instance.executor.shutdownNow();
		try {
			if (instance.executor.awaitTermination(8, TimeUnit.SECONDS)) {
				Bukkit.getLogger().info(ChatColor.GREEN + "Core executor service successfully terminated.");
			}
		} catch (InterruptedException e) {
			Bukkit.getLogger().log(Level.SEVERE, "An interrupted exception occurred while terminating the MinigameCore executor", e);
		} finally {
			instance.maxPlayers = 0;
			instance.disconnections = null;
			instance.queuedPlayers = null;
			instance.queuedSpectators = null;
			instance.executor = null;
			instance.scheduledForceEnd = null;
			instance.socket = null;
			instance.minigame = null;
			instance.future = null;
			instance = null;
		}
	}

	public static MinigameManager getInstance() {
		return instance;
	}

	public boolean attemptSocketConnection() throws IOException {
		this.socket = new MinigameSocket();
		this.future = this.executor.submit(this.socket, true);
		return this.socket.isConnected();
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
					this.minigame = new VanillaManhunt(packet.getGameKey(), this.plugin.getLobbyLocation());
					break;
				case 1:
					this.minigame = new DeathSwap(packet.getGameKey(), this.plugin.getLobbyLocation());
					break;
				case 2:
					this.minigame = new LockOut(packet.getGameKey(), this.plugin.getLobbyLocation());
					break;
				case 3:
					this.minigame = new SurvivalManhunt(packet.getGameKey(), this.plugin.getLobbyLocation());
					break;
				default:
					Bukkit.getLogger().warning("Received a request to create a new mini-game with an id of " + packet.getMinigameId() + ". This id does not exist and has been ignored.");
			}
		} else if (object instanceof PacketPlayOutQueuePlayer) {
			PacketPlayOutQueuePlayer packet = (PacketPlayOutQueuePlayer) object;
			this.queuedPlayers.add(packet.getUniqueId());
		} else if (object instanceof PacketPlayOutForceEnd) {
			Bukkit.getScheduler().runTask(this.plugin, this::handleForceEnd);
		} else if (object instanceof PacketPlayOutPromptDisconnect) {
			PacketPlayOutPromptDisconnect packet = (PacketPlayOutPromptDisconnect) object;
			Bukkit.getScheduler().runTask(this.plugin, () -> {
				if (packet.isShutdown()) {
					Bukkit.getServer().shutdown();
				} else {
					Bukkit.getPluginManager().disablePlugin(this.plugin);
				}
			});
		} else if (object instanceof PacketPlayOutQueueSpectator) {
			PacketPlayOutQueueSpectator packet = (PacketPlayOutQueueSpectator) object;
			this.queuedSpectators.put(packet.getSpectator(), packet.getPlayer());
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
				this.socket.sendPacket(new PacketPlayInEndMinigameConnect(this.queuedPlayers));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void handleConnect(@NotNull Player player) throws IOException {
		if (player.isDead()) {
			player.spigot().respawn();
		}

		if (this.minigame == null) {
			if (!player.hasPermission(StringUtil.BACKEND_SERVER_PERMISSION + MinigameCore.BUNGEE_SERVER_NAME)) {
				this.sendBungeeLobby(player);
			}

			return;
		}

		if (this.minigame.isCountingDown()) {
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
						} else {
							this.socket.sendPacket(new PacketPlayInStartCountdown(this.queuedPlayers, this.minigame.getGameKey()));
							this.minigame.startCountdown(new ArrayList<>(this.queuedPlayers));
							this.queuedPlayers.clear();
						}
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
			UUID spectatedUuid = this.queuedSpectators.remove(player.getUniqueId());
			if (spectatedUuid != null) {
				Player spectated = Bukkit.getPlayer(spectatedUuid);
				if (spectated == null) {
					this.sendBungeeLobby(player);
					return;
				}

				player.teleportAsync(spectated.getLocation()).thenAccept(result -> {
					if (result) {
						this.minigame.hideSpectator(player);
						PlayerUtil.setSpectator(player);
						player.sendMessage(ChatColor.RED + "You are now spectating " + spectated.getName());
					} else {
						player.sendMessage(ChatColor.RED + "Failed to teleport you to " + spectated.getName() + ". Contact an administrator if this occurs.");
					}
				});
			}

			BukkitTask task = this.disconnections.remove(player.getUniqueId());
			if (task == null) {
				if (!player.hasPermission(StringUtil.BACKEND_SERVER_PERMISSION)) {
					this.sendBungeeLobby(player);
				}
			} else {
				task.cancel();
				if (!this.minigame.isSpectator(player.getUniqueId())) {
					Bukkit.broadcastMessage(this.minigame.getFormattedDisplayName(player) + ChatColor.GRAY + " reconnected.");
				}
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
				Bukkit.broadcastMessage(this.minigame.getFormattedDisplayName(player) + ChatColor.GRAY + " disconnected.");
				this.disconnections.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
					this.disconnections.remove(player.getUniqueId());
					try {
						this.socket.sendPacket(new PacketPlayInDisconnectRemove(player.getUniqueId()));
						this.socket.sendPacket(new PacketPlayInUpdatePlayerCount(this.minigame.getNumPlayers() - 1, this.maxPlayers));
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						this.minigame.removePlayer(player);
					}
				}, TimeUnit.MINUTES.toSeconds(3) * 20));
			}
		}
	}

	public void handleEvent(@NotNull Event e, @Nullable UUID uniqueId) {
		if (e instanceof MinigameEvent) {
			if (e instanceof MinigameStartEvent) {
				MinigameStartEvent event = (MinigameStartEvent) e;
				try {
					this.socket.sendPacket(new PacketPlayInUpdatePlayerStatus(event.getPlayers(), (byte) 0));
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					long timestamp = this.minigame.getScheduledForceEndTimestamp();
					if (timestamp > 0) {
						this.scheduledForceEnd = Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
							if (this.minigame != null && this.minigame.getGameKey() == event.getGameKey()) {
								Bukkit.broadcastMessage(ChatColor.GREEN + "The " + this.minigame.getName() + " you were in was automatically ended because it exceeded the maximum time limit.");
								this.minigame.endMinigame(false);
							}
						}, 20 * timestamp);
					}
				}
			} else {
				MinigameEndEvent event = (MinigameEndEvent) e;
				Bukkit.getLogger().info(ChatColor.RED + "Ending minigame for " + event.getMinigame().getName() + " with game key " + event.getGameKey());
				try {
					this.socket.sendPacket(new PacketPlayInEndMinigameConnect(event.getPlayers()));
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					if (this.scheduledForceEnd != null) {
						this.scheduledForceEnd.cancel();
						this.scheduledForceEnd = null;
					}

					Iterator<Map.Entry<UUID, BukkitTask>> iterator = this.disconnections.entrySet().iterator();
					while (iterator.hasNext()) {
						iterator.next().getValue().cancel();
						iterator.remove();
					}

					this.maxPlayers = 0;
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
				Player player = event.getPlayer();
				if (this.minigame.isInProgress() || this.minigame.isFinished()) {
					if (this.minigame.isSpectator(uniqueId)) {
						event.setFormat(StringUtil.SPECTATOR_PREFIX + ChatColor.RESET + this.minigame.getSpectatorFormat(player) + ChatColor.DARK_GRAY + ChatColor.BOLD + " » " + ChatColor.RESET + ChatColor.WHITE + event.getMessage());
					} else {
						event.setFormat(StringUtil.MINIGAME_PREFIX + ChatColor.RESET + this.minigame.getPlayerPrefix(player) + ChatColor.DARK_GRAY + ChatColor.BOLD + " » " + ChatColor.RESET + ChatColor.WHITE + event.getMessage());
					}
				} else {
					event.setFormat(StringUtil.LOBBY_PREFIX + ChatColor.RESET + event.getFormat());
				}

				Iterator<Player> iterator = event.getRecipients().iterator();
				while (true) {
					Player recipient;
					do {
						if (!iterator.hasNext()) {
							return;
						}

						recipient = iterator.next();
					} while (this.minigame.isPlayer(uniqueId) ? (this.minigame.isSpectator(uniqueId) ? this.minigame.isSpectator(recipient.getUniqueId()) : this.minigame.isPlayer(recipient.getUniqueId())) : !this.minigame.isPlayer(recipient.getUniqueId()));
					iterator.remove();
				}
			} else {
				if (this.minigame.isSpectator(uniqueId)) {
					this.minigame.handleSpectatorEvent(e);
				} else {
					this.minigame.handlePlayerEvent(e);
				}
			}
		}
	}

	@Nullable
	public AbstractMinigame getMinigame() {
		return this.minigame;
	}
}
