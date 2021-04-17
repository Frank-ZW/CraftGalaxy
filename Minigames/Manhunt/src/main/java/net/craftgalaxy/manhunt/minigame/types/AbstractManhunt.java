package net.craftgalaxy.manhunt.minigame.types;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import net.craftgalaxy.minigameservice.bukkit.minigame.SurvivalMinigame;
import net.craftgalaxy.minigameservice.bukkit.util.ItemUtil;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractManhunt extends SurvivalMinigame {

	private UUID speedrunner;
	private final Set<UUID> hunters = new ObjectOpenHashSet<>();

	public AbstractManhunt(int gameKey, Location lobby) {
		this(gameKey, lobby, true, true);
	}

	public AbstractManhunt(int gameKey, Location lobby, boolean netherAccess, boolean endAccess) {
		super("Manhunt", gameKey, lobby, netherAccess, endAccess);
	}

	/**
	 * Returns the player object representing the speedrunner. If the speedrunner is not online,
	 * the method returns null.
	 *
	 * @return  The player instance of the speedrunner if the player is online, null otherwise.
	 */
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
		net.minecraft.server.v1_16_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsItem.getTag();
		if (compound == null) {
			return false;
		}

		return compound.getBoolean("player_tracker") && item.getItemMeta() instanceof CompassMeta;
	}

	/**
	 * Broadcasts the winner of the Manhunt before teleporting players back to the
	 * main lobby and deleting world data.
	 *
	 * @param runnerWinner  True if the speedrunner is the winner, false otherwise.
	 * @param urgently      True if the minigame should be ended now.
	 */
	public void endMinigame(boolean runnerWinner, boolean urgently) {
		if (this.status.isFinished()) {
			return;
		}

		if (runnerWinner) {
			Bukkit.broadcastMessage(ChatColor.GREEN + "The speedrunner has won the Manhunt.");
		} else {
			Bukkit.broadcastMessage(ChatColor.GREEN + "The hunters have won the Manhunt.");
		}

		super.endMinigame(urgently);
	}

	/**
	 * Attempts to generate new worlds for the Manhunt game. If an error occurs while
	 * loading up one or more worlds, the method returns false.
	 *
	 * @return  True if all worlds were loaded successfully, false otherwise.
	 */
	@Override
	public boolean createWorlds() {
		if (this.worlds.size() == 3) {
			return true;
		}

		for (int i = 0; i < 3; i++) {
			World.Environment environment = World.Environment.values()[i];
			World world = new WorldCreator(this.getWorldName(environment)).environment(environment).createWorld();
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

	@Override
	public String getFormattedDisplayName(OfflinePlayer offline) {
		return (this.isSpeedrunner(offline.getUniqueId()) ? ChatColor.GREEN : ChatColor.RED) + offline.getName();
	}

	/**
	 * Returns the message sent to players at the start of the game after the player has been teleported.
	 *
	 * @param uniqueId  The UUID of the player to send the message to.
	 * @return          The message sent to the specified player at the start of the game.
	 */
	@Override
	protected String startMessage(@NotNull UUID uniqueId) {
		return this.isSpeedrunner(uniqueId) ? ChatColor.GREEN + "You are the speedrunner. You must kill the Enderdragon before the hunters kill you." : ChatColor.RED + "You are " + (this.hunters.size() == 1 ? "the" : "a") + " hunter. You must use your Player Tracker to relentlessly kill the speedrunner.";
	}

	/**
	 * Handles the starting teleportation logic per player given the distance from the world's default spawn location
	 * and the amount rotated by.
	 * <p>
	 * The spawn location is determined through polar coordinates, where the initial spawn is horizontal along the
	 * Z-axis a distance specified by radius blocks away, and the angle increments to rotate the spawn location by
	 * the specified amount. Hunters will spawn in a circle around the speedrunner.
	 *
	 * @param player    The player to be teleported.
	 * @param to        The spawn location the player should be teleported to. This value can be ignored.
	 * @return          True if the angle should be incremented, false otherwise.
	 */
	@Override
	protected boolean playerStartTeleport(@NotNull Player player, Location to) {
		if (this.isSpeedrunner(player.getUniqueId())) {
			to = this.getOverworld().getSpawnLocation();
			return !super.playerStartTeleport(player, to);
		} else {
			player.getInventory().setItem(8, ItemUtil.createPlayerTracker(ItemUtil.MANHUNT_PLAYER_TRACKER));
			return super.playerStartTeleport(player, to);
		}
	}

	@Override
	public void startTeleport() {
		Player runner = this.getPlayerSpeedrunner();
		if (runner == null) {
			Bukkit.broadcastMessage(ChatColor.RED + "An error occurred while retrieving the speedrunner. Contact an administrator if this occurs.");
			this.endMinigame(true);
		} else {
			super.startTeleport();
		}
	}

	/**
	 * Starts the countdown until the {@link AbstractMinigame#startTeleport()} method
	 * is called to teleport players to the newly generated worlds.
	 *
	 * @param players   The players in the Manhunt.
	 */
	@Override
	public void startCountdown(@NotNull List<UUID> players) {
		super.startCountdown(players);
		speedrunner = players.remove(this.random.nextInt(players.size()));
		this.hunters.addAll(players);
	}

	/**
	 * Clears stored cache in the Manhunt.
	 */
	@Override
	public void unload() {
		super.unload();
		this.hunters.clear();
		this.speedrunner = null;
	}

	@Override
	public void removePlayer(@NotNull Player player) {
		super.removePlayer(player);
		if (this.isSpeedrunner(player.getUniqueId()) && this.status.isInProgress()) {
			this.endMinigame(false, false);
		}

		if (this.hunters.remove(player.getUniqueId()) && this.status.isInProgress() && this.hunters.isEmpty()) {
			this.endMinigame(true, false);
		}
	}

	@Override
	public void cancelCountdown() {
		super.cancelCountdown();
		this.speedrunner = null;
		this.hunters.clear();
	}

	/**
	 * Returns true if the player is the speedrunner, false otherwise.
	 *
	 * @param uniqueId  The UUID of the player.
	 * @return          True if the player is the speedrunner, false otherwise.
	 */
	public boolean isSpeedrunner(@NotNull UUID uniqueId) {
		return uniqueId.equals(this.speedrunner);
	}

	/**
	 * Returns true if the player is a hunter, false otherwise.
	 *
	 * @param uniqueId  The UUID of the player.
	 * @return          True if the player is a hunter, false otherwise.
	 */
	public boolean isHunter(UUID uniqueId) {
		return this.hunters.contains(uniqueId);
	}
}
