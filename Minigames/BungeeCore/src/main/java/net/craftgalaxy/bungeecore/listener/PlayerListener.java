package net.craftgalaxy.bungeecore.listener;

import net.craftgalaxy.bungeecore.data.manager.PlayerManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class PlayerListener implements Listener {

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		PlayerManager.getInstance().executor(() -> PlayerManager.getInstance().addPlayer(e.getPlayer()));
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		PlayerManager.getInstance().executor(() -> PlayerManager.getInstance().removePlayer(e.getPlayer()));
	}
}
