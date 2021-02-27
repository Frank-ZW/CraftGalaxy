package net.craftgalaxy.minigameservice.bukkit.event;

import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public final class MinigameEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final AbstractMinigame minigame;
	private final Set<UUID> players;

	public MinigameEndEvent(AbstractMinigame minigame, Set<UUID> players) {
		super(false);
		this.minigame = minigame;
		this.players = players;
	}

	public AbstractMinigame getMinigame() {
		return this.minigame;
	}

	public Set<UUID> getPlayers() {
		return this.players;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
