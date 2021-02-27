package net.craftgalaxy.minigameservice.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class NPCMinigameStartEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final String player;
	private final String minigame;
	private final int maxPlayers;

	public NPCMinigameStartEvent(String player, String minigame, int maxPlayers) {
		super(false);
		this.player = player;
		this.minigame = minigame;
		this.maxPlayers = maxPlayers;
	}

	public String getPlayer() {
		return this.player;
	}

	public String getMinigame() {
		return this.minigame;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
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
