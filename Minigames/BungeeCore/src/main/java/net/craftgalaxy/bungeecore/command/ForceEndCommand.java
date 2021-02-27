package net.craftgalaxy.bungeecore.command;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.data.manager.ServerManager;
import net.craftgalaxy.minigameservice.bungee.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

public final class ForceEndCommand extends Command {

	private final BungeeCore plugin;

	public ForceEndCommand(BungeeCore plugin) {
		super("forceend", StringUtil.FORCE_END_COMMAND);
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission(StringUtil.FORCE_END_COMMAND)) {
			sender.sendMessage(new TextComponent(StringUtil.INSUFFICIENT_PERMISSION));
			return;
		}

		if (args.length == 1) {
			try {
				int gameKey = Integer.parseInt(args[0]);
				if (ServerManager.getInstance().forceEnd(gameKey)) {
					sender.sendMessage(new TextComponent(ChatColor.GREEN + "The minigame with a game key of " + gameKey + " has been forcibly ended."));
				} else {
					sender.sendMessage(new TextComponent(ChatColor.RED + "There are no servers hosting a minigame with a game key of " + gameKey + "."));
				}
			} catch (NumberFormatException e) {
				ServerInfo target = this.plugin.getProxy().getServerInfo(args[0]);
				if (target == null) {
					sender.sendMessage(new TextComponent(ChatColor.RED + "That server does not exist."));
					return;
				}

				if (ServerManager.getInstance().forceEnd(target)) {
					sender.sendMessage(new TextComponent(ChatColor.GREEN + "If there was a minigame on " + target.getName() + ", it has been forcibly ended."));
				} else {
					sender.sendMessage(new TextComponent(ChatColor.RED + target.getName() + " is not a minigame server."));
				}
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "To forcibly end a minigame, type /forceend <game key> or /forceend <server>."));
		}
	}
}
