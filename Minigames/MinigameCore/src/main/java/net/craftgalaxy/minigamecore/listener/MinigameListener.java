package net.craftgalaxy.minigamecore.listener;

import net.craftgalaxy.minigamecore.minigame.MinigameManager;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameEndEvent;
import net.craftgalaxy.minigameservice.bukkit.event.MinigameStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class MinigameListener implements Listener {

	@EventHandler
	public void onMinigameStart(MinigameStartEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}

	@EventHandler
	public void onMinigameEnd(MinigameEndEvent e) {
		MinigameManager.getInstance().handleEvent(e);
	}
}
