package net.craftgalaxy.mavic.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerBanEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Player player;

	public PlayerBanEvent(Player player) {
		super(true);
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
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
