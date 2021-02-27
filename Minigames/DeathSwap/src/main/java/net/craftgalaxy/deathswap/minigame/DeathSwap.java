package net.craftgalaxy.deathswap.minigame;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.craftgalaxy.deathswap.DeathSwapCore;
import net.craftgalaxy.deathswap.runnable.SwapRunnable;
import net.craftgalaxy.minigameservice.bukkit.minigame.SurvivalMinigame;
import net.craftgalaxy.minigameservice.bukkit.runnable.CountdownRunnable;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import net.craftgalaxy.minigameservice.bukkit.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class DeathSwap extends SurvivalMinigame {

	private final DeathSwapCore plugin;
	private final List<UUID> alive = new ObjectArrayList<>();
	private BukkitRunnable swapRunnable;

	public DeathSwap(int gameKey, Location lobby) {
		super("Death Swap", gameKey, lobby);
		this.plugin = DeathSwapCore.getInstance();
	}

	@Override
	public boolean createWorlds() {
		if (this.getOverworld() != null) {
			return true;
		}

		World.Environment environment = World.Environment.NORMAL;
		World world = new WorldCreator(this.gameKey + "_" + StringUtils.lowerCase(String.valueOf(World.Environment.NORMAL))).createWorld();
		if (world != null) {
			world.setAutoSave(false);
			world.setKeepSpawnInMemory(false);
			world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			this.worlds.put(environment, world);
		}

		return this.getOverworld() != null;
	}

	@Override
	public boolean worldsLoaded() {
		return this.getOverworld() != null;
	}

	@Override
	public void startTeleport() {
		this.setInProgress();
		World overworld = this.getOverworld();
		overworld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		Location spawn = overworld.getSpawnLocation();
		float theta = -90.0F;
		float delta = 360.0F / this.players.size();
		int radius = Math.min(8, 3 * this.players.size());
		for (UUID uniqueId : this.players) {
			Player player = Bukkit.getPlayer(uniqueId);
			if (player == null || !player.isOnline()) {
				continue;
			}

			if (player.isDead()) {
				player.spigot().respawn();
			}

			PlayerUtil.clearAdvancements(player);
			PlayerUtil.resetAttributes(player);
			int x = (int) (spawn.getX() + radius * Math.cos(Math.toRadians(theta)));
			int z = (int) (spawn.getZ() + radius * Math.sin(Math.toRadians(theta)));
			int y = overworld.getHighestBlockYAt(x, z);
			Location location = new Location(overworld, x, y, z);
			player.teleportAsync(location).thenAccept(result -> {
				if (result) {
					player.sendMessage(ChatColor.GREEN + "You must kill the other players in the Death Swap using your surroundings. Player hits will be cancelled.");
				} else {
					player.sendMessage(ChatColor.RED + "Failed to teleport you to the DeathSwap world. Contact an administrator if this occurs.");
				}
			});

			theta += delta;
		}

		this.startTimestamp = System.currentTimeMillis();
		this.swapRunnable = new SwapRunnable(this);
		this.swapRunnable.runTaskTimer(this.plugin, 0L, 20L);
	}

	@Override
	public void startCountdown(@NotNull List<UUID> players) {
		this.setCountingDown();
		this.alive.addAll(players);
		this.players.addAll(players);
		this.countdown = new CountdownRunnable(this);
		this.countdown.runTaskTimer(this.plugin, 20L, 20L);
	}

	@Override
	public void endTeleport() {
		for (UUID uniqueId : this.players) {
			Player player = Bukkit.getPlayer(uniqueId);
			if (player == null || !player.isOnline()) {
				continue;
			}

			if (this.isSpectator(uniqueId)) {
				this.showSpectator(player);
				PlayerUtil.unsetSpectator(player);
			} else {
				PlayerUtil.resetAttributes(player);
			}

			player.teleportAsync(this.lobby).thenAccept(result -> {
				if (!result) {
					player.sendMessage(ChatColor.RED + "Failed to teleport you back to the lobby. Contact an administrator if this occurs.");
					this.plugin.getLogger().warning("Failed to teleport " + player.getName() + " to the lobby.");
				}
			});
		}

		this.setWaiting();
	}

	@Override
	public void deleteWorlds() throws IOException {
		super.deleteWorlds();
		this.alive.clear();
		if (this.swapRunnable != null) {
			this.swapRunnable.cancel();
			this.swapRunnable = null;
		}
	}

	@Override
	public void handleEvent(@NotNull Event event) {
		if (event instanceof PlayerEvent) {
			Player player = ((PlayerEvent) event).getPlayer();
			if (event instanceof AsyncPlayerChatEvent) {
				AsyncPlayerChatEvent e = (AsyncPlayerChatEvent) event;

			} else if (event instanceof PlayerTeleportEvent) {
				PlayerTeleportEvent e = (PlayerTeleportEvent) event;
				switch (e.getCause()) {
					case NETHER_PORTAL:
					case END_PORTAL:
						e.setCancelled(true);
						player.sendMessage(ChatColor.RED + "The " + (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL ? "Nether" : "End") + " is disabled for Death Swap! You must rely on another way to kill your opponents!");
					default:
				}
			}
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (e.getDamager() instanceof Player && e.getEntity() instanceof Player && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.startTimestamp) >= 5) {
				e.setCancelled(true);
			}
		} else if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			Player player = e.getEntity();
			this.spectators.add(player.getUniqueId());
			if (!this.alive.remove(player.getUniqueId())) {
				return;
			}

			if (this.alive.isEmpty()) {
				this.endMinigame(null);
				return;
			}

			e.setCancelled(true);
			this.hideSpectator(player);
			PlayerUtil.setSpectator(player);
			if (this.alive.size() == 1) {
				this.endMinigame(Bukkit.getPlayer(this.alive.get(0)));
			} else {
				this.broadcast("");
				this.broadcast(ChatColor.GREEN + player.getName() + " has died... There are " + this.alive.size() + " players remaining.");
				this.broadcast("");
			}
		}
	}

	public void endMinigame(@Nullable Player winner) {
		if (winner == null) {
			this.broadcast(ChatColor.RED + "An error occurred while retrieving the winner for the Death Swap. Contact an administrator if this occurs.");
		} else {
			this.broadcast(ChatColor.GREEN + winner.getName() + " has won the Death Swap.");
		}

		if (this.swapRunnable != null) {
			this.swapRunnable.cancel();
			this.swapRunnable = null;
		}
		
		super.endMinigame(false);
	}

	/**
	 * Handles swapping players across the map. Since at any given time, a player might be offline, the
	 * method first streams through the player list and filters out those that are offline before collecting
	 * the remaining players into a list.
	 * <p>
	 * If the list is empty or has one player, then the server ends the
	 * Death Swap. Otherwise, the server loops through the list of online
	 * players and teleports them to the player stored adjacently to them
	 * on the right
	 */
	public void swapPlayers() {
		if (this.players.isEmpty() || this.players.size() == 1) {
			return;
		}

		List<Player> online = this.alive.parallelStream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
		if (online.isEmpty()) {
			this.endMinigame(null);
		} else if (online.size() == 1) {
			this.endMinigame(online.get(0));
		} else {
			Location initial = online.get(0).getLocation();
			for (int i = 0; i < online.size(); i++) {
				Player player = online.get(i);
				if (i == online.size() - 1) {
					player.teleportAsync(initial);
					player.playSound(initial, Sound.ENTITY_ENDERMAN_TELEPORT, 0.5F, 0.5F);
				} else {
					Player next = online.get(i + 1);
					player.teleportAsync(next.getLocation());
					player.playSound(next.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5F, 0.5F);
				}
			}
		}
	}
}
