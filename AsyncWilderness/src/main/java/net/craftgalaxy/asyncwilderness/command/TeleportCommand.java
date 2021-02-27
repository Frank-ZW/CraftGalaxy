package net.craftgalaxy.asyncwilderness.command;

import net.craftgalaxy.asyncwilderness.AsyncWilderness;
import net.craftgalaxy.asyncwilderness.runnable.manager.TeleportManager;
import net.craftgalaxy.asyncwilderness.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class TeleportCommand implements CommandExecutor {

	private final AsyncWilderness plugin;

	public TeleportCommand(AsyncWilderness plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(commandSender instanceof Player)) {
			commandSender.sendMessage(StringUtil.PLAYER_ONLY);
			return true;
		}

		Player sender = (Player) commandSender;
		long duration = this.plugin.getInitialTeleportDuration() - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - sender.getFirstPlayed());
		if (duration <= 0 && !sender.hasPermission(StringUtil.WILDERNESS_PERMISSION) && !sender.hasPermission(StringUtil.BYPASS_PERMISSION)) {
			sender.sendMessage(ChatColor.RED + "You no longer have access to /rtp.");
			return true;
		}

		if (args.length == 0) {
			TeleportManager.getInstance().addTeleportRunnable(sender);
		} else {
			sender.sendMessage(ChatColor.RED + "To randomly teleport across the world, type /rtp.");
		}
		return true;
	}
}
