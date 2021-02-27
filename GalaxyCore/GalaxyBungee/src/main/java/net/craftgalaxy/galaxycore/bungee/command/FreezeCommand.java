package net.craftgalaxy.galaxycore.bungee.command;

import net.craftgalaxy.corepackets.client.CPacketPlayerFreeze;
import net.craftgalaxy.galaxycore.bungee.BungeePlugin;
import net.craftgalaxy.galaxycore.bungee.data.PlayerData;
import net.craftgalaxy.galaxycore.bungee.data.manager.PlayerManager;
import net.craftgalaxy.galaxycore.bungee.data.manager.ServerManager;
import net.craftgalaxy.galaxycore.bungee.util.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class FreezeCommand extends Command {

	private final BungeePlugin plugin;

	public FreezeCommand(BungeePlugin plugin) {
		super("freeze", StringUtil.FREEZE_COMMAND_PERMISSION);
		this.plugin = plugin;
		this.setPermissionMessage(StringUtil.INSUFFICIENT_PERMISSION);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "To freeze a player, type /freeze <player>"));
			return;
		}

		ProxiedPlayer target = this.plugin.getProxy().getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "The specified player is not online."));
			return;
		}

		ServerInfo server = target.getServer().getInfo();
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if (target.hasPermission(StringUtil.AUTHENTICATE_PERMISSION)) {
				player.sendMessage(new TextComponent(ChatColor.RED + "This player can only be frozen through console."));
				return;
			}

			if (!player.hasPermission(StringUtil.FREEZE_GLOBAL_PERMISSION) && !server.equals(player.getServer().getInfo())) {
				player.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to freeze players on other servers."));
				return;
			}
		}

		PlayerData targetData = PlayerManager.getInstance().getPlayerData(target.getUniqueId());
		if (targetData == null) {
			sender.sendMessage(new TextComponent(ChatColor.RED + target.getName() + " does not have a player data profile. Contact the developer if this occurs."));
			return;
		}

		ServerManager.getInstance().sendPacketToServer(server.getName(), new CPacketPlayerFreeze(target.getUniqueId(), target.hasPermission(StringUtil.AUTHENTICATE_PERMISSION), false));
		sender.sendMessage(new TextComponent(ChatColor.GREEN + target.getName() + " has been frozen."));
	}
}
