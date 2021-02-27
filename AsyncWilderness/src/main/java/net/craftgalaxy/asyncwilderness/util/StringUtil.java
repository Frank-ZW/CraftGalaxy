package net.craftgalaxy.asyncwilderness.util;

import org.bukkit.ChatColor;

public class StringUtil {

	public static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Craft" + ChatColor.BLUE + "Galaxy" + ChatColor.DARK_GRAY + "]";
	public static final String PLAYER_ONLY = ChatColor.RED + "You must be a player to run this command.";
	public static final String INSUFFICIENT_PERMISSION = ChatColor.RED + "You do not have permission to run this command.";

	public static final String DONATOR_COOLDOWN_PERMISSION = "asyncwilderness.cooldowns.donator";
	public static final String RELOAD_PERMISSION = "asyncwilderness.commands.reload";
	public static final String WILDERNESS_PERMISSION = "asyncwilderness.commands.rtp";
	public static final String BYPASS_PERMISSION = "asyncwilderness.bypass";
}
