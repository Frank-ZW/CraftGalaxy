package net.craftgalaxy.mavic.listener;

import net.craftgalaxy.mavic.Mavic;
import net.craftgalaxy.mavic.check.Check;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.data.manager.PlayerManager;
import net.craftgalaxy.mavic.event.PlayerAlertEvent;
import net.craftgalaxy.mavic.event.PlayerBanEvent;
import net.craftgalaxy.mavic.util.java.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class AlertListener implements Listener {

	private final Mavic plugin;

	public AlertListener(Mavic plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerAlert(PlayerAlertEvent e) {
		Player player = e.getPlayer();
		Check check = e.getCheck();
		String alert = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "Mavic" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + player.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.YELLOW + e.getPing() + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " is suspected of using " + ChatColor.RED + check.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.RED + e.getVl() + ChatColor.DARK_GRAY + "]";
		Bukkit.getConsoleSender().sendMessage(alert);
		Bukkit.getOnlinePlayers().parallelStream().filter(other -> {
			PlayerData otherData = PlayerManager.getInstance().getPlayerData(other);
			return other.hasPermission(StringUtil.NOTIFY_ALERT_PERMISSION) && otherData != null && otherData.isReceiveAlerts(check.getName());
		}).forEach(other -> other.sendMessage(alert));
	}

	@EventHandler
	public void onPlayerBan(PlayerBanEvent e) {
		this.runOnMainThread(() -> {
			Player player = e.getPlayer();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " " + ChatColor.RED + "Unfair advantage");
		});
	}

	public void runOnMainThread(Runnable runnable) {
		Bukkit.getScheduler().runTask(this.plugin, runnable);
	}
}
