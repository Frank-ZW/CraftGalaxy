package net.craftgalaxy.galaxycore.bungee.command;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.leoko.advancedban.manager.PunishmentManager;
import net.craftgalaxy.galaxycore.bungee.BungeePlugin;
import net.craftgalaxy.galaxycore.bungee.data.PlayerData;
import net.craftgalaxy.galaxycore.bungee.data.manager.PlayerManager;
import net.craftgalaxy.galaxycore.bungee.database.DatabaseManager;
import net.craftgalaxy.galaxycore.bungee.util.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.util.CaseInsensitiveSet;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class AltsCommand extends Command {

	private final BungeePlugin plugin;
	private final TaskScheduler scheduler;
	private final Gson gson = new Gson();

	public AltsCommand(BungeePlugin plugin) {
		super("alts", StringUtil.ALTS_COMMAND_PERMISSION);
		this.plugin = plugin;
		this.scheduler = plugin.getProxy().getScheduler();
		this.setPermissionMessage(ChatColor.RED + "You do not have permission to run this command.");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "To view a player's alternate accounts, type /alts <player>"));
		}

		Runnable runnable = () -> {
			String address = null;
			ProxiedPlayer player = this.plugin.getProxy().getPlayer(args[0]);
			if (player == null) {
				Connection connection = DatabaseManager.getInstance().getConnection();
				if (connection == null) {
					sender.sendMessage(new TextComponent(ChatColor.RED + "An error occurred while retrieving the connection to the database."));
					throw new IllegalStateException("Connection to database cannot be null");
				}

				try {
					PreparedStatement statement = connection.prepareStatement("SELECT username, address_history FROM playerdata");
					ResultSet result = statement.executeQuery();
					Deque<String> addressHistory = null;
					while (result.next()) {
						if (result.getString("username").equalsIgnoreCase(args[0])) {
							addressHistory = this.gson.fromJson(result.getString("address_history"), new TypeToken<LinkedList<String>>() {}.getType());
							break;
						}
					}

					if (addressHistory != null && !addressHistory.isEmpty()) {
						address = addressHistory.getLast();
					}

					if (addressHistory != null) {
						addressHistory.clear();
					}

					statement.close();
					result.close();
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}
			} else {
				PlayerData playerData = PlayerManager.getInstance().getPlayerData(player.getUniqueId());
				if (playerData == null) {
					sender.sendMessage(new TextComponent(ChatColor.RED + player.getName() + " does not have a saved player data to the database."));
					return;
				}

				address = playerData.getAddress();
			}

			if (address == null) {
				sender.sendMessage(new TextComponent(ChatColor.RED + "That player has never joined the server before."));
				return;
			}

			sender.sendMessage(new TextComponent(this.fetchAltAccounts(address)));
		};

		this.scheduler.runAsync(this.plugin, runnable);
	}

	private String fetchAltAccounts(@NotNull String address) {
		Connection connection = DatabaseManager.getInstance().getConnection();
		if (connection == null) {
			throw new IllegalStateException("Connection to database cannot be null");
		}

		Set<String> names = new CaseInsensitiveSet();
		for (PlayerData playerData : PlayerManager.getInstance().getPlayers()) {
			if (playerData.loggedOnPreviously(address)) {
				names.add((PunishmentManager.get().isBanned(String.valueOf(playerData.getUniqueId())) ? ChatColor.RED : ChatColor.GREEN) + playerData.getName());
			}
		}

		try {
			PreparedStatement statement = connection.prepareStatement("SELECT username, unique_id, address_history FROM playerdata");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Deque<String> addressHistory = this.gson.fromJson(result.getString("address_history"), new TypeToken<LinkedList<String>>() {}.getType());
				String name = result.getString("username");
				if (!addressHistory.isEmpty() && addressHistory.getLast().equals(address)) {
					names.add((PunishmentManager.get().isBanned(result.getString("unique_id")) ? ChatColor.RED : ChatColor.GREEN) + name);
				}
			}

			result.close();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return ChatColor.RED + "An invalid connection was generated while retrieving the connection to the database.";
		}

		StringBuilder result = new StringBuilder(ChatColor.GREEN + "The following players have logged on from " + address + " (" + names.size() + "): ");
		switch (names.size()) {
			case 0:
				return result.toString().trim();
			case 1:
				result.append(Iterables.getOnlyElement(names));
				break;
			case 2:
				result.append(Iterables.get(names, 0)).append(ChatColor.GREEN).append(" and ").append(Iterables.get(names, 1));
				break;
			default:
				int index = 0;
				for (String name : names) {
					if (index == names.size() - 1) {
						result.append(ChatColor.GREEN).append("and ").append(name);
					} else {
						result.append(name).append(ChatColor.GREEN).append(", ");
					}

					index++;
				}
		}

		return result.toString().trim();
	}
}
