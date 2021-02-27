package net.craftgalaxy.lobbycore;

import net.craftgalaxy.minigameservice.packet.impl.server.PacketPlayInDispatchCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class NPCPlayCommand implements CommandExecutor {

	private final LobbyCore plugin;

	public NPCPlayCommand(LobbyCore plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + "This command can only be run through console.");
			return true;
		}

		if (args.length == 2) {
			String[] subargs = args[1].trim().split("_");
			if (subargs.length != 2) {
				Bukkit.getLogger().info(ChatColor.RED + "Failed to parse command sent by " + sender.getName() + ". Please specify the minigame name and the number of players.");
				return true;
			}

			String player = args[0];
			String minigame = subargs[0];
			int maxPlayers;
			try {
				maxPlayers = Integer.parseInt(subargs[1]);
			} catch (NumberFormatException e) {
				Bukkit.getLogger().info(ChatColor.RED + "Failed to parse command sent by " + sender.getName() + ". The number of players specified is not an integer.");
				return true;
			}

			this.plugin.sendPacket(new PacketPlayInDispatchCommand(player, minigame, maxPlayers));
		} else {
			Bukkit.getLogger().info(ChatColor.RED + "Failed to parse command sent by " + sender.getName() + ". Nothing has been done.");
		}

		return true;
	}
}
