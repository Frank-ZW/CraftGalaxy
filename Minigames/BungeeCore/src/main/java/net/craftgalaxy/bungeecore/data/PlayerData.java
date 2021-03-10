package net.craftgalaxy.bungeecore.data;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class PlayerData {

	private final ProxiedPlayer player;
	private final String name;
	private final UUID uniqueId;
	private PlayerStatus status;

	public PlayerData(ProxiedPlayer player) {
		this(player, PlayerStatus.INACTIVE);
	}

	public PlayerData(ProxiedPlayer player, PlayerStatus status) {
		this.player = player;
		this.name = player.getName();
		this.uniqueId = player.getUniqueId();
		this.status = status;
	}

	public ProxiedPlayer getPlayer() {
		return this.player;
	}

	public String getName() {
		return this.name;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}

	public void setPlayerStatus(PlayerStatus status) {
		this.status = status;
	}

	public boolean isPlaying() {
		return this.status == PlayerStatus.PLAYING;
	}

	public boolean isQueuing() {
		return this.status == PlayerStatus.QUEUING;
	}

	public boolean isSpectating() {
		return this.status == PlayerStatus.SPECTATING;
	}

	public enum PlayerStatus {
		PLAYING,
		QUEUING,
		SPECTATING,
		INACTIVE
	}

	@Override
	public int hashCode() {
		return 13 + this.uniqueId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof PlayerData)) {
			return false;
		}

		PlayerData o = (PlayerData) obj;
		return this.uniqueId.equals(o.getUniqueId());
	}
}
