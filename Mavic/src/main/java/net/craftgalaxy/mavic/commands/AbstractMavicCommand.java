package net.craftgalaxy.mavic.commands;

import net.craftgalaxy.mavic.util.java.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMavicCommand {

	public void accept(CommandSender sender, @Nullable String permission, String[] args, boolean playerOnly) {
		if (permission != null && !sender.hasPermission(permission)) {
			sender.sendMessage(StringUtil.INSUFFICIENT_PERMISSION);
			return;
		}

		if (playerOnly && !(sender instanceof Player)) {
			sender.sendMessage(StringUtil.PLAYER_ONLY);
			return;
		}

		this.onCommand(sender, args);
	}

	protected abstract void onCommand(CommandSender sender, String[] args);
}
