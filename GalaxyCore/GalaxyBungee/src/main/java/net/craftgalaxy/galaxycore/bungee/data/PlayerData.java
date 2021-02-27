package net.craftgalaxy.galaxycore.bungee.data;

import net.craftgalaxy.corepackets.client.CPacketPlayerFreeze;
import net.craftgalaxy.corepackets.client.CPacketPasswordRequest;
import net.craftgalaxy.galaxycore.bungee.BungeePlugin;
import net.craftgalaxy.galaxycore.bungee.data.manager.ServerManager;
import net.craftgalaxy.galaxycore.bungee.util.StringUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

public class PlayerData {

	private final BungeePlugin plugin;
	private final String name;
	private final UUID uniqueId;
	private final InetSocketAddress socketAddress;
	private final Deque<String> addressHistory;
	private final String password;
	private final String address;
	private ProxiedPlayer player;
	private boolean frozen;

	public PlayerData(String name, UUID uniqueId, InetSocketAddress socketAddress) {
		this(name, uniqueId, socketAddress, null);
	}

	public PlayerData(String name, UUID uniqueId, InetSocketAddress socketAddress, String password) {
		this(name, uniqueId, socketAddress, password, new LinkedList<>());
	}

	public PlayerData(String name, UUID uniqueId, InetSocketAddress socketAddress, String password, Deque<String> addressHistory) {
		this.plugin = BungeePlugin.getInstance();
		this.name = name;
		this.uniqueId = uniqueId;
		this.password = password;
		this.socketAddress = socketAddress;
		this.addressHistory = addressHistory;
		this.address = socketAddress.getHostString();
	}

	public String getName() {
		return this.name;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}

	public ProxiedPlayer getPlayer() {
		return this.player;
	}

	public void setPlayer(ProxiedPlayer player) {
		this.player = player;
	}

	public void onPlayerConnect() {
		String address = this.socketAddress.getAddress().getHostAddress();
		if (this.player.hasPermission(StringUtil.AUTHENTICATE_PERMISSION)) {
			if (!this.addressHistory.isEmpty() && !this.addressHistory.contains(address)) {
				this.frozen = true;
				if (!ServerManager.getInstance().sendPacketToServer(this.player.getServer().getInfo().getName(), new CPacketPlayerFreeze(this.uniqueId, true, true))) {
					this.plugin.getLogger().severe("Failed to send freeze packet to " + this.player.getName());
				}
			}

			if (this.password == null) {
				if (!ServerManager.getInstance().sendPacketToServer(this.player.getServer().getInfo().getName(), new CPacketPasswordRequest(this.uniqueId))) {
					this.plugin.getLogger().severe("Failed to send password request packet to " + this.player.getName());
				}
			}
		}
	}

	public boolean isFrozen() {
		return this.frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	public Deque<String> getAddressHistory() {
		return this.addressHistory;
	}

	public String getPassword() {
		return this.password;
	}

	public String getAddress() {
		return this.address;
	}

	public boolean loggedOnPreviously(String address) {
		return this.addressHistory.contains(address);
	}
}
