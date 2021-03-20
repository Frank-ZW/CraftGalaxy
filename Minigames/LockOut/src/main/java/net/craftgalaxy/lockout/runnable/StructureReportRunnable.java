package net.craftgalaxy.lockout.runnable;

import net.craftgalaxy.lockout.minigame.LockOut;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class StructureReportRunnable extends BukkitRunnable {

	private final LockOut lockOut;

	public StructureReportRunnable(LockOut lockOut) {
		this.lockOut = lockOut;
	}

	@Override
	public void run() {
		for (UUID uniqueId : this.lockOut.getPlayers()) {
			Player player = Bukkit.getPlayer(uniqueId);
			if (player == null || this.lockOut.isSpectator(uniqueId)) {
				continue;
			}

			if (this.lockOut.checkStructureChallenges(player)) {
				this.cancel();
			}
		}
	}
}
