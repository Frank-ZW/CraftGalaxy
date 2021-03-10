package net.craftgalaxy.bungeecore.command;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.data.PlayerData;
import net.craftgalaxy.bungeecore.data.manager.PlayerManager;
import net.craftgalaxy.bungeecore.data.manager.ServerManager;
import net.craftgalaxy.minigameservice.bungee.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public final class PlayCommand extends Command {

	private final BungeeCore plugin;

	public PlayCommand(BungeeCore plugin) {
		super("play");
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (!(commandSender instanceof ProxiedPlayer)) {
			commandSender.sendMessage(new TextComponent(StringUtil.PLAYER_ONLY));
			return;
		}

		ProxiedPlayer sender = (ProxiedPlayer) commandSender;
		ServerInfo fromServer = sender.getServer().getInfo();
		if (this.plugin.isSpecializedServer(fromServer.getName()) || this.plugin.isMinigameServer(fromServer.getName())) {
			return;
		}

		PlayerData senderData = PlayerManager.getInstance().getPlayerData(sender);
		if (senderData == null) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "An error occurred while retrieving your player profile. Contact an administrator if this occurs."));
			return;
		}

		if (senderData.isPlaying()) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You cannot run this command in a minigame."));
		} else if (senderData.isSpectating()) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You cannot run this command while spectating."));
		} else {
			senderData.setPlayerStatus(PlayerData.PlayerStatus.QUEUING);
			if (args.length == 1) {
				String[] subargs = args[0].trim().toLowerCase().split("_");
				if (subargs.length != 2) {
					sender.sendMessage(new TextComponent(ChatColor.RED + "You must specify the name of the minigame and the total number of players."));
					return;
				}

				int maxPlayers;
				try {
					maxPlayers = Integer.parseInt(subargs[1]);
				} catch (NumberFormatException e) {
					sender.sendMessage(new TextComponent(ChatColor.RED + "The number of players entered must be a whole number."));
					return;
				}

				ServerManager.getInstance().queuePlayer(senderData, subargs[0], maxPlayers);
			} else {
				sender.sendMessage(new TextComponent(ChatColor.RED + "To play a minigame, type /play <minigame name>_<number of players>."));
			}
		}
	}
}
