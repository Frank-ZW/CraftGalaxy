package net.craftgalaxy.minigameservice.bukkit.util;

import org.bukkit.ChatColor;

public class StringUtil {

	public static final String MINIGAME_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Game Chat" + ChatColor.DARK_GRAY + "] ";
	public static final String SPECTATOR_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Spectator Chat" + ChatColor.DARK_GRAY + "] ";
	public static final String LOBBY_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "Lobby Chat" + ChatColor.DARK_GRAY + "] ";

	public static final String PLAYER_ONLY = ChatColor.RED + "You must be a player to run this command.";

	public static final String BACKEND_SERVER_PERMISSION = "minigamecore.developer";
}
