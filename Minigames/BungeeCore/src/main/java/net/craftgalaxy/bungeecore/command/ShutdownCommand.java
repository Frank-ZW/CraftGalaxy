package net.craftgalaxy.bungeecore.command;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.craftgalaxy.bungeecore.runnable.ShutdownRunnable;
import net.craftgalaxy.minigameservice.bungee.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class ShutdownCommand extends Command {

	private final BungeeCore plugin;
	private ScheduledTask shutdownTask;

	public ShutdownCommand() {
		super("shutdown");
		this.plugin = BungeeCore.getInstance();
		this.shutdownTask = null;
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (!commandSender.hasPermission(StringUtil.SHUTDOWN_COMMAND)) {
			commandSender.sendMessage(new TextComponent(StringUtil.INSUFFICIENT_PERMISSION));
			return;
		}

		switch (args.length) {
			case 0:
				if (!(commandSender instanceof ProxiedPlayer)) {
					commandSender.sendMessage(new TextComponent(ChatColor.RED + "You must specify the server or proxy to shutdown."));
					return;
				}

				if (!this.startShutdownRunnable()) {
					commandSender.sendMessage(new TextComponent(ChatColor.RED + "There is already a shutdown task running. To view its status, type /shutdown status and to cancel the current shutdown task, type /shutdown cancel."));
				}

				break;
			case 1:
				if (args[0].equalsIgnoreCase("all")) {
					if (!commandSender.hasPermission(StringUtil.SHUTDOWN_ALL_COMMAND)) {
						commandSender.sendMessage(new TextComponent(StringUtil.INSUFFICIENT_PERMISSION));
						return;
					}

					this.startShutdownRunnable();
				} else {

				}

				break;
			case 2:
				break;
			default:
		}
	}

	public boolean startShutdownRunnable() {
		return this.startShutdownRunnable(1800);
	}

	public boolean startShutdownRunnable(int seconds) {
		if (this.shutdownTask == null) {
			this.shutdownTask = this.plugin.getProxy().getScheduler().schedule(this.plugin, new ShutdownRunnable(seconds), 1L, 1L, TimeUnit.SECONDS);
			return true;
		}

		return false;
	}
}
