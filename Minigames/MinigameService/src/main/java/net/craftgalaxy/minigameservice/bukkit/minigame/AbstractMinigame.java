package net.craftgalaxy.minigameservice.bukkit.minigame;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.craftgalaxy.minigameservice.bukkit.BukkitService;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEndEvent;
import net.craftgalaxy.minigameservice.bukkit.runnable.CountdownRunnable;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public abstract class AbstractMinigame {

	private final BukkitService plugin;
	protected final String name;
	protected int gameKey;
	protected final Location lobby;
	protected MinigameStatus status;
	protected long startTimestamp;

	protected final Set<UUID> players = new HashSet<>();
	protected final Set<UUID> spectators = new ObjectOpenHashSet<>();
	protected final Random random = new Random();
	protected BukkitRunnable countdown;

	public AbstractMinigame(String name, int gameKey, Location lobby) {
		this.plugin = BukkitService.getInstance();
		this.name = name;
		this.gameKey = gameKey;
		this.lobby = lobby;
		this.startTimestamp = Long.MIN_VALUE;
		this.status = MinigameStatus.WAITING;
	}

	public boolean isWaiting() {
		return this.status.isWaiting();
	}

	public boolean isCountingDown() {
		return this.status.isCountingDown();
	}

	public boolean isInProgress() {
		return this.status.isInProgress();
	}

	public boolean isFinished() {
		return this.status.isFinished();
	}

	public String getName() {
		return this.name;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}

	public int getNumPlayers() {
		return this.players.size();
	}

	public int getGameKey() {
		return this.gameKey;
	}

	public boolean isSpectator(UUID uniqueId) {
		return this.spectators.contains(uniqueId);
	}

	public boolean isPlayer(UUID uniqueId) {
		return this.players.contains(uniqueId);
	}

	public void hideSpectator(@NotNull Player spectator) {
		for (UUID uniqueId : this.players) {
			Player other = Bukkit.getPlayer(uniqueId);
			if (other == null || this.isSpectator(uniqueId)) {
				continue;
			}

			other.hidePlayer(this.plugin, spectator);
		}
	}

	public void showSpectator(@NotNull Player spectator) {
		for (UUID uniqueId : this.players) {
			Player other = Bukkit.getPlayer(uniqueId);
			if (other == null || other.canSee(spectator)) {
				continue;
			}

			other.showPlayer(this.plugin, spectator);
		}
	}

	public void removePlayer(@NotNull Player player) {
		if (this.isWaiting()) {
			return;
		}

		this.players.remove(player.getUniqueId());
		if (this.spectators.remove(player.getUniqueId())) {
			PlayerUtil.unsetSpectator(player);
			player.teleportAsync(this.lobby).thenRun(() -> this.showSpectator(player));
		}
	}

	public void connectMessage(@NotNull Player player) {
		if (this.isSpectator(player.getUniqueId())) {
			return;
		}

		this.broadcast(ChatColor.GREEN + player.getName() + ChatColor.GRAY + " reconnected.");
	}

	public void disconnectMessage(@NotNull Player player) {
		if (this.isSpectator(player.getUniqueId())) {
			return;
		}

		this.broadcast(ChatColor.GREEN + player.getName() + ChatColor.GRAY + " disconnected.");
	}

	public void broadcastTitleAndEffect(@NotNull String message, @NotNull Effect effect) {
		for (UUID uniqueId : this.players) {
			Player player = Bukkit.getPlayer(uniqueId);
			if (player != null) {
				player.sendTitle(message, null, 5, 15, 5);
				player.playEffect(player.getLocation(), effect, effect.getData());
			}
		}
	}

	public void broadcast(@NotNull String message) {
		for (UUID uniqueId : this.players) {
			Player player = Bukkit.getPlayer(uniqueId);
			if (player != null) {
				player.sendMessage(message);
			}
		}
	}

	public void endMinigame(boolean urgently) {
		this.status = MinigameStatus.FINISHED;
		if (urgently) {
			this.endMinigame();
		} else {
			Bukkit.getScheduler().runTaskLater(this.plugin, (Runnable) this::endMinigame, 200);
		}
	}

	private void endMinigame() {
		this.endTeleport();
		this.deleteWorlds(true);
		Bukkit.getPluginManager().callEvent(new MinigameEndEvent(this, this.players));
	}

	public void startTeleport() {
		this.status = MinigameStatus.IN_PROGRESS;
	}

	public void endTeleport() {
		this.status = MinigameStatus.WAITING;
	}

	public void deleteWorlds(boolean urgently) {
		if (urgently) {
			try {
				this.deleteWorlds();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
				try {
					this.deleteWorlds();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}, (long) (20 * Math.ceil(1.5 * this.players.size())));
		}
	}

	public void startCountdown(@NotNull List<UUID> players) {
		this.status = MinigameStatus.COUNTING_DOWN;
		this.countdown = new CountdownRunnable(this);
		this.countdown.runTaskTimer(this.plugin, 20L, 20L);
		this.players.addAll(players);
	}

	public void cancelCountdown() {
		if (this.countdown == null || this.status != MinigameStatus.COUNTING_DOWN) {
			return;
		}

		this.status = MinigameStatus.WAITING;
		this.countdown.cancel();
		this.broadcastTitleAndEffect(ChatColor.RED + "CANCELLED!", Effect.CLICK2);
		this.spectators.clear();
		this.players.clear();
	}

	public void unload() {
		this.spectators.clear();
		this.players.clear();
		this.gameKey = Integer.MIN_VALUE;
		this.startTimestamp = Long.MIN_VALUE;
	}

	public abstract boolean createWorlds();
	public abstract boolean worldsLoaded();
	public abstract void deleteWorlds() throws IOException;
	public abstract void handleEvent(@NotNull Event e);
	public abstract void handleChatFormat(@NotNull AsyncPlayerChatEvent e);

	public enum MinigameStatus {
		WAITING,
		COUNTING_DOWN,
		IN_PROGRESS,
		FINISHED;

		MinigameStatus() {

		}

		public boolean isWaiting() {
			return this == MinigameStatus.WAITING;
		}

		public boolean isCountingDown() {
			return this == MinigameStatus.COUNTING_DOWN;
		}

		public boolean isInProgress() {
			return this == MinigameStatus.IN_PROGRESS;
		}

		public boolean isFinished() {
			return this == MinigameStatus.FINISHED;
		}
	}
}
