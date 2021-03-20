package net.craftgalaxy.minigamecore.runnable;

import net.craftgalaxy.minigamecore.MinigameCore;
import net.craftgalaxy.minigamecore.minigame.MinigameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public class SocketConnectionRunnable extends BukkitRunnable {

	private final MinigameManager manager;

	public SocketConnectionRunnable(MinigameManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {
		try {
			if (this.manager.attemptSocketConnection()) {
				Bukkit.getLogger().info(ChatColor.GREEN + "Established secure socket connection for " + MinigameCore.BUNGEE_SERVER_NAME + " on port " + MinigameCore.SOCKET_PORT_NUMBER);
				this.cancel();
			}
		} catch (IOException e) {
			Bukkit.getLogger().warning("Failed to establish TCP connection with the proxy. Another connection attempt will be made in 10 seconds.");
		}
	}
}
