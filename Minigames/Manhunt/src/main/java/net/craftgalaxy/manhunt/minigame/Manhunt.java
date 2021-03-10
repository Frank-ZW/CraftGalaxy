package net.craftgalaxy.manhunt.minigame;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.craftgalaxy.manhunt.ManhuntCore;
import net.craftgalaxy.minigameservice.bukkit.BukkitService;
import net.craftgalaxy.minigameservice.bukkit.minigame.SurvivalMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.ItemUtil;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import net.craftgalaxy.minigameservice.bukkit.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Manhunt extends SurvivalMinigame {

	private final ManhuntCore plugin;
	private UUID speedrunner;
	private final Set<UUID> hunters = new ObjectOpenHashSet<>();

	public Manhunt(int gameKey, Location lobby) {
		super("Manhunt", gameKey, lobby);
		this.plugin = ManhuntCore.getInstance();
	}

	@Nullable
	public Player getPlayerSpeedrunner() {
		return Bukkit.getPlayer(this.speedrunner);
	}

	/**
	 * Updates the hunter's Player Tracker to point to the speedrunner's
	 * latest location. A check must be performed to ensure the item
	 * being right clicked is a compass and the player is a hunter.
	 *
	 * @param sender    The player right clicking the compass
	 * @param compass   The player tracker being right clicked
	 */
	public void updatePlayerTracker(@NotNull Player sender, @NotNull ItemStack compass) {
		Player player = this.getPlayerSpeedrunner();
		if (player == null || !player.isOnline()) {
			sender.sendActionBar(ChatColor.RED + "There are no players to track!");
			return;
		}

		CompassMeta meta = (CompassMeta) compass.getItemMeta();
		if (player.getWorld().equals(sender.getWorld())) {
			meta.setLodestoneTracked(false);
			meta.setLodestone(player.getLocation());
			compass.setItemMeta(meta);
			sender.sendActionBar(ChatColor.GREEN + "Currently tracking " + player.getName() + "'s latest location.");
		} else {
			sender.sendActionBar(ChatColor.RED + "There are no players to track!");
		}
	}

	/**
	 * @param item  The item to check if a player tracker.
	 * @return      True if the item is a player tracker and false otherwise.
	 */
	public boolean isPlayerTracker(@NotNull ItemStack item) {
		if (item.getType() == Material.COMPASS) {
			CompassMeta meta = (CompassMeta) item.getItemMeta();
			return ItemUtil.PLAYER_TRACKER.equals(meta.getDisplayName());
		}

		return false;
	}

	/**
	 * Broadcasts the winner of the Manhunt before teleporting players back to the
	 * main lobby and deleting world data.
	 *
	 * @param runnerWinner  True if the speedrunner is the winner, false otherwise.
	 * @param urgently      True if the minigame should be ended now.
	 */
	public void endMinigame(boolean runnerWinner, boolean urgently) {
		if (runnerWinner) {
			this.broadcast(ChatColor.GREEN + "The speedrunner has won the Manhunt.");
		} else {
			this.broadcast(ChatColor.GREEN + "The hunters have won the Manhunt.");
		}

		super.endMinigame(urgently);
	}

	@Override
	public boolean createWorlds() {
		if (this.worlds.size() == 3) {
			return true;
		}

		for (int i = 0; i < 3; i++) {
			World.Environment environment = World.Environment.values()[i];
			World world = new WorldCreator(this.gameKey + "_" + StringUtils.lowerCase(String.valueOf(environment))).environment(environment).createWorld();
			if (world != null) {
				if (environment == World.Environment.NORMAL) {
					world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
				}

				world.setDifficulty(Difficulty.NORMAL);
				world.setKeepSpawnInMemory(false);
				world.setAutoSave(false);
				this.worlds.put(environment, world);
			}
		}

		return this.worlds.size() == 3;
	}

	/**
	 * Returns true if the server has Manhunt worlds loaded into memory.
	 *
	 * @return  True if the world cache is not empty, false otherwise.
	 */
	@Override
	public boolean worldsLoaded() {
		return !this.worlds.isEmpty();
	}

	@Override
	public void startTeleport() {
		super.startTeleport();
		Player runner = this.getPlayerSpeedrunner();
		if (runner == null) {
			this.broadcast(ChatColor.RED + "An error occurred while retrieving the speedrunner. Contact an administrator if this occurs.");
			return;
		}

		this.getOverworld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		float theta = -90.0F;
		float delta = 360.0F / this.hunters.size();
		int radius = Math.max(8, this.hunters.size() * 3);
		Location spawn = this.getOverworld().getSpawnLocation();
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
			if (this.isSpeedrunner(uniqueId)) {
				player.teleportAsync(spawn).thenAccept(result -> {
					if (result) {
						player.sendMessage(ChatColor.GREEN + "You are the speedrunner. You must kill the Enderdragon before the hunters kill you.");
					} else {
						player.sendMessage(ChatColor.RED + "Failed to teleport you to the Manhunt worlds. Contact an administrator if this occurs.");
					}
				});
			} else {
				int x = (int) (spawn.getX() + radius * Math.cos(Math.toRadians(theta)));
				int z = (int) (spawn.getZ() + radius * Math.sin(Math.toRadians(theta)));
				int y = spawn.getWorld().getHighestBlockYAt(x, z);
				Location hunter = new Location(spawn.getWorld(), x, y, z);
				player.teleportAsync(hunter).thenAccept(result -> {
					if (result) {
						player.sendMessage(ChatColor.RED + "You are " + (this.hunters.size() == 1 ? "the" : "a") + " hunter. You must use your Player Tracker to relentlessly hunt and kill " + runner.getName() + ".");
						player.getInventory().setItem(8, ItemUtil.createPlayerTracker());
					} else {
						player.sendMessage(ChatColor.RED + "Failed to teleport you to the Manhunt worlds. Contact an administrator if this occurs.");
					}
				});

				theta += delta;
			}

			this.startTimestamp = System.currentTimeMillis();
		}
	}

	@Override
	public void startCountdown(@NotNull List<UUID> players) {
		super.startCountdown(players);
		speedrunner = players.remove(this.random.nextInt(players.size()));
		this.hunters.addAll(players);
	}

	@Override
	public void unload() {
		super.unload();
		this.hunters.clear();
		this.speedrunner = null;
	}

	@Override
	public void handleChatFormat(@NotNull AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String prefix = this.plugin.getChatFormatter() == null ? null : this.plugin.getChatFormatter().getPlayerPrefix(player);
		if (this.status.isInProgress() || this.status.isFinished()) {
			if (this.isSpectator(player.getUniqueId())) {
				e.setFormat(StringUtil.SPECTATOR_PREFIX + ChatColor.RESET + (prefix == null || prefix.length() == 0 ? "" : ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RESET + " ") + ChatColor.BLUE + ChatColor.stripColor("%s") + ChatColor.DARK_GRAY + ChatColor.BOLD + " » " + ChatColor.RESET + ChatColor.WHITE + "%s");
			} else {
				e.setFormat(StringUtil.MINIGAME_PREFIX + ChatColor.RESET + (prefix == null || prefix.length() == 0 ? "" : ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RESET + " ") + (this.isSpeedrunner(player.getUniqueId()) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.stripColor("%s") + ChatColor.DARK_GRAY + ChatColor.BOLD + " » " + ChatColor.RESET + ChatColor.WHITE + "%s");
			}
		} else {
			e.setFormat(StringUtil.LOBBY_PREFIX + ChatColor.RESET + e.getFormat());
		}
	}

	@Override
	public void disconnectMessage(@NotNull Player player) {
		if (this.isSpectator(player.getUniqueId())) {
			return;
		}

		this.broadcast((this.isSpeedrunner(player.getUniqueId()) ? ChatColor.GREEN : ChatColor.RED) + player.getName() + ChatColor.GRAY + " disconnected.");
	}

	@Override
	public void handleEvent(@NotNull Event event) {
		if (event instanceof PlayerEvent) {
			Player player = ((PlayerEvent) event).getPlayer();
			if (event instanceof PlayerInteractEvent) {
				PlayerInteractEvent e = (PlayerInteractEvent) event;
				Block clicked = e.getClickedBlock();
				if (this.isSpectator(player.getUniqueId())) {
					if (e.getAction() == Action.PHYSICAL || (clicked != null && clicked.getType() == Material.BELL)) {
						e.setCancelled(true);
					}

					return;
				}

				if (this.isHunter(player.getUniqueId()) && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && e.getItem() != null && this.isPlayerTracker(e.getItem())) {
					this.updatePlayerTracker(player, e.getItem());
				}

				if (clicked != null && ItemUtil.isBedType(clicked.getType()) && player.getWorld().getEnvironment() == World.Environment.NETHER && e.getAction() == Action.RIGHT_CLICK_BLOCK && this.plugin.isBedBombingDisabled()) {
					player.sendMessage(ChatColor.RED + "Bed bombing has been disabled in the Nether.");
					e.setCancelled(true);
				}
			} else if (event instanceof PlayerDropItemEvent) {
				PlayerDropItemEvent e = (PlayerDropItemEvent) event;
				if (this.isSpectator(player.getUniqueId())) {
					e.setCancelled(true);
					return;
				}

				if (this.isHunter(player.getUniqueId())) {
					ItemStack drop = e.getItemDrop().getItemStack();
					if (this.isPlayerTracker(drop)) {
						e.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You cannot drop your Player Tracker!");
					}
				}
			} else if (event instanceof PlayerRespawnEvent) {
				PlayerRespawnEvent e = (PlayerRespawnEvent) event;
				if (this.getOverworld() == null) {
					return;
				}

				e.setRespawnLocation(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
				if (this.isHunter(player.getUniqueId())) {
					player.getInventory().setItem(8, ItemUtil.createPlayerTracker());
				}
			} else if (event instanceof PlayerPortalEvent) {
				PlayerPortalEvent e = (PlayerPortalEvent) event;
				if (e.getTo().getWorld() == null || e.getFrom().getWorld() == null || e.isCancelled()) {
					return;
				}

				World fromWorld = e.getFrom().getWorld();
				switch (e.getCause()) {
					case NETHER_PORTAL:
						switch (fromWorld.getEnvironment()) {
							case NORMAL:
								e.getTo().setWorld(this.getNether());
								if (this.players.contains(player.getUniqueId()) && !this.spectators.contains(player.getUniqueId())) {
									BukkitService.getInstance().grantNetherAdvancement(player);
								}

								break;
							case NETHER:
								e.setTo(new Location(this.getOverworld(), e.getFrom().getX() * 8.0D, e.getFrom().getY(), e.getFrom().getZ() * 8.0D));
								break;
							default:
						}

						break;
					case END_PORTAL:
						switch (fromWorld.getEnvironment()) {
							case NORMAL:
								e.getTo().setWorld(this.getEnd());
								if (this.players.contains(player.getUniqueId()) && !this.spectators.contains(player.getUniqueId())) {
									BukkitService.getInstance().grantEndAdvancement(player);
								}

								break;
							case THE_END:
								e.setTo(player.getBedSpawnLocation() == null ? this.getOverworld().getSpawnLocation() : player.getBedSpawnLocation());
								break;
							default:
						}

						break;
					default:
				}
			} else if (event instanceof PlayerPickupExperienceEvent) {
				PlayerPickupExperienceEvent e = (PlayerPickupExperienceEvent) event;
				if (this.isSpectator(player.getUniqueId())) {
					e.setCancelled(true);
				}
			} else if (event instanceof PlayerPickupArrowEvent) {
				PlayerPickupArrowEvent e = (PlayerPickupArrowEvent) event;
				if (this.isSpectator(player.getUniqueId())) {
					e.setCancelled(true);
				}
			} else if (event instanceof PlayerAdvancementDoneEvent) {
				PlayerAdvancementDoneEvent e = (PlayerAdvancementDoneEvent) event;
				this.addAwardedAdvancement(player, e.getAdvancement());
			} else if (event instanceof PlayerAdvancementCriterionGrantEvent) {
				PlayerAdvancementCriterionGrantEvent e = (PlayerAdvancementCriterionGrantEvent) event;
				if (this.isSpectator(e.getPlayer().getUniqueId())) {
					e.setCancelled(true);
				}
			}
		} else if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			if (this.status.isFinished() || this.isSpectator(e.getEntity().getUniqueId())) {
				e.setCancelled(true);
				return;
			}

			if (this.status.isInProgress()) {
				if (this.isSpeedrunner(e.getEntity().getUniqueId())) {
					this.endMinigame(false, false);
				} else {
					e.getDrops().removeIf(this::isPlayerTracker);
				}
			}
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (!(e.getDamager() instanceof Player) || e.isCancelled()) {
				return;
			}

			Player damager = (Player) e.getDamager();
			if (this.isSpectator(damager.getUniqueId()) || (e.getEntity() instanceof Player && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - this.startTimestamp) <= 5)) {
				e.setCancelled(true);
			}
		} else if (event instanceof EnderDragonChangePhaseEvent) {
			EnderDragonChangePhaseEvent e = (EnderDragonChangePhaseEvent) event;
			if (e.getNewPhase() == EnderDragon.Phase.DYING) {
				this.endMinigame(true, false);
			}
		} else if (event instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent) event;
			if (e.getEntity() instanceof Player) {
				if (this.status.isFinished() || this.isSpectator(e.getEntity().getUniqueId())) {
					e.setCancelled(true);
				}
			}
		} else if (event instanceof BlockPlaceEvent) {
			BlockPlaceEvent e = (BlockPlaceEvent) event;
			Player player = e.getPlayer();
			if (this.isSpectator(player.getUniqueId())) {
				e.setCancelled(true);
				return;
			}

			if (ItemUtil.isBedType(e.getBlock().getType()) && player.getWorld().getEnvironment() == World.Environment.NETHER && this.plugin.isBedBombingDisabled()) {
				player.sendMessage(ChatColor.RED + "Bed bombing has been disabled in the Nether.");
			}
		} else if (event instanceof BlockBreakEvent) {
			BlockBreakEvent e = (BlockBreakEvent) event;
			if (this.isSpectator(e.getPlayer().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof EntityPickupItemEvent) {
			EntityPickupItemEvent e = (EntityPickupItemEvent) event;
			if (e.getEntity() instanceof Player) {
				if (this.isSpectator(e.getEntity().getUniqueId())) {
					e.setCancelled(true);
				}
			}
		} else if (event instanceof FoodLevelChangeEvent) {
			FoodLevelChangeEvent e = (FoodLevelChangeEvent) event;
			if (this.isSpectator(e.getEntity().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof InventoryClickEvent) {
			InventoryClickEvent e = (InventoryClickEvent) event;
			if (this.isSpectator(e.getWhoClicked().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof VehicleEnterEvent) {
			VehicleEnterEvent e = (VehicleEnterEvent) event;
			if (!(e.getEntered() instanceof Player)) {
				return;
			}

			if (this.isSpectator(e.getEntered().getUniqueId())) {
				e.setCancelled(true);
			}
		} else if (event instanceof EntityTargetEvent) {
			EntityTargetEvent e = (EntityTargetEvent) event;
			if (e.getTarget() instanceof Player) {
				if (this.isSpectator(e.getTarget().getUniqueId())) {
					e.setCancelled(true);
				}
			}
		} else if (event instanceof EntityCombustEvent) {
			EntityCombustEvent e = (EntityCombustEvent) event;
			if (e.getEntity() instanceof Player) {
				if (this.isSpectator(e.getEntity().getUniqueId())) {
					e.setCancelled(true);
				}
			}
		}
	}

	@Override
	public void removePlayer(@NotNull Player player) {
		super.removePlayer(player);
		if (this.isSpeedrunner(player.getUniqueId())) {
			if (this.status.isInProgress()) {
				this.endMinigame(false, false);
			} else {
				this.speedrunner = null;
			}
		} else if (this.hunters.remove(player.getUniqueId()) && this.status.isInProgress() && this.hunters.isEmpty()) {
			this.endMinigame(true, false);
		}
	}

	@Override
	public void cancelCountdown() {
		super.cancelCountdown();
		this.speedrunner = null;
		this.hunters.clear();
	}

	public boolean isSpeedrunner(@NotNull UUID uniqueId) {
		return uniqueId.equals(this.speedrunner);
	}

	public boolean isHunter(UUID uniqueId) {
		return this.hunters.contains(uniqueId);
	}
}
