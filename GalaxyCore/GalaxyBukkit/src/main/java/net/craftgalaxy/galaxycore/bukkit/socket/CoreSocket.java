package net.craftgalaxy.galaxycore.bukkit.socket;

import net.craftgalaxy.corepackets.CoreServerboundPacket;
import net.craftgalaxy.corepackets.client.CPacketDisconnectConfirm;
import net.craftgalaxy.corepackets.client.CPacketPlayerFreeze;
import net.craftgalaxy.corepackets.client.CPacketPlayerUnfreeze;
import net.craftgalaxy.corepackets.server.SPacketConnectIdentifier;
import net.craftgalaxy.galaxycore.bukkit.CorePlugin;
import net.craftgalaxy.galaxycore.bukkit.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CoreSocket implements Runnable {

	private final CorePlugin plugin;
	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;

	public CoreSocket(String hostName, int port) {
		this.plugin = CorePlugin.getInstance();
		try {
			this.socket = new Socket(hostName, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPacket(@NotNull CoreServerboundPacket packet) throws IOException {
		this.output.writeObject(packet);
		this.output.flush();
	}

	@Override
	public void run() {
		try {
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.input = new ObjectInputStream(this.socket.getInputStream());
			this.sendPacket(new SPacketConnectIdentifier(this.plugin.getBungeecordName()));
			while (true) {
				Object object = this.input.readObject();
				if (object instanceof CPacketDisconnectConfirm) {
					break;
				} else if (object instanceof CPacketPlayerFreeze) {
					CPacketPlayerFreeze packet = (CPacketPlayerFreeze) object;
					Player player = Bukkit.getPlayer(packet.getUniqueId());
					if (player != null) {
						PlayerManager.getInstance().freezePlayer(player, packet.isAdvancedAlert(), packet.isUnknownAddress());
					}
				} else if (object instanceof CPacketPlayerUnfreeze) {
					CPacketPlayerUnfreeze packet = (CPacketPlayerUnfreeze) object;
					Player player = Bukkit.getPlayer(packet.getUniqueId());
					if (player != null) {
						PlayerManager.getInstance().unfreezePlayer(player, packet.isAdvancedAlert());
					}
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
