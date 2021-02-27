package net.craftgalaxy.mavic.commands;

import net.craftgalaxy.mavic.commands.impl.DisableCheckCommand;
import net.craftgalaxy.mavic.commands.impl.EnableCheckCommand;
import net.craftgalaxy.mavic.commands.impl.ToggleCommand;
import net.craftgalaxy.mavic.util.java.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class MavicCommand implements CommandExecutor {

	private final Map<String, AbstractMavicCommand> commands = Map.of("toggle", new ToggleCommand(), "enable", new EnableCheckCommand(), "disable", new DisableCheckCommand());

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "This command does not exist.");
			return true;
		}

		switch (StringUtils.lowerCase(args[0])) {
			case "toggle":
				this.commands.get("toggle").accept(sender, StringUtil.TOGGLE_ALERT_PERMISSION, args, true);
				break;
			case "enable":
				this.commands.get("enable").accept(sender, StringUtil.ENABLE_CHECK_PERMISSION, args, false);
				break;
			case "disable":
				this.commands.get("disable").accept(sender, StringUtil.DISABLE_CHECK_PERMISSION, args, false);
			default:
				sender.sendMessage(ChatColor.RED + "This command does not exist.");
		}

		return true;
	}
}
