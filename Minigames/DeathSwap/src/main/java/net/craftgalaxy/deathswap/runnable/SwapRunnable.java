package net.craftgalaxy.deathswap.runnable;

import com.google.common.collect.ImmutableSet;
import net.craftgalaxy.deathswap.minigame.DeathSwap;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public final class SwapRunnable extends BukkitRunnable {

	private final DeathSwap deathSwap;
	private final Set<Integer> countdowns = ImmutableSet.of(10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
	private int secondsRemaining;

	public SwapRunnable(DeathSwap deathSwap) {
		this.deathSwap = deathSwap;
		this.secondsRemaining = 300;
	}

	@Override
	public void run() {
		if (this.countdowns.contains(this.secondsRemaining)) {
			this.deathSwap.broadcast(ChatColor.RED + "Swapping in " + this.secondsRemaining + " second" + (this.secondsRemaining == 1 ? "" : "s") + "!");
		}

		if (this.secondsRemaining <= 0) {
			this.deathSwap.swapPlayers();
			this.secondsRemaining = 300;
		} else {
			this.secondsRemaining--;
		}
	}
}
