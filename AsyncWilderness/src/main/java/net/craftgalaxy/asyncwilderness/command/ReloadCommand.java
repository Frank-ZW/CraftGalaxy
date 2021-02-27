package net.craftgalaxy.asyncwilderness.command;

import net.craftgalaxy.asyncwilderness.AsyncWilderness;
import net.craftgalaxy.asyncwilderness.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

	private final AsyncWilderness plugin;

	public ReloadCommand(AsyncWilderness plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!sender.hasPermission(StringUtil.RELOAD_PERMISSION)) {
			sender.sendMessage(StringUtil.INSUFFICIENT_PERMISSION);
			return true;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			sender.sendMessage(ChatColor.GREEN + "AsyncWilderness has been reloaded.");
			this.plugin.readConfig(false);
		} else {
			sender.sendMessage(ChatColor.RED + "To reload AsyncWilderness, type /asyncwild reload.");
		}

		return true;
	}
}
