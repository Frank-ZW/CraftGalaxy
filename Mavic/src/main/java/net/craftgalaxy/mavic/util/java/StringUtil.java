package net.craftgalaxy.mavic.util.java;

import org.bukkit.ChatColor;

public class StringUtil {

	public static final String PLAYER_ONLY = ChatColor.RED + "You must be a player to run this command.";
	public static final String INSUFFICIENT_PERMISSION = ChatColor.RED + "You to not have permission to run this command.";
	public static final String ERROR_GETTING_PLAYERDATA = ChatColor.RED + "An error occurred while retrieving your player data.";

	public static final String NOTIFY_ALERT_PERMISSION = "mavic.alerts.notify";
	public static final String TOGGLE_ALERT_PERMISSION = "mavic.alerts.toggle";
	public static final String ENABLE_CHECK_PERMISSION = "mavic.checks.enable";
	public static final String DISABLE_CHECK_PERMISSION = "mavic.checks.disable";
}
