package net.craftgalaxy.bungeecore.runnable;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.List;

public class ShutdownRunnable implements Runnable {

	private final BungeeCore plugin;
	private int secondsLeft;
	private final List<Integer> broadcasts = Arrays.asList(21600, 10800, 3600, 1800, 900, 600, 300, 180, 120, 60, 45, 30, 15, 10, 5, 4, 3, 2, 1);

	public ShutdownRunnable(int secondsLeft) {
		this.plugin = BungeeCore.getInstance();
		this.secondsLeft = secondsLeft;
	}

	@Override
	public void run() {
		if (this.broadcasts.contains(this.secondsLeft)) {
			this.plugin.getProxy().broadcast(new TextComponent(ChatColor.RED + "The proxy will shutdown in " + this.secondsLeft + " seconds" + (this.secondsLeft == 1 ? "" : "s" + ".")));
		}

		if (this.secondsLeft <= 0) {
			this.plugin.getProxy().broadcast(new TextComponent(ChatColor.RED + "The proxy has shutdown."));

			return;
		}

		this.secondsLeft--;
	}
}
