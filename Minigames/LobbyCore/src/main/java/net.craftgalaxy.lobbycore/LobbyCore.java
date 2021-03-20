package net.craftgalaxy.lobbycore;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;
import net.craftgalaxy.minigameservice.packet.impl.client.PacketPlayOutConfirmDisconnect;
import net.craftgalaxy.minigameservice.packet.impl.server.PacketPlayInRequestDisconnect;
import net.craftgalaxy.minigameservice.packet.impl.server.PacketPlayInServerConnect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class LobbyCore extends JavaPlugin {

	private final Map<String, CommandExecutor> commands = Map.of("npcplay", new BPlayCommand(this));
	private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("LobbyCore Socket Executor").build());
	private Future<Boolean> future;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket lobbySocket;
	private String bungeeServerName;
	private String socketHostName;
	private int socketPortNumber;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.readConfig();
		this.registerCommands();
		this.future = this.executor.submit(() -> {
			try {
				this.lobbySocket = new Socket(this.socketHostName, this.socketPortNumber);
				this.output = new ObjectOutputStream(this.lobbySocket.getOutputStream());
				this.input = new ObjectInputStream(this.lobbySocket.getInputStream());
				this.sendPacket(new PacketPlayInServerConnect(this.bungeeServerName));
				while (true) {
					Object object = this.input.readObject();
					if (object instanceof PacketPlayOutConfirmDisconnect) {
						break;
					}
				}
			} catch (Exception e) {
				if (!(e instanceof EOFException)) {
					e.printStackTrace();
				}
			} finally {
				try {
					this.output.close();
					this.input.close();
					this.lobbySocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, true);
	}

	@Override
	public void onDisable() {
		this.executor.shutdown();
		if (this.lobbySocket != null && this.future != null) {
			try {
				this.sendPacket(new PacketPlayInRequestDisconnect(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet())));
				if (this.future.get(10, TimeUnit.SECONDS)) {
					Bukkit.getLogger().info(ChatColor.GREEN + "Successfully disconnected established TCP socket connection with the proxy.");
				}
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				Bukkit.getLogger().log(Level.SEVERE, "An error occurred while interrupting the established TCP socket connection with the proxy", e);
			}
		}

		this.executor.shutdownNow();
	}

	private void registerCommands() {
		for (Map.Entry<String, CommandExecutor> entry : this.commands.entrySet()) {
			PluginCommand command = this.getCommand(entry.getKey());
			if (command != null) {
				command.setExecutor(entry.getValue());
			}
		}
	}

	public void readConfig() {
		try {
			this.bungeeServerName = this.getConfig().getString("socket-settings.bungee-server-name");
			this.socketHostName = this.getConfig().getString("socket-settings.host-name");
			this.socketPortNumber = this.getConfig().getInt("socket-settings.port-number");
		} catch (NumberFormatException e) {
			Bukkit.getLogger().warning("An error occurred while reading in from the config file. Before reloading the plugin, make sure all values entered are the correct data types.");
		}
	}

	public void sendPacket(@NotNull MinigamePacketPlayIn packet) {
		try {
			this.output.writeObject(packet);
			this.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
