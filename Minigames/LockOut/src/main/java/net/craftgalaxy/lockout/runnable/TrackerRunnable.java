package net.craftgalaxy.lockout.runnable;

import net.craftgalaxy.lockout.minigame.LockOut;
import net.craftgalaxy.lockout.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class TrackerRunnable extends BukkitRunnable {

	private final LockOut lockOut;
	private final Map<UUID, Team> players;

	public TrackerRunnable(LockOut lockOut) {
		this.lockOut = lockOut;
		this.players = lockOut.getTeams();
	}

	@Override
	public void run() {
		for (Map.Entry<UUID, Team> entry : this.players.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			if (player == null) {
				continue;
			}

			Team team = entry.getValue();
			Player target = team.getPlayerTracking();
			if (target != null) {
				player.sendActionBar(ChatColor.GREEN + "Currently tracking " + target.getName() + "'s latest location.");
				for (ItemStack item : player.getInventory().getContents()) {
					if (this.lockOut.isPlayerTracker(item)) {
						if (player.getWorld().equals(target.getWorld())) {
							CompassMeta meta = (CompassMeta) item.getItemMeta();
							meta.setLodestoneTracked(false);
							meta.setLodestone(target.getLocation());
							item.setItemMeta(meta);
						} else {
							player.sendActionBar(ChatColor.RED + "There are no players to track!");
						}

						break;
					}
				}
			}
		}
	}
}
