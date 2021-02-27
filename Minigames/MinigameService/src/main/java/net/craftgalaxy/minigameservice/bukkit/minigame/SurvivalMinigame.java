package net.craftgalaxy.minigameservice.bukkit.minigame;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public abstract class SurvivalMinigame extends AbstractMinigame {

	protected Map<World.Environment, World> worlds = new Object2ObjectOpenHashMap<>();
	protected Map<UUID, Set<Advancement>> advancements = new Object2ObjectOpenHashMap<>();

	public SurvivalMinigame(String name, int gameKey, Location lobby) {
		super(name, gameKey, lobby);
	}

	/**
	 * Logs advancements players have made during the minigame. The server uses the list to
	 * revoke the awarded advancements at the end of the minigame. This is much faster than
	 * looping through every available advancement.
	 *
	 * @param player        The player the advancement was awarded to.
	 * @param advancement   The advancement to be awarded.
	 */
	public void addAwardedAdvancement(@NotNull Player player, @NotNull Advancement advancement) {
		Set<Advancement> advancements = this.advancements.get(player.getUniqueId());
		if (advancements == null) {
			advancements = new HashSet<>();
		}

		advancements.add(advancement);
		this.advancements.put(player.getUniqueId(), advancements);
	}

	/**
	 * Removes all awarded advancements during the minigame. This is much faster than
	 * looping through every available advancement and revoking each one individually.
	 *
	 * @param player    The player to revoke all advancements.
	 */
	public void clearAwardedAdvancements(@NotNull Player player) {
		Set<Advancement> advancements = this.advancements.remove(player.getUniqueId());
		if (advancements != null) {
			for (Advancement advancement : advancements) {
				AdvancementProgress progress = player.getAdvancementProgress(advancement);
				for (String criteria : progress.getAwardedCriteria()) {
					progress.revokeCriteria(criteria);
				}
			}
		}
	}

	@Override
	public void deleteWorlds() throws IOException {
		for (World world : this.worlds.values()) {
			if (world.getPlayerCount() != 0) {
				List<Player> players = world.getPlayers();
				for (Player player : players) {
					player.teleportAsync(this.lobby).thenAccept(result -> {
						if (result) {
							player.sendMessage(ChatColor.RED + "You were unexpectedly in the world " + world.getName() + " while it was being deleted. You have been teleported back to the lobby.");
						} else {
							player.sendMessage(ChatColor.RED + "Failed to teleport you back to the lobby spawn. Contact an administrator if this occurs.");
						}
					});
				}
			}

			Bukkit.unloadWorld(world, false);
			FileUtils.deleteDirectory(world.getWorldFolder());
		}

		this.worlds.clear();
		this.spectators.clear();
		this.advancements.clear();
		this.players.clear();
		this.gameKey = Integer.MIN_VALUE;
	}

	@Override
	public void endTeleport() {
		for (UUID uniqueId : this.players) {
			Player player = Bukkit.getPlayer(uniqueId);
			if (player == null || !player.isOnline()) {
				continue;
			}

			if (player.isDead()) {
				player.spigot().respawn();
			}

			if (this.isSpectator(uniqueId)) {
				PlayerUtil.unsetSpectator(player);
			} else {
				this.clearAwardedAdvancements(player);
				PlayerUtil.resetAttributes(player);
			}

			player.teleportAsync(this.lobby).thenAccept(result -> {
				if (!result) {
					player.sendMessage(ChatColor.RED + "Failed to teleport you back to the lobby. Contact an administrator if this occurs.");
					Bukkit.getLogger().warning("Failed to teleport " + player.getName() + " to the lobby.");
				}
			});
		}

		this.setWaiting();
	}

	/**
	 * @return  The world for the minigame representing the Overworld.
	 */
	public World getOverworld() {
		return this.worlds.get(World.Environment.NORMAL);
	}

	/**
	 * Returns the linked nether world for the minigame. Certain minigames have nether world
	 * access disabled by default. As a result, the return value will be null in those
	 * circumstances.
	 *
	 * @return  The world for the minigame representing the Nether.
	 */
	public World getNether() {
		return this.worlds.get(World.Environment.NETHER);
	}

	/**
	 * Returns the linked end world for the minigame. Certain minigames have end world
	 * access disabled by default. As a result, the return value will be null in those
	 * circumstances.
	 *
	 * @return  The world for the minigame representing the End.
	 */
	public World getEnd() {
		return this.worlds.get(World.Environment.THE_END);
	}
}
