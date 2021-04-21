package net.craftgalaxy.mavic.commands.impl;

import net.craftgalaxy.mavic.commands.AbstractMavicCommand;
import net.craftgalaxy.mavic.util.java.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class DisableCheckCommand extends AbstractMavicCommand {

	@Override
	protected void onCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission(StringUtil.DISABLE_CHECK_PERMISSION)) {
			sender.sendMessage(StringUtil.INSUFFICIENT_PERMISSION);
			return;
		}

		switch (args.length) {
			case 1:
				break;
			case 2:
				break;
			default:
				sender.sendMessage(ChatColor.RED + "To disable a check, type /check disable <name>");
		}
	}
}
