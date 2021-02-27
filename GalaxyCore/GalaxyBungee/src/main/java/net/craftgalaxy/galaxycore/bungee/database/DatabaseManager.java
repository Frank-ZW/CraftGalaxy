package net.craftgalaxy.galaxycore.bungee.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.craftgalaxy.galaxycore.bungee.BungeePlugin;
import net.craftgalaxy.galaxycore.bungee.data.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

public class DatabaseManager {

	private BungeePlugin plugin;
	private Gson gson = new Gson();
	private Connection connection;
	private static DatabaseManager instance;

	public DatabaseManager(BungeePlugin plugin) {
		this.plugin = plugin;
		this.loadDatabase();
	}

	public static void enable(BungeePlugin plugin) {
		instance = new DatabaseManager(plugin);
	}

	public static void disable() {
		if (instance == null) {
			return;
		}

		try {
			if (instance.connection != null) {
				if (!instance.connection.isClosed()) {
					instance.connection.close();
				}

				instance.connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		instance.plugin = null;
		instance.gson = null;
		instance = null;
	}

	public static DatabaseManager getInstance() {
		return instance;
	}

	@Nullable
	public Connection getConnection() {
		try {
			File databaseFile = new File(this.plugin.getDataFolder(), "database.db");
			if (!databaseFile.exists()) {
				if (!databaseFile.createNewFile()) {
					this.plugin.getLogger().severe("Failed to create new SQLite database file... Player data will not be stored properly.");
				}
			}

			if (this.connection != null && !this.connection.isClosed()) {
				return this.connection;
			}

			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
			return this.connection;
		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void loadDatabase() {
		this.connection = this.getConnection();
		if (this.connection == null) {
			throw new IllegalStateException("Connection to database cannot be null");
		}

		Statement createTableStatement;
		try {
			createTableStatement = this.connection.createStatement();
			createTableStatement.executeUpdate("CREATE TABLE IF NOT EXISTS playerdata (unique_id MESSAGE_TEXT NOT NULL, username MESSAGE_TEXT NOT NULL, address_history MESSAGE_TEXT, password MESSAGE_TEXT)");
			createTableStatement.close();
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public PlayerData fetchPlayerData(String name, UUID uniqueId, InetSocketAddress socketAddress) {
		this.connection = this.getConnection();
		if (this.connection == null) {
			throw new IllegalStateException("Connection to database cannot be null");
		}

		PlayerData playerData = null;
		try {
			PreparedStatement statement = this.connection.prepareStatement("SELECT username, address_history, password FROM playerdata WHERE unique_id = ?");
			statement.setString(1, String.valueOf(uniqueId));
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				if (result.getString("unique_id").equals(String.valueOf(uniqueId))) {
					Deque<String> addressHistory = this.gson.fromJson(result.getString("address_history"), new TypeToken<LinkedList<String>>() {}.getType());
					String password = result.getString("password");
					playerData = new PlayerData(name, uniqueId, socketAddress, password, addressHistory);
					break;
				}
			}

			statement.close();
			result.close();
			this.connection.close();
			this.connection = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return playerData == null ? new PlayerData(name, uniqueId, socketAddress) : playerData;
	}

	public void writePlayerData(@NotNull PlayerData playerData) {
		this.connection = this.getConnection();
		if (this.connection == null) {
			throw new IllegalStateException("Connection to database cannot be null");
		}

		try {
			PreparedStatement statement = this.connection.prepareStatement("SELECT count(*) AS playercount FROM playerdata WHERE unique_id = ?");
			statement.setString(1, String.valueOf(playerData.getUniqueId()));
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				int count = result.getInt("playercount");
				if (count == 1) {
					statement = this.connection.prepareStatement("UPDATE playerdata SET username = ?, password = ?, address_history = ?, WHERE unique_id = ?");
					statement.setString(1, playerData.getName());
					statement.setString(2, playerData.getPassword());
					statement.setString(3, this.gson.toJson(playerData.getAddressHistory()));
					statement.setString(4, String.valueOf(playerData.getUniqueId()));
					statement.executeUpdate();
					result.close();
					statement.close();
					this.connection.close();
					this.connection = null;
					return;
				}
			}

			statement = this.connection.prepareStatement("INSERT INTO playerdata (unique_id, username, password, address_history) VALUES (?, ?, ?, ?)");
			statement.setString(1, String.valueOf(playerData.getUniqueId()));
			statement.setString(2, playerData.getName());
			statement.setString(3, playerData.getPassword());
			statement.setString(4, this.gson.toJson(playerData.getAddressHistory()));
			statement.executeUpdate();
			result.close();
			statement.close();
			this.connection.close();
			this.connection = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
