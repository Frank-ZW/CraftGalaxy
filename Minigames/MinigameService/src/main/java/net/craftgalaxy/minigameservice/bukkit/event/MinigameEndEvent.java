package net.craftgalaxy.minigameservice.bukkit.event;

import net.craftgalaxy.minigameservice.bukkit.minigame.AbstractMinigame;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public final class MinigameEndEvent extends MinigameEvent {

	private static final HandlerList handlers = new HandlerList();

	public MinigameEndEvent(AbstractMinigame minigame, Set<UUID> players) {
		super(minigame, players);
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
