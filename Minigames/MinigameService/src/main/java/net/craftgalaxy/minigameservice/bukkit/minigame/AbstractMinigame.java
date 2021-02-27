package net.craftgalaxy.minigameservice.bukkit.minigame;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.craftgalaxy.minigameservice.bukkit.BukkitService;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEndEvent;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import net.craftgalaxy.minigameservice.bukkit.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class AbstractMinigame {

	private final BukkitService plugin;
	protected final String name;
	protected int gameKey;
	protected final Location lobby;
	protected boolean waiting;
	protected boolean countingDown;
	protected boolean inProgress;
	protected boolean finished;
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
		this.waiting = true;
	}

	public void setWaiting() {
		this.waiting = true;
		this.countingDown = false;
		this.inProgress = false;
		this.finished = false;
	}

	public void setCountingDown() {
		this.waiting = false;
		this.countingDown = true;
		this.inProgress = false;
		this.finished = false;
	}

	public void setInProgress() {
		this.waiting = false;
		this.countingDown = false;
		this.inProgress = true;
		this.finished = false;
	}

	public void setFinished() {
		this.waiting = false;
		this.countingDown = false;
		this.inProgress = false;
		this.finished = true;
	}

	public boolean isWaiting() {
		return this.waiting;
	}

	public boolean isCountingDown() {
		return this.countingDown;
	}

	public boolean isInProgress() {
		return this.inProgress;
	}

	public boolean isFinished() {
		return this.finished;
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
			if (other == null || this.isSpectator(uniqueId)) {
				continue;
			}

			other.showPlayer(this.plugin, spectator);
		}
	}

	/**
	 * Removes a player from the minigame with a given delay. If the duration
	 * is scheduled to be non-positive, the server immediately removes the player.
	 *
	 * @param player    The player to be removed from the minigame.
	 * @param unit      The unit of time for the method to be delayed.
	 * @param duration  The duration of time for the method to be delayed.
	 */
	public void removePlayer(@NotNull Player player, TimeUnit unit, int duration) {
		if (duration <= 0) {
			this.removePlayer(player);
		} else {
			Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.removePlayer(player), unit.toSeconds(duration) * 20);
		}
	}

	/**
	 * Immediately removes a player from the minigame.
	 *
	 * @param player    The player to be removed from the minigame.
	 */
	public void removePlayer(@NotNull Player player) {
		if (this.spectators.remove(player.getUniqueId())) {
			PlayerUtil.unsetSpectator(player);
			player.teleportAsync(this.lobby).thenAccept(result -> {
				this.showSpectator(player);
				if (!result) {
					player.sendMessage(ChatColor.RED + "Failed to teleport you back to the lobby. Contact an administrator if this occurs.");
				}
			});
		}

		this.players.remove(player.getUniqueId());
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

	public void broadcast(@NotNull String message, Player exception) {
		for (UUID uniqueId : this.players) {
			Player player = Bukkit.getPlayer(uniqueId);
			if (player != null && !uniqueId.equals(exception.getUniqueId())) {
				player.sendMessage(message);
			}
		}
	}

	public void endMinigame(boolean urgently) {
		this.setFinished();
		if (urgently) {
			this.endTeleport();
			Bukkit.getPluginManager().callEvent(new MinigameEndEvent(this, this.players));
			this.deleteWorlds(true);
		} else {
			Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
				this.endTeleport();
				Bukkit.getPluginManager().callEvent(new MinigameEndEvent(this, this.players));
				this.deleteWorlds(false);
			}, 100L);
		}
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

	public void cancelCountdown() {
		if (this.countingDown && this.countdown != null) {
			this.setWaiting();
			this.broadcastTitleAndEffect(ChatColor.RED + "CANCELLED!", Effect.CLICK2);
			this.countdown.cancel();
			this.spectators.clear();
			this.players.clear();
		}
	}

	public void handleChatEvent(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) {
			return;
		}

		Player player = e.getPlayer();
		Set<Player> recipients = e.getRecipients();
		Iterator<Player> iterator = recipients.iterator();
		if (this.inProgress || this.finished) {
			if (this.isSpectator(player.getUniqueId())) {
				e.setFormat(StringUtil.SPECTATOR_PREFIX + ChatColor.RESET + ChatColor.BLUE + ChatColor.stripColor("%s") + ChatColor.DARK_GRAY + ChatColor.BOLD + " » " + ChatColor.RESET + ChatColor.WHITE + "%s");
			} else {
				e.setFormat(StringUtil.MINIGAME_PREFIX + ChatColor.RESET + ChatColor.GREEN + ChatColor.stripColor("%s") + ChatColor.DARK_GRAY + ChatColor.BOLD + " » " + ChatColor.RESET + ChatColor.WHITE + "%s");
			}
		} else {
			e.setFormat(StringUtil.LOBBY_PREFIX + ChatColor.RESET + e.getFormat());
		}

		while (true) {
			Player recipient;
			do {
				if (!iterator.hasNext()) {
					return;
				}

				recipient = iterator.next();
			} while (this.players.contains(player.getUniqueId()) ? (this.isSpectator(player.getUniqueId()) ? this.isSpectator(recipient.getUniqueId()) : this.players.contains(recipient.getUniqueId())) : !this.players.contains(recipient.getUniqueId()));
			e.getRecipients().remove(recipient);
		}
	}

	public abstract boolean createWorlds();
	public abstract boolean worldsLoaded();
	public abstract void startTeleport();
	public abstract void startCountdown(@NotNull List<UUID> players);
	public abstract void endTeleport();
	public abstract void deleteWorlds() throws IOException;
	public abstract void handleEvent(@NotNull Event e);
}
