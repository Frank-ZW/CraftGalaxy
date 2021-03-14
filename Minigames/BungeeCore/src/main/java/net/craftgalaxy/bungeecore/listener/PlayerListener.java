package net.craftgalaxy.bungeecore.listener;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.data.PlayerData;
import net.craftgalaxy.bungeecore.data.manager.PlayerManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class PlayerListener implements Listener {

	private final BungeeCore plugin;

	public PlayerListener(BungeeCore plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent e) {
		if (this.plugin.isMinigameServer(e.getTarget().getName())) {
			return;
		}

		ProxiedPlayer player = e.getPlayer();
		ServerInfo server = PlayerManager.getInstance().getDisconnectionServer(player.getUniqueId());
		if (server == null) {
			return;
		}

		PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
		if (playerData != null) {
			e.setTarget(server);
			playerData.setPlayerStatus(PlayerData.PlayerStatus.PLAYING);
		}
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		PlayerManager.getInstance().executor(() -> PlayerManager.getInstance().addPlayer(e.getPlayer()));
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		PlayerManager.getInstance().executor(() -> PlayerManager.getInstance().removePlayer(e.getPlayer()));
	}
}
