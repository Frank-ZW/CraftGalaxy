package net.craftgalaxy.galaxycore.bungee.listener;

import net.craftgalaxy.galaxycore.bungee.BungeePlugin;
import net.craftgalaxy.galaxycore.bungee.data.manager.PlayerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public final class PlayerListener implements Listener {

	private final BungeePlugin plugin;

	public PlayerListener(BungeePlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPreLogin(PreLoginEvent e) {
		if (this.plugin.isSetup()) {
			PlayerManager.getInstance().addPlayer(e.getConnection());
		} else {
			e.setCancelled(true);
			e.setCancelReason(new ComponentBuilder().append("Please wait for the Bungee Core plugin to finish loading up.").color(ChatColor.RED).create());
		}
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		PlayerManager.getInstance().onPlayerConnect(e.getPlayer());
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		PlayerManager.getInstance().removePlayer(e.getPlayer(), true);
	}
}
