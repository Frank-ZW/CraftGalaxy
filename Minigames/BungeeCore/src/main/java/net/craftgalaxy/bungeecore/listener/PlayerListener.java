package net.craftgalaxy.bungeecore.listener;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.data.PlayerData;
import net.craftgalaxy.bungeecore.data.ServerSocketData;
import net.craftgalaxy.bungeecore.data.manager.PlayerManager;
import net.craftgalaxy.bungeecore.data.manager.ServerManager;
import net.md_5.bungee.api.ChatColor;
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
		ServerInfo server = PlayerManager.getInstance().removeDisconnection(player.getUniqueId());
		PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
		if (server != null && playerData != null) {
			e.setTarget(server);
			playerData.setPlayerStatus(PlayerData.PlayerStatus.PLAYING);
			this.plugin.getLogger().info(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Player Manager" + ChatColor.DARK_GRAY + "] " + ChatColor.GREEN + " Redirecting " + player.getName() + " to " + server.getName());
		}
	}

	@EventHandler
	public void onServerSwitch(ServerSwitchEvent e) {
		ServerSocketData serverData = ServerManager.getInstance().getServerData(e.getFrom());
		if (serverData != null) {
			serverData.handleServerSwitch(e.getPlayer());
		}
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		PlayerManager.getInstance().addPlayer(e.getPlayer());
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		PlayerManager.getInstance().removePlayer(e.getPlayer());
	}
}
