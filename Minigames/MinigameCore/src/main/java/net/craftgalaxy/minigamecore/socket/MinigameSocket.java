package net.craftgalaxy.minigamecore.socket;

import net.craftgalaxy.minigamecore.MinigameCore;
import net.craftgalaxy.minigamecore.minigame.MinigameManager;
import net.craftgalaxy.minigameservice.packet.MinigamePacketPlayIn;
import net.craftgalaxy.minigameservice.packet.impl.client.PacketPlayOutConfirmDisconnect;
import net.craftgalaxy.minigameservice.packet.impl.server.PacketPlayInServerConnect;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MinigameSocket implements Runnable {

	private final Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;

	public MinigameSocket() throws IOException {
		this.socket = new Socket(MinigameCore.SOCKET_HOST_NAME, MinigameCore.SOCKET_PORT_NUMBER);
	}

	public void sendPacket(@NotNull MinigamePacketPlayIn packet) throws IOException {
		this.output.writeObject(packet);
		this.output.flush();
	}

	public boolean isConnected() {
		return this.socket.isConnected();
	}

	@Override
	public void run() {
		try {
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
			this.input = new ObjectInputStream(this.socket.getInputStream());
			this.sendPacket(new PacketPlayInServerConnect(MinigameCore.BUNGEE_SERVER_NAME));
			while (true) {
				Object object = input.readObject();
				if (object instanceof PacketPlayOutConfirmDisconnect) {
					break;
				} else {
					MinigameManager.getInstance().handlePacket(object);
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
