package net.craftgalaxy.bungeecore.command;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.minigameservice.bungee.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public final class CoreCommand extends Command {

	private final BungeeCore plugin;

	public CoreCommand(BungeeCore plugin) {
		super("bungeecore", StringUtil.BUNGEECORE_COMMAND, "bcore", "core");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission(StringUtil.BUNGEECORE_COMMAND)) {
			sender.sendMessage(new TextComponent(StringUtil.INSUFFICIENT_PERMISSION));
			return;
		}

		if (args.length == 0) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "This command does not exist."));
			return;
		}

		switch (args[0].toLowerCase()) {
			case "reload":
			case "rl":
				sender.sendMessage(new TextComponent(ChatColor.GREEN + "BungeeCore " + this.plugin.getDescription().getVersion() + " has been reloaded."));
				this.plugin.readConfig(false);
				break;
			default:
				sender.sendMessage(new TextComponent(ChatColor.RED + "This command does not exist."));
		}
	}
}
