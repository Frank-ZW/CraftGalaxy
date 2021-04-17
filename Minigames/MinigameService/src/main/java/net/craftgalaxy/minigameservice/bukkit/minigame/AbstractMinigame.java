package net.craftgalaxy.minigameservice.bukkit.minigame;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.craftgalaxy.minigameservice.bukkit.BukkitService;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEndEvent;
import net.craftgalaxy.minigameservice.bukkit.runnable.CountdownRunnable;
import net.craftgalaxy.minigameservice.bukkit.util.ItemUtil;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import net.milkbowl.vault.chat.Chat;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class AbstractMinigame {

	protected final BukkitService plugin;
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

	/**
	 * Returns true if the mini-game is in the waiting stage. The waiting stage is the duration when the
	 * mini-game is not active and has not started the countdown.
	 *
	 * @return  True if the mini-game is waiting, false otherwise.
	 */
	public boolean isWaiting() {
		return this.status.isWaiting();
	}

	/**
	 * Returns true if the mini-game is counting down before the server sends the players to the spawning location.
	 *
	 * @return  True if the mini-game is counting down, false otherwise.
	 */
	public boolean isCountingDown() {
		return this.status.isCountingDown();
	}

	/**
	 * Returns true if the mini-game is in progress. A mini-game is in progress when the countdown has finished but
	 * the game has not ended yet.
	 *
	 * @return  True if the mini-game is in progress, false otherwise.
	 */
	public boolean isInProgress() {
		return this.status.isInProgress();
	}

	/**
	 * Returns true if the mini-game is finished. A mini-game is finished during the period of time after the victory
	 * message has been broadcasted but the players have not been sent back to the lobby yet.
	 *
	 * @return  True if the mini-game is finished, false otherwise.
	 */
	public boolean isFinished() {
		return this.status.isFinished();
	}

	/**
	 * Returns the raw name of the mini-game. The raw name is the display name with all spaces
	 * removed.
	 *
	 * @return  The raw name of the mini-game.
	 */
	public String getRawName() {
		return this.name.replaceAll("\\s+", "");
	}

	/**
	 * Returns a name of the world being created.
	 *
	 * @param environment   The environment of the world being created.
	 * @return              The name of the world to be created.
	 */
	public String getWorldName(World.Environment environment) {
		return StringUtils.capitalize(this.getRawName()) + "_" + this.gameKey + "_" + StringUtils.lowerCase(String.valueOf(environment));
	}

	/**
	 * @return  The display name of the game presented to the players.
	 */
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

	/**
	 * Adds the specified player to the mini-game players list and hides the
	 * spectator from all other in-game players.
	 *
	 * @param spectator The player spectating.
	 */
	public void hideSpectator(@NotNull Player spectator) {
		this.players.add(spectator.getUniqueId());
		this.spectators.add(spectator.getUniqueId());
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
			player.teleport(this.lobby);
			this.showSpectator(player);
		}
	}

	/**
	 * Returns a String representing the display name of the player in chat.
	 *
	 * @param offline   The player's offline instance.
	 * @return          The chat format of the player's name.
	 */
	public String getFormattedDisplayName(OfflinePlayer offline) {
		return ChatColor.GREEN + offline.getName();
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

	public void endMinigame(boolean urgently) {
		this.status = MinigameStatus.FINISHED;
		if (urgently) {
			this.endMinigameInternal();
		} else {
			Bukkit.getScheduler().runTaskLater(this.plugin, this::endMinigameInternal, 200);
		}
	}

	private void endMinigameInternal() {
		this.endTeleport();
		this.deleteWorlds(true);
		Bukkit.getPluginManager().callEvent(new MinigameEndEvent(this, this.players));
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
		Bukkit.getScheduler().cancelTasks(this.plugin);
	}

	public String getPlayerPrefix(Player player) {
		String prefix = null;
		Chat formatter = this.plugin.getChatFormatter();
		if (formatter != null) {
			prefix = formatter.getPlayerPrefix(player);
		}

		return (prefix == null ? "" : ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RESET + " ") + this.getFormattedDisplayName(player);
	}

	public String getSpectatorFormat(Player player) {
		String prefix = null;
		Chat formatter = this.plugin.getChatFormatter();
		if (formatter != null) {
			prefix = formatter.getPlayerPrefix(player);
		}

		return (prefix == null ? "" : " " + ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RESET + " ") + ChatColor.BLUE + player.getName();
	}

	/**
	 * Returns a long representing the maximum length the game can last. The timestamp is in seconds.
	 * <p>
	 * If the value returned is negative, the result will be ignored and the scheduler will not be
	 * executed.
	 *
	 * @return  The maximum length that the given game can last for.
	 */
	public long getScheduledForceEndTimestamp() {
		return TimeUnit.HOURS.toSeconds(3);
	}

	public boolean isSpectatorCompass(@Nullable ItemStack item) {
		if (item == null) {
			return false;
		}

		net.minecraft.server.v1_16_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsItem.getTag();
		return item.getItemMeta() instanceof CompassMeta && compound != null && compound.getBoolean("spectator_compass");
	}

	public void handleSpectatorEvent(@NotNull Event e) {
		if (e instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent) e;
			event.setCancelled(true);
			Player player = event.getPlayer();
			Block clicked = event.getClickedBlock();
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				ItemStack item = event.getItem();
				if (this.isSpectatorCompass(item)) {
					Inventory inventory = Bukkit.createInventory(player, 36, ItemUtil.SPECTATOR_GUI);
					for (UUID uniqueId : this.players) {
						if (this.isSpectator(uniqueId)) {
							continue;
						}

						OfflinePlayer offline = Bukkit.getOfflinePlayer(uniqueId);
						ItemStack head = new ItemStack(Material.PLAYER_HEAD);
						SkullMeta meta = (SkullMeta) head.getItemMeta();
						if (meta != null) {
							meta.setDisplayName(this.getFormattedDisplayName(offline));
							meta.setOwningPlayer(offline);
							head.setItemMeta(meta);
						}

						inventory.addItem(head);
					}

					player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
					player.openInventory(inventory);
					return;
				}
			}

			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && clicked != null && clicked.getState() instanceof Chest) {
				Chest chest = (Chest) clicked.getState();
				player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
				player.openInventory(chest.getInventory());
			}
		} else if (e instanceof PlayerPickupExperienceEvent) {
			((PlayerPickupExperienceEvent) e).setCancelled(true);
		} else if (e instanceof PlayerPickupArrowEvent) {
			((PlayerPickupArrowEvent) e).setCancelled(true);
		} else if (e instanceof PlayerAdvancementCriterionGrantEvent) {
			((PlayerAdvancementCriterionGrantEvent) e).setCancelled(true);
		} else if (e instanceof EntityPickupItemEvent) {
			((EntityPickupItemEvent) e).setCancelled(true);
		} else if (e instanceof PlayerDeathEvent) {
			((PlayerDeathEvent) e).setCancelled(true);
		} else if (e instanceof PlayerDropItemEvent) {
			((PlayerDropItemEvent) e).setCancelled(true);
		} else if (e instanceof EntityDamageByEntityEvent) {
			((EntityDamageByEntityEvent) e).setCancelled(true);
		} else if (e instanceof EntityDamageEvent) {
			((EntityDamageEvent) e).setCancelled(true);
		} else if (e instanceof BlockPlaceEvent) {
			((BlockPlaceEvent) e).setCancelled(true);
		} else if (e instanceof BlockBreakEvent) {
			((BlockBreakEvent) e).setCancelled(true);
		} else if (e instanceof FoodLevelChangeEvent) {
			((FoodLevelChangeEvent) e).setCancelled(true);
		} else if (e instanceof InventoryClickEvent) {
			InventoryClickEvent event = (InventoryClickEvent) e;
			event.setCancelled(true);
			if (event.getView().getTitle().equals(ItemUtil.SPECTATOR_GUI)) {
				ItemStack clicked = event.getCurrentItem();
				if (clicked != null && clicked.getType() == Material.PLAYER_HEAD) {
					Player spectated = Bukkit.getPlayer(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
					if (spectated != null) {
						Player player = (Player) event.getWhoClicked();
						player.closeInventory(InventoryCloseEvent.Reason.TELEPORT);
						player.teleportAsync(spectated.getLocation()).thenAccept(result -> {
							if (result) {
								player.sendMessage(ChatColor.GREEN + "You are now spectating " + spectated.getName());
							} else {
								player.sendMessage(ChatColor.RED + "Failed to teleport you to " + spectated.getName());
							}
						});
					}
				}
			}
		} else if (e instanceof VehicleEnterEvent) {
			((VehicleEnterEvent) e).setCancelled(true);
		} else if (e instanceof EntityTargetLivingEntityEvent) {
			((EntityTargetLivingEntityEvent) e).setCancelled(true);
		} else if (e instanceof EntityCombustEvent) {
			((EntityCombustEvent) e).setCancelled(true);
		}
	}

	protected abstract boolean playerStartTeleport(@NotNull Player player, Location to);
	protected abstract void playerEndTeleport(@NotNull Player player);
	protected abstract String startMessage(@NotNull UUID uniqueId);
	public abstract void startTeleport();
	public abstract void endTeleport();
	public abstract boolean createWorlds();
	public abstract boolean worldsLoaded();
	public abstract void deleteWorlds() throws IOException;
	public abstract void handlePlayerEvent(@NotNull Event e);

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
