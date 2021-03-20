package net.craftgalaxy.minigamecore.command;

import net.craftgalaxy.minigamecore.minigame.MinigameManager;
import net.craftgalaxy.minigameservice.bukkit.util.java.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class LeaveCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(commandSender instanceof Player)) {
			commandSender.sendMessage(StringUtil.PLAYER_ONLY);
			return true;
		}

		Player sender = (Player) commandSender;
		if (args.length == 0) {
			try {
				MinigameManager.getInstance().sendBungeeLobby(sender);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			sender.sendMessage(ChatColor.RED + "To leave a minigame, type /leave.");
		}

		return true;
	}
}
