package net.craftgalaxy.asyncwilderness.runnable.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.craftgalaxy.asyncwilderness.AsyncWilderness;
import net.craftgalaxy.asyncwilderness.runnable.TeleportSupplier;
import net.craftgalaxy.asyncwilderness.util.StringUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;

public final class TeleportManager {

	private final AsyncWilderness plugin;
	private final long defaultDuration;
	private final long donatorDuration;
	private final Map<UUID, Long> timestamps = new HashMap<>();
	private final Map<UUID, CompletableFuture<Void>> completableFutures = new HashMap<>();
	private final ExecutorService executors = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("AsyncWilderness Callable Executor").build());
	private static TeleportManager instance;

	public TeleportManager(AsyncWilderness plugin, long defaultDuration, long donatorDuration) {
		this.defaultDuration = defaultDuration;
		this.donatorDuration = donatorDuration;
		this.plugin = plugin;
	}

	public static void enable(AsyncWilderness plugin, long defaultDuration, long donatorDuration) {
		instance = new TeleportManager(plugin, defaultDuration, donatorDuration);
	}

	public static void disable() {
		if (instance != null) {
			try {
				if (instance.executors.submit(() -> {
					Iterator<Map.Entry<UUID, CompletableFuture<Void>>> iterator = instance.completableFutures.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<UUID, CompletableFuture<Void>> entry = iterator.next();
						entry.getValue().cancel(true);
						Player player = Bukkit.getPlayer(entry.getKey());
						if (player != null) {
							player.sendMessage(ChatColor.RED + "Your wilderness teleportation task was cancelled.");
						}

						iterator.remove();
					}
				}, true).get(6L, TimeUnit.SECONDS)) {
					Bukkit.getLogger().info(ChatColor.GREEN + "Cleared any pending or active asynchronous teleportation tasks from the cache.");
				}
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
			}

			instance.executors.shutdownNow();
			instance.completableFutures.clear();
			instance.timestamps.clear();
			instance = null;
		}
	}

	public static TeleportManager getInstance() {
		return instance;
	}

	public void addTeleportRunnable(@NotNull Player sender) {
		Long timestamp = this.timestamps.remove(sender.getUniqueId());
		if (timestamp != null) {
			long secondsLeft = (sender.hasPermission(StringUtil.DONATOR_COOLDOWN_PERMISSION) ? this.donatorDuration : this.defaultDuration) - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timestamp);
			if (secondsLeft > 0) {
				sender.sendMessage(StringUtil.PREFIX + ChatColor.RED + " You have " + secondsLeft + " seconds remaining before you can run this command.");
				this.timestamps.put(sender.getUniqueId(), timestamp);
				return;
			}
		}

		sender.sendMessage(ChatColor.GREEN + "Choosing a random location... please be patient, this may take a while.");
		this.timestamps.put(sender.getUniqueId(), System.currentTimeMillis());
		this.completableFutures.put(sender.getUniqueId(), CompletableFuture.supplyAsync(new TeleportSupplier(this.plugin), this.executors).thenAccept(location -> {
			if (location == null) {
				sender.sendMessage(ChatColor.RED + "Failed to find you a random wilderness location. This shouldn't ever happen, but if it does, contact an administrator ugently.");
			} else {
				Bukkit.getScheduler().runTask(this.plugin, () -> {
					Location teleport = location.clone().add(0.5D, 1.5D, 0.5D);
					sender.teleportAsync(teleport).thenAccept(result -> {
						if (result) {
							if (this.plugin.isSpawnParticles()) {
								teleport.getWorld().spawnParticle(Particle.PORTAL, teleport, 250);
							}

							if (this.plugin.isPlayEndermanEffect()) {
								sender.playSound(teleport, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75F, 0.75F);
							}

							sender.sendMessage(ChatColor.GREEN + "You have been teleported to (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
						}
					});

					CompletableFuture<Void> future = this.completableFutures.remove(sender.getUniqueId());
					if (future != null) {
						future.join();
					}
				});
			}
		}));
	}
}
