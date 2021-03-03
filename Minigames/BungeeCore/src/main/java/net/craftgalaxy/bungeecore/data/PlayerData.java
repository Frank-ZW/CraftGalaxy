package net.craftgalaxy.bungeecore.data;

import net.craftgalaxy.bungeecore.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class PlayerData {

	private final ProxiedPlayer player;
	private final String name;
	private final UUID uniqueId;

	private boolean playing;
	private boolean queuing;
	private boolean spectating;

	public PlayerData(ProxiedPlayer player) {
		this.player = player;
		this.name = player.getName();
		this.uniqueId = player.getUniqueId();
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

	public boolean isPlaying() {
		return this.playing;
	}

	public void setPlayerStatus(boolean playing, boolean queuing, boolean spectating) {
		this.playing = playing;
		this.queuing = queuing;
		this.spectating = spectating;
		BungeeCore.getInstance().getLogger().info(ChatColor.GREEN + this.player.getName() + " had 'playing' set to '" + this.playing + "', 'queuing' set to '" + this.queuing + "', and 'spectating' set to '" + this.spectating + "'.");
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public boolean isQueuing() {
		return this.queuing;
	}

	public void setQueuing(boolean queuing) {
		this.queuing = queuing;
	}

	public boolean isSpectating() {
		return this.spectating;
	}

	public void setSpectating(boolean spectating) {
		this.spectating = spectating;
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
