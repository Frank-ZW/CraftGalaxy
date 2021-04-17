package net.craftgalaxy.minigameservice.bukkit.minigame;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.craftgalaxy.minigameservice.bukkit.util.PlayerUtil;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public abstract class SurvivalMinigame extends AbstractMinigame {

	protected Map<World.Environment, World> worlds = new Object2ObjectOpenHashMap<>();
	protected Map<UUID, Set<Advancement>> advancements = new Object2ObjectOpenHashMap<>();
	protected boolean netherAccess;
	protected boolean endAccess;

	public SurvivalMinigame(String name, int gameKey, Location lobby) {
		this(name, gameKey, lobby, true, true);
	}

	public SurvivalMinigame(String name, int gameKey, Location lobby, boolean netherAccess, boolean endAccess) {
		super(name, gameKey, lobby);
		this.netherAccess = netherAccess;
		this.endAccess = endAccess;
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
	 * Removes all awarded advancements during the game. This is much faster than
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
					player.teleport(this.lobby);
					player.sendMessage(ChatColor.RED + "You were unexpectedly in the world " + world.getName() + " while it was being deleted. You have been teleported back to the lobby.");
				}
			}

			Bukkit.unloadWorld(world, false);
			FileUtils.deleteDirectory(world.getWorldFolder());
		}
	}

	@Override
	protected boolean playerStartTeleport(@NotNull Player player, Location to) {
		player.teleportAsync(to).thenAccept(result -> {
			if (result) {
				player.sendMessage(this.startMessage(player.getUniqueId()));
			} else {
				player.sendMessage(ChatColor.RED + "Failed to teleport you to the " + this.getName() + " world. Contact an administrator if this occurs.");
			}
		});
		return true;
	}

	@Override
	protected void playerEndTeleport(@NotNull Player player) {
		if (this.isSpectator(player.getUniqueId())) {
			this.showSpectator(player);
			PlayerUtil.unsetSpectator(player);
		} else {
			this.clearAwardedAdvancements(player);
			PlayerUtil.resetAttributes(player);
		}

		player.teleport(this.lobby);
	}

	@Override
	public void startTeleport() {
		this.status = MinigameStatus.IN_PROGRESS;
		this.getOverworld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		int radius = Math.max(8, 3 * this.players.size());
		float theta = -90.0F;
		float delta = 360.0F / this.players.size();
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

			int x = (int) (this.getOverworld().getSpawnLocation().getX() + radius * Math.cos(Math.toRadians(theta)));
			int z = (int) (this.getOverworld().getSpawnLocation().getZ() + radius * Math.sin(Math.toRadians(theta)));
			int y = this.getOverworld().getHighestBlockYAt(x, z) + 1;
			Location location = new Location(this.getOverworld(), x, y, z);
			if (this.playerStartTeleport(player, location)) {
				theta += delta;
			}
		}

		this.startTimestamp = System.currentTimeMillis();
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

			this.playerEndTeleport(player);
		}

		this.status = MinigameStatus.WAITING;
	}

	@Override
	public void cancelCountdown() {
		super.cancelCountdown();
		this.advancements.clear();
	}

	@Override
	public void handlePlayerEvent(@NotNull Event event) {
		if (event instanceof PlayerPortalEvent) {
			PlayerPortalEvent e = (PlayerPortalEvent) event;
			Player player = e.getPlayer();
			World fromWorld = e.getFrom().getWorld();
			if (e.getTo().getWorld() == null || e.getFrom().getWorld() == null || e.isCancelled()) {
				return;
			}

			switch (e.getCause()) {
				case NETHER_PORTAL:
					switch (fromWorld.getEnvironment()) {
						case NORMAL:
							e.getTo().setWorld(this.getNether());
							if (this.players.contains(player.getUniqueId()) && !this.spectators.contains(player.getUniqueId())) {
								this.plugin.grantNetherAdvancement(player);
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
								this.plugin.grantEndAdvancement(player);
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
		} else if (event instanceof PortalCreateEvent) {
			PortalCreateEvent e = (PortalCreateEvent) event;
			switch (e.getReason()) {
				case FIRE:
				case NETHER_PAIR:
					if (!this.netherAccess) {
						e.setCancelled(true);
					}

					break;
				default:
					if (!this.endAccess) {
						e.setCancelled(true);
					}
			}
		}
	}

	@Override
	public boolean worldsLoaded() {
		return !this.worlds.isEmpty();
	}

	/**
	 * @return  The world for the minigame representing the normal world.
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

	@Override
	public void unload() {
		super.unload();
		this.worlds.clear();
		this.advancements.clear();
	}
}
