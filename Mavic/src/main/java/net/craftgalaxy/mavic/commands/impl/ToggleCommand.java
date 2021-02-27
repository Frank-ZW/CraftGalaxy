package net.craftgalaxy.mavic.commands.impl;

import net.craftgalaxy.mavic.commands.AbstractMavicCommand;
import net.craftgalaxy.mavic.data.PlayerData;
import net.craftgalaxy.mavic.data.manager.PlayerManager;
import net.craftgalaxy.mavic.util.java.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ToggleCommand extends AbstractMavicCommand {

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
		if (playerData == null) {
			player.sendMessage(StringUtil.ERROR_GETTING_PLAYERDATA);
			return;
		}

		if (args.length == 1) {
			if (playerData.isReceiveAlerts()) {
				player.sendMessage(ChatColor.RED + "You will no longer receive alerts from the anticheat.");
				playerData.setReceiveAlerts(false);
			} else {
				player.sendMessage(ChatColor.GREEN + "You will now receive alerts from the anticheat.");
				playerData.setReceiveAlerts(true);
			}
		} else {
			StringBuilder check = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				check.append(args[i]).append(" ");
			}

			String checkName = check.toString().trim();
			if (playerData.unignoreCheckAlerts(checkName)) {
				player.sendMessage(ChatColor.RED + "You will no longer receive alerts from check: " + checkName);
			} else {
				playerData.ignoreCheckAlerts(checkName);
				player.sendMessage(ChatColor.GREEN + "You will now receive alerts from check: " + checkName);
			}
		}
	}
}
