package net.craftgalaxy.deathswap.minigame;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.craftgalaxy.deathswap.runnable.SwapRunnable;
import net.craftgalaxy.minigameservice.bukkit.minigame.SurvivalMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class DeathSwap extends SurvivalMinigame {

	private final List<UUID> alive = new ObjectArrayList<>();
	private BukkitRunnable swapRunnable;

	public DeathSwap(int gameKey, Location lobby) {
		super("Death Swap", gameKey, lobby);
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
	protected String startMessage(@NotNull UUID uniqueId) {
		return ChatColor.GREEN + "You must kill the other players in your " + this.getName() + " using your surroundings. PVP has been disabled.";
	}

	@Override
	public void startTeleport() {
		super.startTeleport();
		this.swapRunnable = new SwapRunnable(this);
		this.swapRunnable.runTaskTimer(this.plugin, 0L, 20L);
	}

	@Override
	public void startCountdown(@NotNull List<UUID> players) {
		super.startCountdown(players);
		this.alive.addAll(players);
	}

	@Override
	public void unload() {
		super.unload();
		this.alive.clear();
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
	public void cancelCountdown() {
		super.cancelCountdown();
		this.alive.clear();
	}

	@Override
	public void handleEvent(@NotNull Event event) {
		if (event instanceof PlayerEvent) {
			Player player = ((PlayerEvent) event).getPlayer();
			if (event instanceof PlayerTeleportEvent) {
				PlayerTeleportEvent e = (PlayerTeleportEvent) event;
				switch (e.getCause()) {
					case NETHER_PORTAL:
					case END_PORTAL:
						e.setCancelled(true);
						player.sendMessage(ChatColor.RED + "The Nether and End has been disabled for Death Swap. You must rely on another way to kill the opponents.");
					default:
				}
			} else if (event instanceof PlayerAdvancementCriterionGrantEvent) {
				PlayerAdvancementCriterionGrantEvent e = (PlayerAdvancementCriterionGrantEvent) event;
				if (this.isSpectator(e.getPlayer().getUniqueId())) {
					e.setCancelled(true);
				}
			} else if (event instanceof PlayerPickupArrowEvent) {
				PlayerPickupArrowEvent e = (PlayerPickupArrowEvent) event;
				if (this.isSpectator(e.getPlayer().getUniqueId())) {
					e.setCancelled(true);
				}
			} else if (event instanceof PlayerRespawnEvent) {
				PlayerRespawnEvent e = (PlayerRespawnEvent) event;
				e.setRespawnLocation(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
			} else if (event instanceof PlayerInteractEvent) {
				PlayerInteractEvent e = (PlayerInteractEvent) event;
				Block clickedBlock = e.getClickedBlock();
				if (this.isSpectator(e.getPlayer().getUniqueId()) && (e.getAction() == Action.PHYSICAL || (clickedBlock != null && clickedBlock.getType() == Material.BELL))) {
					e.setCancelled(true);
				}
			}
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if ((e.getDamager() instanceof Player && e.getEntity() instanceof Player && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.startTimestamp) >= 5) || this.isSpectator(e.getDamager().getUniqueId()) || this.isSpectator(e.getEntity().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			if (!this.status.isInProgress()) {
				return;
			}

			Player player = e.getEntity();
			e.setCancelled(true);
			if (this.alive.remove(player.getUniqueId())) {
				PlayerUtil.setSpectator(player);
				this.spectators.add(player.getUniqueId());
				this.hideSpectator(player);
				switch (this.alive.size()) {
					case 0:
						this.endMinigame(Bukkit.getOfflinePlayer(player.getUniqueId()));
						break;
					case 1:
						this.endMinigame(Bukkit.getOfflinePlayer(this.alive.get(0)));
						break;
					default:
						Bukkit.broadcastMessage("");
						Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has died. There are " + this.alive.size() + " players remaining.");
						Bukkit.broadcastMessage("");
				}
			}
		} else if (event instanceof PortalCreateEvent) {
			PortalCreateEvent e = (PortalCreateEvent) event;
			e.setCancelled(true);
			Entity entity = e.getEntity();
			if (entity != null) {
				entity.sendMessage(ChatColor.RED + "The Nether and End has been disabled for Death Swap. You must rely on another way to kill the opponents.");
			}
		} else if (event instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent) event;
			if (this.status.isFinished() || this.isSpectator(e.getEntity().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof EntityTargetLivingEntityEvent) {
			EntityTargetLivingEntityEvent e = (EntityTargetLivingEntityEvent) event;
			if (e.getTarget() != null && this.isSpectator(e.getTarget().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof EntityPickupItemEvent) {
			EntityPickupItemEvent e = (EntityPickupItemEvent) event;
			if (this.isSpectator(e.getEntity().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof EntityDropItemEvent) {
			EntityDropItemEvent e = (EntityDropItemEvent) event;
			if (this.isSpectator(e.getEntity().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof FoodLevelChangeEvent) {
			FoodLevelChangeEvent e = (FoodLevelChangeEvent) event;
			if (this.isSpectator(e.getEntity().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof VehicleEnterEvent) {
			VehicleEnterEvent e = (VehicleEnterEvent) event;
			if (this.isSpectator(e.getEntered().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof EntityCombustEvent) {
			EntityCombustEvent e = (EntityCombustEvent) event;
			if (this.isSpectator(e.getEntity().getUniqueId())) {
				e.setCancelled(true);
			}
		}
	}

	public void endMinigame(@Nullable OfflinePlayer player) {
		if (player == null) {
			Bukkit.broadcastMessage(ChatColor.RED + "An error occurred while retrieving the winner of the Death Swap. Contact an administrator if this occurs.");
		} else {
			Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has won the Death Swap.");
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
		if (online.size() > 1) {
			Location initial = online.get(0).getLocation();
			for (int i = 0; i < online.size(); i++) {
				Player player = online.get(i);
				Location to = i == online.size() - 1 ? initial : online.get(i + 1).getLocation();
				player.teleportAsync(to);
				player.playSound(to, Sound.ENTITY_ENDERMAN_TELEPORT, 0.5F, 0.5F);
			}
		}
	}

	@Override
	public void removePlayer(@NotNull Player player) {
		super.removePlayer(player);
		if (this.alive.remove(player.getUniqueId())) {
			if (this.alive.isEmpty()) {
				this.endMinigame(player);
			} else if (this.alive.size() == 1) {
				this.endMinigame(Bukkit.getOfflinePlayer(this.alive.get(0)));
			} else {
				Bukkit.broadcastMessage("");
				Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " has died. There are " + this.alive.size() + " players remaining.");
				Bukkit.broadcastMessage("");
			}
		}
	}
}
