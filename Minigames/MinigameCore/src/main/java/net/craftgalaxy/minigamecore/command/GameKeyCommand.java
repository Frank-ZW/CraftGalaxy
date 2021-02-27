package net.craftgalaxy.minigamecore.command;

import net.craftgalaxy.minigamecore.minigame.MinigameManager;
import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class GameKeyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		AbstractMinigame minigame = MinigameManager.getInstance().getMinigame();
		if (minigame == null) {
			sender.sendMessage(ChatColor.RED + "This server does not have an active minigame running.");
			return true;
		}

		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (minigame.isSpectator(player.getUniqueId())) {
				player.sendMessage(ChatColor.GREEN + "The " + minigame.getName() + " you are spectating has a game key of " + minigame.getGameKey());
			} else {
				player.sendMessage(ChatColor.GREEN + "The game key of your " + minigame.getName() + " is " + minigame.getGameKey());
			}
		} else {
			sender.sendMessage(ChatColor.GREEN + "The game key for the " + minigame.getName() + " is " + minigame.getGameKey());
		}

		return true;
	}
}
