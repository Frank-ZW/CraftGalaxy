package net.craftgalaxy.minigameservice.bukkit.runnable;

import com.google.common.collect.ImmutableSet;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameStartEvent;
import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class CountdownRunnable extends BukkitRunnable {

	private final AbstractMinigame minigame;
	private int time;
	private final Set<Integer> countdowns = ImmutableSet.of(15, 10, 5, 4, 3, 2, 1);

	public CountdownRunnable(AbstractMinigame minigame) {
		this.minigame = minigame;
		this.time = 15;
	}

	@Override
	public void run() {
		if (this.countdowns.contains(this.time)) {
			this.minigame.broadcastTitleAndEffect(this.translateColor(this.time), Effect.CLICK2);
		}

		if (this.time <= 0) {
			Bukkit.getPluginManager().callEvent(new MinigameStartEvent(this.minigame, this.minigame.getPlayers()));
			this.minigame.startTeleport();
			this.cancel();
			return;
		}

		this.time--;
	}

	public String translateColor(int time) {
		if (time > 4) {
			return ChatColor.GREEN.toString() + time;
		} else if (time > 2) {
			return ChatColor.GOLD.toString() + time;
		} else {
			return ChatColor.RED.toString() + time;
		}
	}
}
