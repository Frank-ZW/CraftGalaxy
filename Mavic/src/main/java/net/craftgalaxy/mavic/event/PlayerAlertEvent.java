package net.craftgalaxy.mavic.event;

import net.craftgalaxy.mavic.check.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerAlertEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final Check check;
	private final int ping;
	private final int vl;
	private final String data;
	private boolean cancelled;

	public PlayerAlertEvent(Player player, Check check, int ping, int vl, String data) {
		super(true);
		this.player = player;
		this.check = check;
		this.ping = ping;
		this.vl = vl;
		this.data = data;
		this.cancelled = false;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Check getCheck() {
		return this.check;
	}

	public int getPing() {
		return this.ping;
	}

	public String getData() {
		return this.data;
	}

	public int getVl() {
		return this.vl;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
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
