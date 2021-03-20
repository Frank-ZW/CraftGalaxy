package net.craftgalaxy.minigameservice.bukkit.event;

import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import org.bukkit.event.Event;

import java.util.Set;
import java.util.UUID;

public abstract class MinigameEvent extends Event {

	protected AbstractMinigame minigame;
	protected int gameKey;
	protected Set<UUID> players;

	public MinigameEvent(AbstractMinigame minigame, Set<UUID> players) {
		super(false);
		this.minigame = minigame;
		this.gameKey = minigame.getGameKey();
		this.players = players;
	}

	public int getGameKey() {
		return this.gameKey;
	}

	public AbstractMinigame getMinigame() {
		return this.minigame;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}
}
