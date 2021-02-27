package net.craftgalaxy.galaxycore.bungee.data;

import net.craftgalaxy.corepackets.CoreClientboundPacket;
import net.craftgalaxy.corepackets.client.CPacketDisconnectConfirm;
import net.craftgalaxy.corepackets.server.SPacketConnectIdentifier;
import net.craftgalaxy.corepackets.server.SPacketDisconnectRequest;
import net.craftgalaxy.galaxycore.bungee.BungeePlugin;
import net.craftgalaxy.galaxycore.bungee.data.manager.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class ServerData implements Runnable {

	private final BungeePlugin plugin;
	private final Socket socket;
	private ServerInfo serverInfo;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	public ServerData(Socket socket) {
		this.socket = socket;
		this.plugin = BungeePlugin.getInstance();
	}

	public void connectPlayer(ProxiedPlayer player) {
		player.connect(this.serverInfo);
	}

	public void sendPacket(@NotNull CoreClientboundPacket packet) throws IOException {
		this.output.writeObject(packet);
		this.output.flush();
	}

	public void disconnect() throws IOException {
		this.sendPacket(new CPacketDisconnectConfirm(this.serverInfo == null));
	}

	public void forceDisconnect() throws IOException {
		for (ProxiedPlayer player : this.serverInfo.getPlayers()) {
			player.sendMessage(new TextComponent(ChatColor.GREEN + "The server you were on has shutdown. You have been connected to the fallback server."));
			ServerManager.getInstance().connectFallback(player);
		}

		this.disconnect();
	}

	@SuppressWarnings("InfiniteLoopStatement")
	@Override
	public void run() {
		try {
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.input = new ObjectInputStream(this.socket.getInputStream());
			while (true) {
				Object object = this.input.readObject();
				if (object instanceof SPacketConnectIdentifier) {
					SPacketConnectIdentifier packet = (SPacketConnectIdentifier) object;
					this.serverInfo = this.plugin.getProxy().getServerInfo(packet.getServerName());
					if (this.serverInfo == null) {
						this.disconnect();
					} else {
						ServerManager.getInstance().connectServer(packet.getServerName(), this);
					}
				} else if (object instanceof SPacketDisconnectRequest) {
					SPacketDisconnectRequest packet = (SPacketDisconnectRequest) object;
					ServerManager.getInstance().disconnectServer(this.serverInfo.getName());
					for (UUID uniqueId : packet.getPlayers()) {
						ProxiedPlayer player = this.plugin.getProxy().getPlayer(uniqueId);
						if (player != null) {
							ServerManager.getInstance().connectFallback(player);
						}
					}

					this.disconnect();
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
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
